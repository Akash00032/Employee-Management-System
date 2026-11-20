import { useMemo, useState } from 'react'
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { zodResolver } from '@hookform/resolvers/zod'
import { Pencil, Plus, Search, Trash2 } from 'lucide-react'
import { useForm } from 'react-hook-form'
import { z } from 'zod'
import { ConfirmDialog } from '../../components/common/ConfirmDialog'
import { EmptyState, ErrorState, LoadingState } from '../../components/common/Feedback'
import { Modal } from '../../components/common/Modal'
import { PageHeader } from '../../components/common/PageHeader'
import { useToast } from '../../components/common/ToastProvider'
import { getApiErrorMessage, getValidationErrors } from '../../services/api'
import { organizationService } from '../../services/organizationService'
import type { LeaveType, LeaveTypeRequest } from '../../types/api'

const schema = z.object({
  code: z.string().trim().min(1, 'Leave type code is required').max(20),
  name: z.string().trim().min(1, 'Leave type name is required').max(100),
  maxDays: z.string().min(1, 'Maximum days are required').transform(Number).refine((value) => Number.isInteger(value) && value > 0, 'Enter a positive whole number'),
  paidLeave: z.boolean(),
  active: z.boolean(),
  description: z.string().max(500),
})
type FormInput = z.input<typeof schema>
type FormValues = z.output<typeof schema>

export function LeaveTypesPage() {
  const [search, setSearch] = useState('')
  const [editing, setEditing] = useState<LeaveType | null>(null)
  const [dialogOpen, setDialogOpen] = useState(false)
  const [deleting, setDeleting] = useState<LeaveType | null>(null)
  const client = useQueryClient()
  const notify = useToast()
  const query = useQuery({ queryKey: ['leave-types'], queryFn: organizationService.leaveTypes })
  const { register, handleSubmit, reset, setError, formState: { errors } } = useForm<FormInput, unknown, FormValues>({
    resolver: zodResolver(schema),
    defaultValues: { code: '', name: '', maxDays: '1', paidLeave: true, active: true, description: '' },
  })
  const rows = useMemo(() => {
    const term = search.trim().toLowerCase()
    return (query.data ?? []).filter((type) => !term || `${type.code} ${type.name} ${type.description ?? ''}`.toLowerCase().includes(term))
  }, [query.data, search])
  const close = () => {
    setDialogOpen(false)
    setEditing(null)
    reset({ code: '', name: '', maxDays: '1', paidLeave: true, active: true, description: '' })
  }
  const openEdit = (type: LeaveType) => {
    setEditing(type)
    reset({ code: type.code, name: type.name, maxDays: type.maxDays.toString(), paidLeave: type.paidLeave, active: type.active, description: type.description ?? '' })
    setDialogOpen(true)
  }
  const save = useMutation({
    mutationFn: (request: LeaveTypeRequest) => editing ? organizationService.updateLeaveType(editing.id, request) : organizationService.createLeaveType(request),
    onSuccess: async () => {
      await client.invalidateQueries({ queryKey: ['leave-types'] })
      notify(editing ? 'Leave type updated.' : 'Leave type created.')
      close()
    },
    onError: (error) => {
      const validation = getValidationErrors(error)
      let mapped = false
      for (const field of ['code', 'name', 'maxDays', 'paidLeave', 'active', 'description'] as const) {
        if (validation[field]) { setError(field, { message: validation[field] }); mapped = true }
      }
      if (!mapped) notify(getApiErrorMessage(error), 'error')
    },
  })
  const remove = useMutation({
    mutationFn: (type: LeaveType) => organizationService.deleteLeaveType(type.id),
    onSuccess: async () => {
      await client.invalidateQueries({ queryKey: ['leave-types'] })
      notify('Leave type deleted.')
      setDeleting(null)
    },
    onError: (error) => notify(getApiErrorMessage(error), 'error'),
  })
  const submit = handleSubmit((values) => save.mutate({
    code: values.code.trim(),
    name: values.name.trim(),
    maxDays: values.maxDays,
    paidLeave: values.paidLeave,
    active: values.active,
    description: values.description.trim() || null,
  }))

  return (
    <div className="page-stack">
      <PageHeader eyebrow="LEAVE MANAGEMENT" title="Leave types" description="Configure the leave categories available to your organization." action={<button className="button button-primary" onClick={() => { setEditing(null); reset({ code: '', name: '', maxDays: '1', paidLeave: true, active: true, description: '' }); setDialogOpen(true) }}><Plus size={17} /> Add leave type</button>} />
      <section className="panel directory-panel">
        <div className="directory-toolbar"><label className="table-search"><Search size={17} /><input value={search} onChange={(event) => setSearch(event.target.value)} placeholder="Search leave types" aria-label="Search leave types" />{search && <button className="text-button" onClick={() => setSearch('')}>Clear</button>}</label><span className="result-count">{rows.length} {rows.length === 1 ? 'type' : 'types'}</span></div>
        {query.isLoading && <LoadingState label="Loading leave types" />}
        {query.isError && <ErrorState message={getApiErrorMessage(query.error)} retry={() => void query.refetch()} />}
        {query.data && rows.length === 0 && <EmptyState title={search ? 'No leave types found' : 'No leave types yet'} description={search ? 'Try another search.' : 'Create a leave type to make it available for leave requests.'} />}
        {rows.length > 0 && <div className="table-scroll"><table className="data-table">
          <thead><tr><th>LEAVE TYPE</th><th>ALLOWED DAYS</th><th>PAY</th><th>STATUS</th><th>DESCRIPTION</th><th className="actions-heading">ACTIONS</th></tr></thead>
          <tbody>{rows.map((type) => <tr key={type.id}>
            <td><div className="resource-name"><span className="resource-icon resource-icon-green"><span className="leave-type-mark">{type.code.slice(0, 2)}</span></span><span><strong>{type.name}</strong><small>{type.code}</small></span></div></td>
            <td>{type.maxDays} days</td>
            <td>{type.paidLeave ? 'Paid' : 'Unpaid'}</td>
            <td><span className={`status-badge ${type.active ? 'status-active' : 'status-default'}`}><span className="status-dot" />{type.active ? 'Active' : 'Inactive'}</span></td>
            <td><span className="description-cell">{type.description || '—'}</span></td>
            <td><div className="row-actions"><button className="icon-button row-action" onClick={() => openEdit(type)} aria-label={`Edit ${type.name}`} title="Edit leave type"><Pencil size={16} /></button><button className="icon-button row-action row-action-danger" onClick={() => setDeleting(type)} aria-label={`Delete ${type.name}`} title="Delete leave type"><Trash2 size={16} /></button></div></td>
          </tr>)}</tbody>
        </table></div>}
      </section>
      <Modal open={dialogOpen} title={editing ? 'Edit leave type' : 'Create leave type'} onClose={close}>
        <form className="modal-form" onSubmit={(event) => void submit(event)} noValidate>
          <div className="form-grid"><Field label="Code" error={errors.code?.message}><input className="text-input" {...register('code')} /></Field><Field label="Name" error={errors.name?.message}><input className="text-input" {...register('name')} /></Field><Field label="Maximum days" error={errors.maxDays?.message}><input className="text-input" type="number" min="1" step="1" {...register('maxDays')} /></Field></div>
          <Field label="Description" error={errors.description?.message}><textarea className="text-input text-area" rows={3} {...register('description')} /></Field>
          <label className="toggle-field"><input type="checkbox" {...register('paidLeave')} /><span>Paid leave</span></label>
          <label className="toggle-field"><input type="checkbox" {...register('active')} /><span>Available for new requests</span></label>
          {save.isError && !Object.keys(getValidationErrors(save.error)).length && <div className="form-alert" role="alert">{getApiErrorMessage(save.error)}</div>}
          <div className="dialog-actions"><button type="button" className="button button-secondary" onClick={close} disabled={save.isPending}>Cancel</button><button className="button button-primary" disabled={save.isPending}>{save.isPending ? 'Saving…' : editing ? 'Save changes' : 'Create leave type'}</button></div>
        </form>
      </Modal>
      <ConfirmDialog open={Boolean(deleting)} title="Delete leave type?" description={`Delete ${deleting?.name ?? 'this leave type'}? Existing requests may prevent deletion.`} busy={remove.isPending} onCancel={() => setDeleting(null)} onConfirm={() => deleting && remove.mutate(deleting)} />
    </div>
  )
}

function Field({ label, error, children }: { label: string; error?: string; children: React.ReactNode }) {
  return <label className="form-field"><span className="field-label">{label}</span>{children}{error && <span className="field-error">{error}</span>}</label>
}
