import { useMemo, useState } from 'react'
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { zodResolver } from '@hookform/resolvers/zod'
import { CalendarDays, Clock3, Pencil, Plus, Search, Trash2 } from 'lucide-react'
import { useForm } from 'react-hook-form'
import { z } from 'zod'
import { ConfirmDialog } from '../../components/common/ConfirmDialog'
import { EmptyState, ErrorState, LoadingState } from '../../components/common/Feedback'
import { Modal } from '../../components/common/Modal'
import { PageHeader } from '../../components/common/PageHeader'
import { StatusBadge } from '../../components/common/StatusBadge'
import { useToast } from '../../components/common/ToastProvider'
import { useAuth } from '../../context/AuthContext'
import { attendanceService } from '../../services/attendanceService'
import { employeeService } from '../../services/employeeService'
import { getApiErrorMessage, getValidationErrors } from '../../services/api'
import type { Attendance, AttendanceRequest } from '../../types/api'

const schema = z.object({
  employeeId: z.string().min(1, 'Choose an employee').transform(Number).refine(Number.isInteger, 'Choose an employee'),
  attendanceDate: z.string().min(1, 'Attendance date is required'),
  checkIn: z.string(),
  checkOut: z.string(),
  status: z.enum(['PRESENT', 'ABSENT', 'HALF_DAY', 'LATE', 'WORK_FROM_HOME', 'ON_LEAVE']),
  remarks: z.string().max(500),
})
type FormValues = z.output<typeof schema>
type FormInput = z.input<typeof schema>
const statusValues = ['PRESENT', 'ABSENT', 'HALF_DAY', 'LATE', 'WORK_FROM_HOME', 'ON_LEAVE'] as const
const localDate = (value = new Date()) => `${value.getFullYear()}-${String(value.getMonth() + 1).padStart(2, '0')}-${String(value.getDate()).padStart(2, '0')}`
const toDateTimeInput = (value: string | null) => value?.slice(0, 16) ?? ''
const formatTime = (value: string | null) => value ? new Intl.DateTimeFormat(undefined, { hour: 'numeric', minute: '2-digit' }).format(new Date(value)) : '—'

export function AttendancePage() {
  const { hasAnyRole } = useAuth()
  const canManage = hasAnyRole('ADMIN', 'HR')
  const isManager = hasAnyRole('MANAGER')
  const [search, setSearch] = useState('')
  const [date, setDate] = useState('')
  const [status, setStatus] = useState('')
  const [editing, setEditing] = useState<Attendance | null>(null)
  const [dialogOpen, setDialogOpen] = useState(false)
  const [deleting, setDeleting] = useState<Attendance | null>(null)
  const queryClient = useQueryClient()
  const notify = useToast()
  const attendanceQuery = useQuery({
    queryKey: ['attendance', { teamOnly: isManager }],
    queryFn: () => attendanceService.list(isManager),
  })
  const employeesQuery = useQuery({
    queryKey: ['employees', { keyword: '' }],
    queryFn: () => employeeService.list(),
    enabled: canManage,
  })
  const { register, handleSubmit, reset, setError, formState: { errors } } = useForm<FormInput, unknown, FormValues>({
    resolver: zodResolver(schema),
    defaultValues: { employeeId: '', attendanceDate: localDate(), checkIn: '', checkOut: '', status: 'PRESENT', remarks: '' },
  })

  const rows = useMemo(() => {
    const term = search.trim().toLowerCase()
    return (attendanceQuery.data ?? []).filter((record) => {
      return (!date || record.attendanceDate === date)
        && (!status || record.status === status)
        && (!term || `${record.employeeName} ${record.employeeCode} ${record.remarks ?? ''}`.toLowerCase().includes(term))
    }).sort((left, right) => right.attendanceDate.localeCompare(left.attendanceDate))
  }, [attendanceQuery.data, date, search, status])

  const closeForm = () => {
    setDialogOpen(false)
    setEditing(null)
    reset({ employeeId: '', attendanceDate: localDate(), checkIn: '', checkOut: '', status: 'PRESENT', remarks: '' })
  }
  const openCreate = () => {
    setEditing(null)
    reset({ employeeId: '', attendanceDate: localDate(), checkIn: '', checkOut: '', status: 'PRESENT', remarks: '' })
    setDialogOpen(true)
  }
  const openEdit = (record: Attendance) => {
    setEditing(record)
    reset({ employeeId: record.employeeId.toString(), attendanceDate: record.attendanceDate, checkIn: toDateTimeInput(record.checkIn), checkOut: toDateTimeInput(record.checkOut), status: record.status, remarks: record.remarks ?? '' })
    setDialogOpen(true)
  }

  const saveMutation = useMutation({
    mutationFn: (request: AttendanceRequest) => editing ? attendanceService.update(editing.id, request) : attendanceService.create(request),
    onSuccess: async () => {
      await queryClient.invalidateQueries({ queryKey: ['attendance'] })
      await queryClient.invalidateQueries({ queryKey: ['dashboard'] })
      notify(editing ? 'Attendance record updated.' : 'Attendance marked.')
      closeForm()
    },
    onError: (error) => {
      const validation = getValidationErrors(error)
      let mapped = false
      for (const field of ['employeeId', 'attendanceDate', 'checkIn', 'checkOut', 'status', 'remarks'] as const) {
        if (validation[field]) {
          setError(field, { message: validation[field] })
          mapped = true
        }
      }
      if (!mapped) notify(getApiErrorMessage(error), 'error')
    },
  })
  const deleteMutation = useMutation({
    mutationFn: (record: Attendance) => attendanceService.remove(record.id),
    onSuccess: async () => {
      await queryClient.invalidateQueries({ queryKey: ['attendance'] })
      await queryClient.invalidateQueries({ queryKey: ['dashboard'] })
      notify('Attendance record deleted.')
      setDeleting(null)
    },
    onError: (error) => notify(getApiErrorMessage(error), 'error'),
  })
  const submit = handleSubmit((values) => saveMutation.mutate({
    employeeId: values.employeeId,
    attendanceDate: values.attendanceDate,
    checkIn: values.checkIn ? `${values.checkIn}:00` : null,
    checkOut: values.checkOut ? `${values.checkOut}:00` : null,
    status: values.status,
    remarks: values.remarks.trim() || null,
  }))

  return (
    <div className="page-stack">
      <PageHeader eyebrow="PEOPLE OPERATIONS" title="Attendance" description={isManager ? 'Review attendance records for your direct reports.' : 'Review attendance records and keep daily time tracking up to date.'} action={canManage ? <button className="button button-primary" onClick={openCreate}><Plus size={17} /> Mark attendance</button> : undefined} />
      <section className="attendance-summary">
        <Summary icon={<CalendarDays size={18} />} label="Records" value={(attendanceQuery.data?.length ?? 0).toLocaleString()} />
        <Summary icon={<Clock3 size={18} />} label="Present" value={(attendanceQuery.data?.filter((entry) => entry.status === 'PRESENT').length ?? 0).toLocaleString()} />
        <Summary icon={<CalendarDays size={18} />} label="On leave" value={(attendanceQuery.data?.filter((entry) => entry.status === 'ON_LEAVE').length ?? 0).toLocaleString()} />
      </section>
      <section className="panel directory-panel">
        <div className="directory-toolbar attendance-toolbar">
          <label className="table-search"><Search size={17} /><input value={search} onChange={(event) => setSearch(event.target.value)} placeholder="Search employee or remarks" aria-label="Search attendance records" />{search && <button className="text-button" onClick={() => setSearch('')}>Clear</button>}</label>
          <div className="directory-tools">
            <label className="filter-control"><span>Date</span><input type="date" value={date} onChange={(event) => setDate(event.target.value)} aria-label="Filter by attendance date" />{date && <button className="text-button" onClick={() => setDate('')}>Clear</button>}</label>
            <label className="filter-control"><span>Status</span><select value={status} onChange={(event) => setStatus(event.target.value)} aria-label="Filter by attendance status"><option value="">All</option>{statusValues.map((value) => <option value={value} key={value}>{value.replaceAll('_', ' ').toLowerCase()}</option>)}</select></label>
          </div>
        </div>
        {attendanceQuery.isLoading && <LoadingState label="Loading attendance records" />}
        {attendanceQuery.isError && <ErrorState message={getApiErrorMessage(attendanceQuery.error)} retry={() => void attendanceQuery.refetch()} />}
        {attendanceQuery.data && rows.length === 0 && <EmptyState title={attendanceQuery.data.length ? 'No records match these filters' : 'No attendance records yet'} description={attendanceQuery.data.length ? 'Adjust the date, status, or search terms.' : 'Attendance records will appear here when marked.'} />}
        {rows.length > 0 && (
          <div className="table-scroll">
            <table className="data-table attendance-table">
              <thead><tr><th>EMPLOYEE</th><th>DATE</th><th>CHECK IN</th><th>CHECK OUT</th><th>HOURS</th><th>STATUS</th><th className="actions-heading">ACTIONS</th></tr></thead>
              <tbody>{rows.map((record) => (
                <tr key={record.id}>
                  <td><div className="employee-cell"><span className="avatar avatar-soft">{record.employeeName.split(' ').map((part) => part[0]).slice(0, 2).join('')}</span><span><strong>{record.employeeName}</strong><small>{record.employeeCode}</small></span></div></td>
                  <td>{new Intl.DateTimeFormat(undefined, { dateStyle: 'medium' }).format(new Date(`${record.attendanceDate}T00:00:00`))}</td>
                  <td>{formatTime(record.checkIn)}</td><td>{formatTime(record.checkOut)}</td>
                  <td>{record.workingHours == null ? '—' : `${record.workingHours.toFixed(1)} h`}</td><td><StatusBadge status={record.status} /></td>
                  <td><div className="row-actions">{canManage && <><button className="icon-button row-action" onClick={() => openEdit(record)} aria-label={`Edit attendance for ${record.employeeName}`} title="Edit record"><Pencil size={16} /></button><button className="icon-button row-action row-action-danger" onClick={() => setDeleting(record)} aria-label={`Delete attendance for ${record.employeeName}`} title="Delete record"><Trash2 size={16} /></button></>}</div></td>
                </tr>
              ))}</tbody>
            </table>
          </div>
        )}
      </section>
      {canManage && <div className="directory-note"><span>i</span> The attendance API returns an unpaginated list. Date, employee and status filtering on this screen is applied to that response.</div>}
      {canManage && <Modal open={dialogOpen} title={editing ? 'Edit attendance' : 'Mark attendance'} onClose={closeForm}>
        <form className="modal-form" onSubmit={(event) => void submit(event)} noValidate>
          <Field label="Employee" error={errors.employeeId?.message}><select className="text-input" {...register('employeeId')}><option value="">Choose an employee</option>{(employeesQuery.data ?? []).map((employee) => <option key={employee.id} value={employee.id}>{employee.fullName} · {employee.employeeCode}</option>)}</select></Field>
          <Field label="Attendance date" error={errors.attendanceDate?.message}><input className="text-input" type="date" {...register('attendanceDate')} /></Field>
          <div className="form-grid">
            <Field label="Check-in" error={errors.checkIn?.message}><input className="text-input" type="datetime-local" {...register('checkIn')} /></Field>
            <Field label="Check-out" error={errors.checkOut?.message}><input className="text-input" type="datetime-local" {...register('checkOut')} /></Field>
          </div>
          <Field label="Status" error={errors.status?.message}><select className="text-input" {...register('status')}>{statusValues.map((value) => <option key={value} value={value}>{value.replaceAll('_', ' ').toLowerCase()}</option>)}</select></Field>
          <Field label="Remarks" error={errors.remarks?.message}><textarea className="text-input text-area" rows={3} {...register('remarks')} /></Field>
          {saveMutation.isError && !Object.keys(getValidationErrors(saveMutation.error)).length && <div className="form-alert" role="alert">{getApiErrorMessage(saveMutation.error)}</div>}
          <div className="dialog-actions"><button type="button" className="button button-secondary" onClick={closeForm} disabled={saveMutation.isPending}>Cancel</button><button className="button button-primary" disabled={saveMutation.isPending || employeesQuery.isLoading}>{saveMutation.isPending ? 'Saving…' : editing ? 'Save changes' : 'Mark attendance'}</button></div>
        </form>
      </Modal>}
      {canManage && <ConfirmDialog open={Boolean(deleting)} title="Delete attendance record?" description={`Delete the attendance record for ${deleting?.employeeName ?? 'this employee'}? This action cannot be undone.`} busy={deleteMutation.isPending} onCancel={() => setDeleting(null)} onConfirm={() => deleting && deleteMutation.mutate(deleting)} />}
    </div>
  )
}

function Summary({ icon, label, value }: { icon: React.ReactNode; label: string; value: string }) {
  return <article className="panel attendance-stat"><span>{icon}</span><div><small>{label}</small><strong>{value}</strong></div></article>
}
function Field({ label, error, children }: { label: string; error?: string; children: React.ReactNode }) {
  return <label className="form-field"><span className="field-label">{label}</span>{children}{error && <span className="field-error">{error}</span>}</label>
}
