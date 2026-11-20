import { useMemo, useState } from 'react'
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { zodResolver } from '@hookform/resolvers/zod'
import { Building2, Pencil, Plus, Search, Trash2, UsersRound } from 'lucide-react'
import { useForm } from 'react-hook-form'
import { z } from 'zod'
import { ConfirmDialog } from '../../components/common/ConfirmDialog'
import { EmptyState, ErrorState, LoadingState } from '../../components/common/Feedback'
import { Modal } from '../../components/common/Modal'
import { PageHeader } from '../../components/common/PageHeader'
import { useToast } from '../../components/common/ToastProvider'
import { getApiErrorMessage, getValidationErrors } from '../../services/api'
import { organizationService } from '../../services/organizationService'
import type { Department, DepartmentRequest } from '../../types/api'

const schema = z.object({
  code: z.string().trim().min(1, 'Department code is required').max(20),
  name: z.string().trim().min(1, 'Department name is required').max(100),
  description: z.string().max(500, 'Description cannot exceed 500 characters'),
  active: z.boolean(),
})
type FormValues = z.infer<typeof schema>

export function DepartmentPage() {
  const [search, setSearch] = useState('')
  const [editing, setEditing] = useState<Department | null>(null)
  const [dialogOpen, setDialogOpen] = useState(false)
  const [deleting, setDeleting] = useState<Department | null>(null)
  const queryClient = useQueryClient()
  const notify = useToast()
  const departmentsQuery = useQuery({ queryKey: ['departments'], queryFn: organizationService.departments })
  const { register, handleSubmit, reset, setError, formState: { errors } } = useForm<FormValues>({
    resolver: zodResolver(schema),
    defaultValues: { code: '', name: '', description: '', active: true },
  })

  const visible = useMemo(() => {
    const term = search.trim().toLowerCase()
    return (departmentsQuery.data ?? []).filter((department) => !term || `${department.name} ${department.code} ${department.description ?? ''}`.toLowerCase().includes(term))
  }, [departmentsQuery.data, search])

  const closeForm = () => {
    setDialogOpen(false)
    setEditing(null)
    reset({ code: '', name: '', description: '', active: true })
  }
  const openCreate = () => {
    setEditing(null)
    reset({ code: '', name: '', description: '', active: true })
    setDialogOpen(true)
  }
  const openEdit = (department: Department) => {
    setEditing(department)
    reset({ code: department.code, name: department.name, description: department.description ?? '', active: department.active })
    setDialogOpen(true)
  }

  const saveMutation = useMutation({
    mutationFn: (request: DepartmentRequest) => editing
      ? organizationService.updateDepartment(editing.id, request)
      : organizationService.createDepartment(request),
    onSuccess: async () => {
      await queryClient.invalidateQueries({ queryKey: ['departments'] })
      await queryClient.invalidateQueries({ queryKey: ['dashboard'] })
      notify(editing ? 'Department details updated.' : 'Department created.')
      closeForm()
    },
    onError: (error) => {
      const validation = getValidationErrors(error)
      let mapped = false
      for (const field of ['code', 'name', 'description', 'active'] as const) {
        if (validation[field]) {
          setError(field, { message: validation[field] })
          mapped = true
        }
      }
      if (!mapped) notify(getApiErrorMessage(error), 'error')
    },
  })
  const deleteMutation = useMutation({
    mutationFn: (department: Department) => organizationService.deleteDepartment(department.id),
    onSuccess: async () => {
      await queryClient.invalidateQueries({ queryKey: ['departments'] })
      await queryClient.invalidateQueries({ queryKey: ['dashboard'] })
      notify('Department deleted.')
      setDeleting(null)
    },
    onError: (error) => notify(getApiErrorMessage(error), 'error'),
  })

  const submit = handleSubmit((values) => saveMutation.mutate({
    code: values.code.trim(),
    name: values.name.trim(),
    description: values.description.trim() || null,
    active: values.active,
  }))

  return (
    <div className="page-stack">
      <PageHeader eyebrow="ORGANIZATION" title="Departments" description="Organize your people into clear, connected teams." action={<button className="button button-primary" onClick={openCreate}><Plus size={17} /> Add department</button>} />
      <section className="panel directory-panel">
        <div className="directory-toolbar">
          <label className="table-search"><Search size={17} /><input value={search} onChange={(event) => setSearch(event.target.value)} placeholder="Search departments" aria-label="Search departments" />{search && <button className="text-button" onClick={() => setSearch('')}>Clear</button>}</label>
          <span className="result-count">{visible.length} {visible.length === 1 ? 'department' : 'departments'}</span>
        </div>
        {departmentsQuery.isLoading && <LoadingState label="Loading departments" />}
        {departmentsQuery.isError && <ErrorState message={getApiErrorMessage(departmentsQuery.error)} retry={() => void departmentsQuery.refetch()} />}
        {departmentsQuery.data && visible.length === 0 && <EmptyState title={search ? 'No departments found' : 'No departments yet'} description={search ? 'Try a different search.' : 'Create your first department to organize your employees.'} />}
        {visible.length > 0 && (
          <div className="table-scroll">
            <table className="data-table">
              <thead><tr><th>DEPARTMENT</th><th>DESCRIPTION</th><th>EMPLOYEES</th><th>STATUS</th><th className="actions-heading">ACTIONS</th></tr></thead>
              <tbody>{visible.map((department) => (
                <tr key={department.id}>
                  <td><div className="resource-name"><span className="resource-icon resource-icon-green"><Building2 size={17} /></span><span><strong>{department.name}</strong><small>{department.code}</small></span></div></td>
                  <td><span className="description-cell">{department.description || '—'}</span></td>
                  <td><span className="employee-count"><UsersRound size={14} />{department.employeeCount}</span></td>
                  <td><span className={`status-badge ${department.active ? 'status-active' : 'status-default'}`}><span className="status-dot" />{department.active ? 'Active' : 'Inactive'}</span></td>
                  <td><div className="row-actions"><button className="icon-button row-action" onClick={() => openEdit(department)} aria-label={`Edit ${department.name}`} title="Edit department"><Pencil size={16} /></button><button className="icon-button row-action row-action-danger" onClick={() => setDeleting(department)} aria-label={`Delete ${department.name}`} title="Delete department"><Trash2 size={16} /></button></div></td>
                </tr>
              ))}</tbody>
            </table>
          </div>
        )}
      </section>
      <Modal open={dialogOpen} title={editing ? 'Edit department' : 'Create department'} onClose={closeForm}>
        <form className="modal-form" onSubmit={(event) => void submit(event)} noValidate>
          <Field label="Department code" error={errors.code?.message}><input className="text-input" {...register('code')} /></Field>
          <Field label="Department name" error={errors.name?.message}><input className="text-input" {...register('name')} /></Field>
          <Field label="Description" error={errors.description?.message}><textarea className="text-input text-area" rows={3} {...register('description')} /></Field>
          <label className="toggle-field"><input type="checkbox" {...register('active')} /><span>Department is active</span></label>
          {saveMutation.isError && !Object.keys(getValidationErrors(saveMutation.error)).length && <div className="form-alert" role="alert">{getApiErrorMessage(saveMutation.error)}</div>}
          <div className="dialog-actions"><button type="button" className="button button-secondary" onClick={closeForm} disabled={saveMutation.isPending}>Cancel</button><button className="button button-primary" disabled={saveMutation.isPending}>{saveMutation.isPending ? 'Saving…' : editing ? 'Save changes' : 'Create department'}</button></div>
        </form>
      </Modal>
      <ConfirmDialog open={Boolean(deleting)} title="Delete department?" description={`Delete ${deleting?.name ?? 'this department'}? Employees or related records may prevent deletion.`} busy={deleteMutation.isPending} onCancel={() => setDeleting(null)} onConfirm={() => deleting && deleteMutation.mutate(deleting)} />
    </div>
  )
}

function Field({ label, error, children }: { label: string; error?: string; children: React.ReactNode }) {
  return <label className="form-field"><span className="field-label">{label}</span>{children}{error && <span className="field-error">{error}</span>}</label>
}
