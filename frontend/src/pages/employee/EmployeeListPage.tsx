import { useEffect, useState } from 'react'
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { Link, useSearchParams } from 'react-router-dom'
import { ArrowDownUp, ArrowRight, ChevronLeft, ChevronRight, Eye, Plus, Search, Trash2, UserRoundPen } from 'lucide-react'
import { ConfirmDialog } from '../../components/common/ConfirmDialog'
import { EmptyState, ErrorState, LoadingState } from '../../components/common/Feedback'
import { PageHeader } from '../../components/common/PageHeader'
import { StatusBadge } from '../../components/common/StatusBadge'
import { useToast } from '../../components/common/ToastProvider'
import { useAuth } from '../../context/AuthContext'
import { useDebouncedValue } from '../../hooks/useDebouncedValue'
import { employeeService } from '../../services/employeeService'
import { getApiErrorMessage } from '../../services/api'
import type { Employee } from '../../types/api'

const PAGE_SIZE = 10

export function EmployeeListPage() {
  const [searchParams, setSearchParams] = useSearchParams()
  const [search, setSearch] = useState(searchParams.get('q') ?? '')
  const [page, setPage] = useState(0)
  const [sort, setSort] = useState<'name' | 'joiningDate'>('name')
  const [selected, setSelected] = useState<Employee | null>(null)
  const debouncedSearch = useDebouncedValue(search.trim())
  const queryClient = useQueryClient()
  const notify = useToast()
  const { hasAnyRole } = useAuth()
  const canEdit = hasAnyRole('ADMIN', 'HR')
  const isManager = hasAnyRole('MANAGER')

  useEffect(() => {
    setSearchParams((current) => {
      const next = new URLSearchParams(current)
      if (debouncedSearch) next.set('q', debouncedSearch)
      else next.delete('q')
      return next
    }, { replace: true })
    setPage(0)
  }, [debouncedSearch, setSearchParams])

  const teamQuery = useQuery({
    queryKey: ['employees', { keyword: debouncedSearch, teamOnly: isManager }],
    queryFn: () => employeeService.list(
      debouncedSearch || undefined,
      true,
    ),
    enabled: isManager,
  })
  const directoryQuery = useQuery({
    queryKey: ['employee-directory', { keyword: debouncedSearch, page, sort }],
    queryFn: () => employeeService.directory(debouncedSearch, page, PAGE_SIZE, sort),
    enabled: !isManager,
  })
  const employeesQuery = isManager ? teamQuery : directoryQuery

  useEffect(() => {
    if (!isManager && directoryQuery.data && page >= directoryQuery.data.totalPages && page > 0) {
      setPage(Math.max(0, directoryQuery.data.totalPages - 1))
    }
  }, [directoryQuery.data, isManager, page])

  const deleteMutation = useMutation({
    mutationFn: (id: number) => employeeService.remove(id),
    onSuccess: async () => {
      await queryClient.invalidateQueries({ queryKey: ['employees'] })
      await queryClient.invalidateQueries({ queryKey: ['employee-directory'] })
      await queryClient.invalidateQueries({ queryKey: ['dashboard'] })
      notify('Employee record deleted.')
      setSelected(null)
    },
    onError: (error) => notify(getApiErrorMessage(error), 'error'),
  })

  const returnedEmployees = isManager
    ? teamQuery.data ?? []
    : directoryQuery.data?.content ?? []
  const displayedEmployees = isManager
    ? [...returnedEmployees].sort((left, right) => {
      if (sort === 'joiningDate') return right.joiningDate.localeCompare(left.joiningDate)
      return left.fullName.localeCompare(right.fullName)
    })
    : returnedEmployees
  const totalElements = isManager
    ? displayedEmployees.length
    : directoryQuery.data?.totalElements ?? 0
  const totalPages = Math.max(
    1,
    isManager
      ? Math.ceil(totalElements / PAGE_SIZE)
      : directoryQuery.data?.totalPages ?? 0,
  )
  const visibleEmployees = isManager
    ? displayedEmployees.slice(page * PAGE_SIZE, (page + 1) * PAGE_SIZE)
    : displayedEmployees

  return (
    <div className="page-stack">
      <PageHeader
        eyebrow="YOUR PEOPLE"
        title="Employee directory"
        description="A thoughtful place for every person on your team."
        action={canEdit ? <Link className="button button-primary" to="/employees/create"><Plus size={17} /> Add employee</Link> : undefined}
      />

      <section className="panel directory-panel">
        <div className="directory-toolbar">
          <label className="table-search">
            <Search size={17} />
            <input value={search} onChange={(event) => setSearch(event.target.value)} placeholder="Search employee name or email" aria-label="Search employees by name or email" />
            {search && <button className="text-button" onClick={() => setSearch('')}>Clear</button>}
          </label>
          <div className="directory-tools">
            <label className="sort-control"><ArrowDownUp size={15} /><span>Sort</span><select value={sort} onChange={(event) => setSort(event.target.value as typeof sort)} aria-label="Sort employees"><option value="name">Name</option><option value="joiningDate">Recently joined</option></select></label>
          </div>
        </div>
        {employeesQuery.isLoading && <LoadingState label="Loading employee directory" />}
        {employeesQuery.isError && <ErrorState message={getApiErrorMessage(employeesQuery.error)} retry={() => void employeesQuery.refetch()} />}
        {employeesQuery.data && displayedEmployees.length === 0 && (
          <EmptyState
            title={debouncedSearch ? 'No employees found' : 'Your directory is ready'}
            description={debouncedSearch ? 'Try another first name, last name, or email address.' : 'Add your first employee to begin building your directory.'}
          />
        )}
        {employeesQuery.data && displayedEmployees.length > 0 && (
          <>
            <div className="table-scroll">
              <table className="data-table employee-table">
                <thead><tr><th>EMPLOYEE</th><th>DEPARTMENT</th><th>POSITION</th><th>EMPLOYMENT</th><th>JOINED</th><th className="actions-heading">ACTIONS</th></tr></thead>
                <tbody>
                  {visibleEmployees.map((employee) => (
                    <tr key={employee.id}>
                      <td>
                        <Link className="employee-cell" to={`/employees/${employee.id}`}>
                          <span className="avatar avatar-soft">{employee.firstName[0]}{employee.lastName[0]}</span>
                          <span><strong>{employee.fullName}</strong><small>{employee.employeeCode} · {employee.email}</small></span>
                        </Link>
                      </td>
                      <td><span className="department-cell">{employee.departmentName ?? 'Unassigned'}</span></td>
                      <td><span className="position-cell">{employee.designation}</span><small className="table-subtext">{employee.employmentType.replaceAll('_', ' ').toLowerCase()}</small></td>
                      <td><StatusBadge status={employee.employmentStatus} /></td>
                      <td><span className="date-cell">{new Intl.DateTimeFormat(undefined, { year: 'numeric', month: 'short', day: 'numeric' }).format(new Date(`${employee.joiningDate}T00:00:00`))}</span></td>
                      <td>
                        <div className="row-actions">
                          <Link className="icon-button row-action" to={`/employees/${employee.id}`} aria-label={`View ${employee.fullName}`} title="View profile"><Eye size={17} /></Link>
                          {canEdit && <><Link className="icon-button row-action" to={`/employees/${employee.id}/edit`} aria-label={`Edit ${employee.fullName}`} title="Edit employee"><UserRoundPen size={17} /></Link><button className="icon-button row-action row-action-danger" onClick={() => setSelected(employee)} aria-label={`Delete ${employee.fullName}`} title="Delete employee"><Trash2 size={17} /></button></>}
                        </div>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
            <div className="table-footer">
              <span>Showing <strong>{totalElements ? page * PAGE_SIZE + 1 : 0}–{page * PAGE_SIZE + visibleEmployees.length}</strong> of <strong>{totalElements}</strong> employees</span>
              <div className="pagination">
                <button className="icon-button pagination-button" onClick={() => setPage((value) => Math.max(0, value - 1))} disabled={page === 0} aria-label="Previous page"><ChevronLeft size={18} /></button>
                <span>Page {page + 1} of {totalPages}</span>
                <button className="icon-button pagination-button" onClick={() => setPage((value) => Math.min(totalPages - 1, value + 1))} disabled={page >= totalPages - 1} aria-label="Next page"><ChevronRight size={18} /></button>
              </div>
            </div>
          </>
        )}
      </section>
      <div className="directory-note"><span>i</span> {isManager ? 'This list contains only your direct reports; compensation is not included.' : 'Search, paging and sorting are applied on the server across the employee directory.'} <ArrowRight size={14} /></div>
      <ConfirmDialog
        open={Boolean(selected)}
        title="Delete employee?"
        description={`This will permanently remove ${selected?.fullName ?? 'this employee'} from the directory. Related records may prevent deletion.`}
        busy={deleteMutation.isPending}
        onCancel={() => setSelected(null)}
        onConfirm={() => selected && deleteMutation.mutate(selected.id)}
      />
    </div>
  )
}
