import { useMemo, useState } from 'react'
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { zodResolver } from '@hookform/resolvers/zod'
import { Clock3, Pencil, Plus, Search, Sun, Trash2 } from 'lucide-react'
import { useForm } from 'react-hook-form'
import { z } from 'zod'
import { ConfirmDialog } from '../../components/common/ConfirmDialog'
import { EmptyState, ErrorState, LoadingState } from '../../components/common/Feedback'
import { Modal } from '../../components/common/Modal'
import { PageHeader } from '../../components/common/PageHeader'
import { useToast } from '../../components/common/ToastProvider'
import { getApiErrorMessage, getValidationErrors } from '../../services/api'
import { organizationService } from '../../services/organizationService'
import type { WorkShift, WorkShiftRequest } from '../../types/api'

const schema = z.object({
  name: z.string().trim().min(2, 'Shift name must be at least 2 characters').max(100),
  startTime: z.string().min(1, 'Start time is required'),
  endTime: z.string().min(1, 'End time is required'),
  breakStartTime: z.string(),
  breakEndTime: z.string(),
  overnight: z.boolean(),
  active: z.boolean(),
})
type FormValues = z.infer<typeof schema>
const toTimeInput = (value: string | null) => value?.slice(0, 5) ?? ''
const formatTime = (value: string) => new Intl.DateTimeFormat(undefined, { hour: 'numeric', minute: '2-digit' }).format(new Date(`1970-01-01T${value.slice(0, 5)}:00`))

export function WorkShiftPage() {
  const [search, setSearch] = useState('')
  const [editing, setEditing] = useState<WorkShift | null>(null)
  const [dialogOpen, setDialogOpen] = useState(false)
  const [deleting, setDeleting] = useState<WorkShift | null>(null)
  const queryClient = useQueryClient()
  const notify = useToast()
  const shiftsQuery = useQuery({ queryKey: ['work-shifts', 'all'], queryFn: organizationService.allWorkShifts })
  const { register, handleSubmit, reset, setError, formState: { errors } } = useForm<FormValues>({
    resolver: zodResolver(schema),
    defaultValues: { name: '', startTime: '', endTime: '', breakStartTime: '', breakEndTime: '', overnight: false, active: true },
  })
  const visible = useMemo(() => {
    const term = search.trim().toLowerCase()
    return (shiftsQuery.data ?? []).filter((shift) => !term || shift.name.toLowerCase().includes(term))
  }, [shiftsQuery.data, search])

  const closeForm = () => {
    setDialogOpen(false)
    setEditing(null)
    reset({ name: '', startTime: '', endTime: '', breakStartTime: '', breakEndTime: '', overnight: false, active: true })
  }
  const openCreate = () => {
    setEditing(null)
    reset({ name: '', startTime: '', endTime: '', breakStartTime: '', breakEndTime: '', overnight: false, active: true })
    setDialogOpen(true)
  }
  const openEdit = (shift: WorkShift) => {
    setEditing(shift)
    reset({ name: shift.name, startTime: toTimeInput(shift.startTime), endTime: toTimeInput(shift.endTime), breakStartTime: toTimeInput(shift.breakStartTime), breakEndTime: toTimeInput(shift.breakEndTime), overnight: shift.overnight, active: shift.active })
    setDialogOpen(true)
  }

  const saveMutation = useMutation({
    mutationFn: (request: WorkShiftRequest) => editing
      ? organizationService.updateWorkShift(editing.id, request)
      : organizationService.createWorkShift(request),
    onSuccess: async () => {
      await queryClient.invalidateQueries({ queryKey: ['work-shifts'] })
      notify(editing ? 'Work shift details updated.' : 'Work shift created.')
      closeForm()
    },
    onError: (error) => {
      const validation = getValidationErrors(error)
      let mapped = false
      for (const field of ['name', 'startTime', 'endTime', 'breakStartTime', 'breakEndTime', 'overnight', 'active'] as const) {
        if (validation[field]) {
          setError(field, { message: validation[field] })
          mapped = true
        }
      }
      if (!mapped) notify(getApiErrorMessage(error), 'error')
    },
  })
  const deleteMutation = useMutation({
    mutationFn: (shift: WorkShift) => organizationService.deleteWorkShift(shift.id),
    onSuccess: async () => {
      await queryClient.invalidateQueries({ queryKey: ['work-shifts'] })
      notify('Work shift deleted.')
      setDeleting(null)
    },
    onError: (error) => notify(getApiErrorMessage(error), 'error'),
  })

  const submit = handleSubmit((values) => saveMutation.mutate({
    name: values.name.trim(),
    startTime: `${values.startTime}:00`,
    endTime: `${values.endTime}:00`,
    breakStartTime: values.breakStartTime ? `${values.breakStartTime}:00` : null,
    breakEndTime: values.breakEndTime ? `${values.breakEndTime}:00` : null,
    overnight: values.overnight,
    active: values.active,
  }))

  return (
    <div className="page-stack">
      <PageHeader eyebrow="ORGANIZATION" title="Work shifts" description="Keep working hours clear and consistent across your teams." action={<button className="button button-primary" onClick={openCreate}><Plus size={17} /> Add work shift</button>} />
      <section className="panel directory-panel">
        <div className="directory-toolbar">
          <label className="table-search"><Search size={17} /><input value={search} onChange={(event) => setSearch(event.target.value)} placeholder="Search work shifts" aria-label="Search work shifts" />{search && <button className="text-button" onClick={() => setSearch('')}>Clear</button>}</label>
          <span className="result-count">{visible.length} {visible.length === 1 ? 'shift' : 'shifts'}</span>
        </div>
        {shiftsQuery.isLoading && <LoadingState label="Loading work shifts" />}
        {shiftsQuery.isError && <ErrorState message={getApiErrorMessage(shiftsQuery.error)} retry={() => void shiftsQuery.refetch()} />}
        {shiftsQuery.data && visible.length === 0 && <EmptyState title={search ? 'No work shifts found' : 'No work shifts yet'} description={search ? 'Try a different search.' : 'Create a shift to start assigning working hours to employees.'} />}
        {visible.length > 0 && (
          <div className="table-scroll">
            <table className="data-table">
              <thead><tr><th>SHIFT</th><th>WORKING HOURS</th><th>BREAK</th><th>EMPLOYEES</th><th>STATUS</th><th className="actions-heading">ACTIONS</th></tr></thead>
              <tbody>{visible.map((shift) => (
                <tr key={shift.id}>
                  <td><div className="resource-name"><span className="resource-icon resource-icon-amber"><Sun size={17} /></span><span><strong>{shift.name}</strong><small>{shift.overnight ? 'Overnight shift' : 'Standard shift'}</small></span></div></td>
                  <td><span className="hours-cell">{formatTime(shift.startTime)} – {formatTime(shift.endTime)}</span></td>
                  <td><span className="description-cell">{shift.breakStartTime && shift.breakEndTime ? `${formatTime(shift.breakStartTime)} – ${formatTime(shift.breakEndTime)}` : 'No break set'}</span></td>
                  <td><span className="employee-count"><Clock3 size={14} />{shift.employeeCount}</span></td>
                  <td><span className={`status-badge ${shift.active ? 'status-active' : 'status-default'}`}><span className="status-dot" />{shift.active ? 'Active' : 'Inactive'}</span></td>
                  <td><div className="row-actions"><button className="icon-button row-action" onClick={() => openEdit(shift)} aria-label={`Edit ${shift.name}`} title="Edit shift"><Pencil size={16} /></button><button className="icon-button row-action row-action-danger" onClick={() => setDeleting(shift)} aria-label={`Delete ${shift.name}`} title="Delete shift"><Trash2 size={16} /></button></div></td>
                </tr>
              ))}</tbody>
            </table>
          </div>
        )}
      </section>
      <div className="directory-note"><span>i</span> Shift times use your local time zone. The overnight flag is stored as provided; time validity is enforced by the backend.</div>
      <Modal open={dialogOpen} title={editing ? 'Edit work shift' : 'Create work shift'} onClose={closeForm}>
        <form className="modal-form" onSubmit={(event) => void submit(event)} noValidate>
          <Field label="Shift name" error={errors.name?.message}><input className="text-input" placeholder="e.g. Morning shift" {...register('name')} /></Field>
          <div className="form-grid">
            <Field label="Start time" error={errors.startTime?.message}><input className="text-input" type="time" {...register('startTime')} /></Field>
            <Field label="End time" error={errors.endTime?.message}><input className="text-input" type="time" {...register('endTime')} /></Field>
            <Field label="Break starts" error={errors.breakStartTime?.message}><input className="text-input" type="time" {...register('breakStartTime')} /></Field>
            <Field label="Break ends" error={errors.breakEndTime?.message}><input className="text-input" type="time" {...register('breakEndTime')} /></Field>
          </div>
          <label className="toggle-field"><input type="checkbox" {...register('overnight')} /><span>Shift runs overnight</span></label>
          <label className="toggle-field"><input type="checkbox" {...register('active')} /><span>Shift is active</span></label>
          {saveMutation.isError && !Object.keys(getValidationErrors(saveMutation.error)).length && <div className="form-alert" role="alert">{getApiErrorMessage(saveMutation.error)}</div>}
          <div className="dialog-actions"><button type="button" className="button button-secondary" onClick={closeForm} disabled={saveMutation.isPending}>Cancel</button><button className="button button-primary" disabled={saveMutation.isPending}>{saveMutation.isPending ? 'Saving…' : editing ? 'Save changes' : 'Create work shift'}</button></div>
        </form>
      </Modal>
      <ConfirmDialog open={Boolean(deleting)} title="Delete work shift?" description={`Delete ${deleting?.name ?? 'this shift'}? Employees or related records may prevent deletion.`} busy={deleteMutation.isPending} onCancel={() => setDeleting(null)} onConfirm={() => deleting && deleteMutation.mutate(deleting)} />
    </div>
  )
}

function Field({ label, error, children }: { label: string; error?: string; children: React.ReactNode }) {
  return <label className="form-field"><span className="field-label">{label}</span>{children}{error && <span className="field-error">{error}</span>}</label>
}
