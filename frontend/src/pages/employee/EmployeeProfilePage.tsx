import { useQuery } from '@tanstack/react-query'
import { ArrowLeft, BriefcaseBusiness, CalendarDays, Mail, MapPin, Pencil, Phone, UserRound } from 'lucide-react'
import { Link, useParams } from 'react-router-dom'
import { ErrorState, LoadingState } from '../../components/common/Feedback'
import { PageHeader } from '../../components/common/PageHeader'
import { StatusBadge } from '../../components/common/StatusBadge'
import { employeeService } from '../../services/employeeService'
import { getApiErrorMessage } from '../../services/api'
import { useAuth } from '../../context/AuthContext'

export function EmployeeProfilePage() {
  const { hasAnyRole } = useAuth()
  const isManager = hasAnyRole('MANAGER')
  const { id } = useParams()
  const employeeId = Number(id)
  const query = useQuery({
    queryKey: ['employees', employeeId, { teamOnly: isManager }],
    queryFn: () => employeeService.get(employeeId, isManager),
    enabled: Number.isInteger(employeeId) && employeeId > 0,
  })

  if (!Number.isInteger(employeeId) || employeeId <= 0) return <ErrorState message="The employee ID in this URL is invalid." />
  if (query.isLoading) return <LoadingState label="Loading employee profile" />
  if (query.isError) return <ErrorState message={getApiErrorMessage(query.error)} retry={() => void query.refetch()} />
  if (!query.data) return <ErrorState message="Employee profile was not returned by the server." />

  const employee = query.data
  const initials = `${employee.firstName[0]}${employee.lastName[0]}`
  return (
    <div className="page-stack">
      <Link to="/employees" className="back-link"><ArrowLeft size={16} /> Back to directory</Link>
      <PageHeader
        eyebrow={`EMPLOYEE · ${employee.employeeCode}`}
        title={employee.fullName}
        description={employee.designation}
        action={hasAnyRole('ADMIN', 'HR') ? <Link className="button button-primary" to={`/employees/${employee.id}/edit`}><Pencil size={16} /> Edit profile</Link> : undefined}
      />
      <section className="profile-hero panel">
        <span className="profile-large-avatar">{initials}</span>
        <div className="profile-hero-main">
          <div className="profile-name-row"><h2>{employee.fullName}</h2><StatusBadge status={employee.employmentStatus} /></div>
          <p>{employee.designation} {employee.departmentName ? <><span>·</span> {employee.departmentName}</> : null}</p>
          <div className="profile-quick-info">
            <span><Mail size={15} />{employee.email}</span>
            <span><Phone size={15} />{employee.phone}</span>
            <span><CalendarDays size={15} />Joined {new Intl.DateTimeFormat(undefined, { year: 'numeric', month: 'long', day: 'numeric' }).format(new Date(`${employee.joiningDate}T00:00:00`))}</span>
          </div>
        </div>
      </section>
      <section className="profile-grid">
        <article className="panel profile-detail-panel">
          <div className="panel-heading"><div><span className="panel-eyebrow">PERSONAL</span><h2>Personal information</h2><p>Contact details and personal information</p></div><UserRound size={19} className="detail-panel-icon" /></div>
          <Detail label="Email address" value={employee.email} />
          <Detail label="Phone number" value={employee.phone} />
          <Detail label="Date of birth" value={employee.dateOfBirth ? new Intl.DateTimeFormat(undefined, { dateStyle: 'long' }).format(new Date(`${employee.dateOfBirth}T00:00:00`)) : 'Not provided'} />
          <Detail label="Gender" value={employee.gender ? employee.gender[0] + employee.gender.slice(1).toLowerCase() : 'Not specified'} />
        </article>
        <article className="panel profile-detail-panel">
          <div className="panel-heading"><div><span className="panel-eyebrow">AT WORK</span><h2>Employment details</h2><p>Team, role and reporting line</p></div><BriefcaseBusiness size={19} className="detail-panel-icon" /></div>
          <Detail label="Employee code" value={employee.employeeCode} />
          <Detail label="Department" value={employee.departmentName ?? 'Not assigned'} />
          <Detail label="Manager" value={employee.managerName ?? 'Not assigned'} />
          <Detail label="Work shift" value={employee.workShiftName ?? 'Not assigned'} />
          <Detail label="Employment type" value={employee.employmentType.replaceAll('_', ' ').toLowerCase()} />
          {employee.salary !== null && <Detail label="Salary amount" value={new Intl.NumberFormat(undefined, { maximumFractionDigits: 2 }).format(employee.salary)} />}
        </article>
      </section>
      <div className="profile-api-note"><MapPin size={15} /> Profile details reflect the fields provided by the employee API. Related attendance, leave, payroll and project history are managed in their own modules.</div>
    </div>
  )
}

function Detail({ label, value }: { label: string; value: string }) {
  return <div className="detail-row"><span>{label}</span><strong>{value}</strong></div>
}
