import { useMemo, useState } from 'react'
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { zodResolver } from '@hookform/resolvers/zod'
import { Check, ClipboardList, Plus, Search, X } from 'lucide-react'
import { useForm } from 'react-hook-form'
import { Link } from 'react-router-dom'
import { z } from 'zod'
import { ConfirmDialog } from '../../components/common/ConfirmDialog'
import { EmptyState, ErrorState, LoadingState } from '../../components/common/Feedback'
import { Modal } from '../../components/common/Modal'
import { PageHeader } from '../../components/common/PageHeader'
import { StatusBadge } from '../../components/common/StatusBadge'
import { useAuth } from '../../context/AuthContext'
import { useToast } from '../../components/common/ToastProvider'
import { employeeService } from '../../services/employeeService'
import { getApiErrorMessage, getValidationErrors } from '../../services/api'
import { leaveService } from '../../services/leaveService'
import { organizationService } from '../../services/organizationService'
import type { LeaveRequest, LeaveRequestInput, LeaveStatus } from '../../types/api'

const schema = z.object({
  employeeId: z.string().min(1, 'Choose an employee').transform(Number).refine(Number.isInteger, 'Choose an employee'),
  leaveTypeId: z.string().min(1, 'Choose a leave type').transform(Number).refine(Number.isInteger, 'Choose a leave type'),
  startDate: z.string().min(1, 'Start date is required'),
  endDate: z.string().min(1, 'End date is required'),
  reason: z.string().max(1000),
})
type FormValues = z.output<typeof schema>
type FormInput = z.input<typeof schema>
const statuses: Array<'ALL' | LeaveStatus> = ['ALL', 'PENDING', 'APPROVED', 'REJECTED', 'CANCELLED']

export function LeavePage() {
  const [search, setSearch] = useState('')
  const [filter, setFilter] = useState<'ALL' | LeaveStatus>('ALL')
  const [dialogOpen, setDialogOpen] = useState(false)
  const [rejecting, setRejecting] = useState<LeaveRequest | null>(null)
  const [rejectReason, setRejectReason] = useState('')
  const [deleting, setDeleting] = useState<LeaveRequest | null>(null)
  const { hasAnyRole } = useAuth()
  const canCreate = hasAnyRole('ADMIN', 'HR')
  const canApprove = hasAnyRole('ADMIN', 'HR', 'MANAGER')
  const canDelete = hasAnyRole('ADMIN', 'HR')
  const isManager = hasAnyRole('MANAGER')
  const isHr = hasAnyRole('ADMIN', 'HR')
  const queryClient = useQueryClient()
  const notify = useToast()
  const requestsQuery = useQuery({
    queryKey: ['leave-requests', { teamOnly: isManager }],
    queryFn: () => leaveService.requests(isManager),
  })
  const typesQuery = useQuery({ queryKey: ['leave-types'], queryFn: organizationService.leaveTypes, enabled: canCreate })
  const employeesQuery = useQuery({ queryKey: ['employees', { keyword: '' }], queryFn: () => employeeService.list(), enabled: canCreate })
  const { register, handleSubmit, reset, setError, formState: { errors } } = useForm<FormInput, unknown, FormValues>({
    resolver: zodResolver(schema),
    defaultValues: { employeeId: '', leaveTypeId: '', startDate: '', endDate: '', reason: '' },
  })

  const visible = useMemo(() => {
    const term = search.trim().toLowerCase()
    return (requestsQuery.data ?? []).filter((request) =>
      (filter === 'ALL' || request.status === filter)
      && (!term || `${request.employeeName} ${request.employeeCode} ${request.leaveTypeName} ${request.reason ?? ''}`.toLowerCase().includes(term)),
    ).sort((left, right) => right.appliedAt.localeCompare(left.appliedAt))
  }, [filter, requestsQuery.data, search])
  const closeForm = () => {
    setDialogOpen(false)
    reset({ employeeId: '', leaveTypeId: '', startDate: '', endDate: '', reason: '' })
  }
  const invalidate = async () => {
    await queryClient.invalidateQueries({ queryKey: ['leave-requests'] })
    await queryClient.invalidateQueries({ queryKey: ['dashboard'] })
  }

  const createMutation = useMutation({
    mutationFn: (request: LeaveRequestInput) => leaveService.create(request),
    onSuccess: async () => { await invalidate(); notify('Leave request submitted.'); closeForm() },
    onError: (error) => {
      const validation = getValidationErrors(error)
      let mapped = false
      for (const field of ['employeeId', 'leaveTypeId', 'startDate', 'endDate', 'reason'] as const) {
        if (validation[field]) { setError(field, { message: validation[field] }); mapped = true }
      }
      if (!mapped) notify(getApiErrorMessage(error), 'error')
    },
  })
  const approveMutation = useMutation({
    mutationFn: (request: LeaveRequest) => leaveService.approve(request.id, isManager),
    onSuccess: async () => { await invalidate(); notify('Leave request approved.') },
    onError: (error) => notify(getApiErrorMessage(error), 'error'),
  })
  const rejectMutation = useMutation({
    mutationFn: ({ request, reason }: { request: LeaveRequest; reason: string }) => leaveService.reject(request.id, reason, isManager),
    onSuccess: async () => { await invalidate(); notify('Leave request rejected.'); setRejecting(null); setRejectReason('') },
    onError: (error) => notify(getApiErrorMessage(error), 'error'),
  })
  const deleteMutation = useMutation({
    mutationFn: (request: LeaveRequest) => leaveService.remove(request.id),
    onSuccess: async () => { await invalidate(); notify('Leave request cancelled.'); setDeleting(null) },
    onError: (error) => notify(getApiErrorMessage(error), 'error'),
  })
  const submit = handleSubmit((values) => createMutation.mutate({
    employeeId: values.employeeId,
    leaveTypeId: values.leaveTypeId,
    startDate: values.startDate,
    endDate: values.endDate,
    reason: values.reason.trim() || null,
  }))

  return (
    <div className="page-stack">
      <PageHeader eyebrow="PEOPLE OPERATIONS" title="Leave requests" description="Keep time away transparent, organized and thoughtfully managed." action={<div className="header-actions">{isHr && <Link className="button button-secondary" to="/leave/types">Leave types</Link>}{canCreate && <button className="button button-primary" onClick={() => setDialogOpen(true)}><Plus size={17} /> New request</button>}</div>} />
      <section className="leave-overview">
        {(['PENDING', 'APPROVED', 'REJECTED'] as const).map((status) => <article className="panel leave-stat" key={status}><span className={`leave-stat-icon leave-${status.toLowerCase()}`}><ClipboardList size={17} /></span><div><small>{status === 'PENDING' ? 'Awaiting review' : status.toLowerCase()}</small><strong>{requestsQuery.data?.filter((request) => request.status === status).length ?? 0}</strong></div></article>)}
      </section>
      <section className="panel directory-panel">
        <div className="directory-toolbar leave-toolbar">
          <label className="table-search"><Search size={17} /><input value={search} onChange={(event) => setSearch(event.target.value)} placeholder="Search employee, leave type, or reason" aria-label="Search leave requests" />{search && <button className="text-button" onClick={() => setSearch('')}>Clear</button>}</label>
          <label className="filter-control"><span>Status</span><select value={filter} onChange={(event) => setFilter(event.target.value as typeof filter)} aria-label="Filter leave requests">{statuses.map((status) => <option value={status} key={status}>{status === 'ALL' ? 'All requests' : status.toLowerCase()}</option>)}</select></label>
        </div>
        {requestsQuery.isLoading && <LoadingState label="Loading leave requests" />}
        {requestsQuery.isError && <ErrorState message={getApiErrorMessage(requestsQuery.error)} retry={() => void requestsQuery.refetch()} />}
        {requestsQuery.data && visible.length === 0 && <EmptyState title={search || filter !== 'ALL' ? 'No requests match these filters' : 'No leave requests yet'} description={search || filter !== 'ALL' ? 'Try changing your search or status filter.' : 'Leave requests will appear here once submitted.'} />}
        {visible.length > 0 && (
          <div className="table-scroll">
            <table className="data-table leave-table">
              <thead><tr><th>EMPLOYEE</th><th>LEAVE TYPE</th><th>DATES</th><th>DAYS</th><th>STATUS</th><th>APPROVER</th><th className="actions-heading">ACTIONS</th></tr></thead>
              <tbody>{visible.map((request) => (
                <tr key={request.id}>
                  <td><div className="employee-cell"><span className="avatar avatar-soft">{request.employeeName.split(' ').map((part) => part[0]).slice(0, 2).join('')}</span><span><strong>{request.employeeName}</strong><small>{request.employeeCode}</small></span></div></td>
                  <td><strong className="table-primary-text">{request.leaveTypeName}</strong><small className="table-subtext">{request.leaveTypeCode}</small></td>
                  <td><span className="date-range">{formatDate(request.startDate)}<small>to</small>{formatDate(request.endDate)}</span></td>
                  <td>{request.totalDays}</td>
                  <td><StatusBadge status={request.status} />{request.status === 'REJECTED' && request.rejectionReason && <small className="rejection-detail" title={request.rejectionReason}>{request.rejectionReason}</small>}</td>
                  <td>{request.approvedByName ?? '—'}</td>
                  <td><div className="row-actions">{canApprove && request.status === 'PENDING' && <><button className="icon-button row-action action-approve" onClick={() => approveMutation.mutate(request)} disabled={approveMutation.isPending} aria-label={`Approve ${request.employeeName}'s request`} title="Approve"><Check size={16} /></button><button className="icon-button row-action action-reject" onClick={() => { setRejecting(request); setRejectReason('') }} aria-label={`Reject ${request.employeeName}'s request`} title="Reject"><X size={16} /></button></>}{canDelete && request.status === 'PENDING' && <button className="icon-button row-action row-action-danger" onClick={() => setDeleting(request)} aria-label={`Cancel ${request.employeeName}'s request`} title="Cancel request"><X size={16} /></button>}</div></td>
                </tr>
              ))}</tbody>
            </table>
          </div>
        )}
      </section>
      <Modal open={dialogOpen} title="Submit a leave request" onClose={closeForm}>
        <form className="modal-form" onSubmit={(event) => void submit(event)} noValidate>
          <Field label="Employee" error={errors.employeeId?.message}><select className="text-input" {...register('employeeId')}><option value="">Choose an employee</option>{(employeesQuery.data ?? []).map((employee) => <option value={employee.id} key={employee.id}>{employee.fullName} · {employee.employeeCode}</option>)}</select></Field>
          <Field label="Leave type" error={errors.leaveTypeId?.message}><select className="text-input" {...register('leaveTypeId')}><option value="">Choose a leave type</option>{(typesQuery.data ?? []).filter((type) => type.active).map((type) => <option value={type.id} key={type.id}>{type.name} · {type.maxDays} days{type.paidLeave ? ' · paid' : ''}</option>)}</select></Field>
          <div className="form-grid"><Field label="Start date" error={errors.startDate?.message}><input className="text-input" type="date" {...register('startDate')} /></Field><Field label="End date" error={errors.endDate?.message}><input className="text-input" type="date" {...register('endDate')} /></Field></div>
          <Field label="Reason" error={errors.reason?.message}><textarea className="text-input text-area" rows={3} {...register('reason')} /></Field>
          {createMutation.isError && !Object.keys(getValidationErrors(createMutation.error)).length && <div className="form-alert" role="alert">{getApiErrorMessage(createMutation.error)}</div>}
          <div className="dialog-actions"><button type="button" className="button button-secondary" onClick={closeForm} disabled={createMutation.isPending}>Cancel</button><button className="button button-primary" disabled={createMutation.isPending || typesQuery.isLoading || employeesQuery.isLoading}>{createMutation.isPending ? 'Submitting…' : 'Submit request'}</button></div>
        </form>
      </Modal>
      <Modal open={Boolean(rejecting)} title="Reject leave request" onClose={() => setRejecting(null)}>
        <form className="modal-form" onSubmit={(event) => { event.preventDefault(); if (rejecting && rejectReason.trim()) rejectMutation.mutate({ request: rejecting, reason: rejectReason.trim() }) }}>
          <p className="modal-description">Add a brief reason for declining {rejecting?.employeeName ?? 'this request'}’s leave.</p>
          <label className="form-field"><span className="field-label">Reason for rejection</span><textarea className="text-input text-area" rows={3} maxLength={500} required value={rejectReason} onChange={(event) => setRejectReason(event.target.value)} /></label>
          {rejectMutation.isError && <div className="form-alert" role="alert">{getApiErrorMessage(rejectMutation.error)}</div>}
          <div className="dialog-actions"><button type="button" className="button button-secondary" onClick={() => setRejecting(null)} disabled={rejectMutation.isPending}>Cancel</button><button className="button button-danger" disabled={rejectMutation.isPending || !rejectReason.trim()}>{rejectMutation.isPending ? 'Rejecting…' : 'Reject request'}</button></div>
        </form>
      </Modal>
      <ConfirmDialog open={Boolean(deleting)} title="Cancel leave request?" description={`Cancel ${deleting?.employeeName ?? 'this employee'}’s pending leave request?`} busy={deleteMutation.isPending} onCancel={() => setDeleting(null)} onConfirm={() => deleting && deleteMutation.mutate(deleting)} />
    </div>
  )
}

function Field({ label, error, children }: { label: string; error?: string; children: React.ReactNode }) {
  return <label className="form-field"><span className="field-label">{label}</span>{children}{error && <span className="field-error">{error}</span>}</label>
}
function formatDate(value: string) {
  return new Intl.DateTimeFormat(undefined, { month: 'short', day: 'numeric', year: 'numeric' }).format(new Date(`${value}T00:00:00`))
}
