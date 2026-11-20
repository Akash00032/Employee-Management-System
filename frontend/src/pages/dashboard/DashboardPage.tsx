import { useQuery } from '@tanstack/react-query'
import {
  Activity,
  ArrowUpRight,
  BriefcaseBusiness,
  Building2,
  CalendarCheck2,
  CalendarDays,
  Check,
  CircleDollarSign,
  Clock3,
  RefreshCcw,
  UsersRound,
  X,
} from 'lucide-react'
import {
  Area,
  AreaChart,
  CartesianGrid,
  ResponsiveContainer,
  Tooltip,
  XAxis,
  YAxis,
} from 'recharts'
import { Link } from 'react-router-dom'
import { PageHeader } from '../../components/common/PageHeader'
import { EmptyState, ErrorState, LoadingState } from '../../components/common/Feedback'
import { useToast } from '../../components/common/ToastProvider'
import { useAuth } from '../../context/AuthContext'
import { dashboardService } from '../../services/dashboardService'
import { getApiErrorMessage } from '../../services/api'
import { leaveService } from '../../services/leaveService'

const palette = ['var(--chart-blue)', 'var(--chart-green)', 'var(--chart-amber)', 'var(--chart-red)', 'var(--chart-violet)', 'var(--chart-slate)']
const money = new Intl.NumberFormat(undefined, { maximumFractionDigits: 2 })

const statusLabel = (value: string) => value.replaceAll('_', ' ').toLowerCase().replace(/\b\w/g, (letter) => letter.toUpperCase())

export function DashboardPage() {
  const { user, hasAnyRole } = useAuth()
  const notify = useToast()

  const summaryQuery = useQuery({
    queryKey: ['dashboard', 'summary'],
    queryFn: dashboardService.summary,
  })

  const pendingLeavesQuery = useQuery({
    queryKey: ['dashboard', 'pending-leaves'],
    queryFn: async () => {
      const requests = await leaveService.requests(false)
      return requests
        .filter((request) => request.status === 'PENDING')
        .sort((left, right) => right.appliedAt.localeCompare(left.appliedAt))
        .slice(0, 5)
    },
  })

  const summary = summaryQuery.data
  const pendingLeaves = pendingLeavesQuery.data ?? []
  const canManageLeaves = hasAnyRole('ADMIN', 'HR', 'MANAGER')

  const refreshDashboard = async () => {
    await Promise.all([summaryQuery.refetch(), pendingLeavesQuery.refetch()])
  }

  const attendanceTotal = summary ? summary.presentToday + summary.absentToday + summary.onLeaveToday : 0

  return (
    <div className="page-stack">
      <PageHeader
        eyebrow="ADMIN PORTAL"
        title={`Welcome, ${user?.employeeName || 'System Administrator'}`}
        description="Organization overview"
        action={
          <div className="header-actions">
            <button className="button button-secondary" onClick={() => void refreshDashboard()} disabled={summaryQuery.isFetching || pendingLeavesQuery.isFetching}>
              <RefreshCcw size={15} className={summaryQuery.isFetching || pendingLeavesQuery.isFetching ? 'spin' : ''} />
              Refresh
            </button>
          </div>
        }
      />

      {(summaryQuery.isLoading || pendingLeavesQuery.isLoading) && <LoadingState label="Loading dashboard..." />}

      {(summaryQuery.isError || pendingLeavesQuery.isError) && (
        <ErrorState
          message={summaryQuery.isError ? getApiErrorMessage(summaryQuery.error) : getApiErrorMessage(pendingLeavesQuery.error)}
          retry={() => void refreshDashboard()}
        />
      )}

      {summary && (
        <>
          <section className="stat-grid" aria-label="Admin overview metrics">
            <StatCard title="Total Employees" value={summary.totalEmployees.toLocaleString()} note={`${summary.activeEmployees} active`} icon={<UsersRound size={19} />} tone="blue" />
            <StatCard title="Active Employees" value={summary.activeEmployees.toLocaleString()} note={`${summary.inactiveEmployees} inactive`} icon={<UsersRound size={19} />} tone="green" />
            <StatCard title="Departments" value={summary.totalDepartments.toLocaleString()} note="Across the organization" icon={<Building2 size={19} />} tone="violet" />
            <StatCard title="Attendance Today" value={attendanceTotal ? `${summary.presentToday.toLocaleString()} / ${attendanceTotal.toLocaleString()}` : 'N/A'} note={`${summary.presentToday} present today`} icon={<CalendarCheck2 size={19} />} tone="amber" />
            <StatCard title="Pending Leave Requests" value={summary.pendingLeaveRequests.toLocaleString()} note="Awaiting review" icon={<Clock3 size={19} />} tone="amber" />
            <StatCard title="Active Projects" value={summary.activeProjects.toLocaleString()} note={`${summary.totalProjects} total`} icon={<BriefcaseBusiness size={19} />} tone="blue" />
            <StatCard title="Payroll Status" value={summary.totalPayroll !== null ? money.format(summary.totalPayroll) : 'N/A'} note={summary.totalPayroll !== null ? 'Recorded payroll total' : 'Aggregate unavailable'} icon={<CircleDollarSign size={19} />} tone="green" />
            <StatCard title="System Users" value="N/A" note="No aggregate endpoint" icon={<UsersRound size={19} />} tone="violet" />
          </section>

          <section className="dashboard-grid">
            <article className="panel chart-panel">
              <div className="panel-heading">
                <div>
                  <span className="panel-eyebrow">EMPLOYEE DATA</span>
                  <h2>Employment Status</h2>
                  <p>Current workforce distribution</p>
                </div>
                <span className="chart-icon"><Activity size={18} /></span>
              </div>

              {summary.employeeStatusStats.length === 0 ? (
                <EmptyState title="No employee status data yet" description="Workforce distribution will show here once employee records are available." />
              ) : (
                <div className="chart-content chart-content-stack">
                  <div className="chart-legend chart-legend-wide">
                    {summary.employeeStatusStats.map((entry, index) => (
                      <div className="legend-row" key={entry.status}>
                        <span className="legend-dot" style={{ backgroundColor: palette[index % palette.length] }} />
                        <span>{statusLabel(String(entry.status))}</span>
                        <strong>{entry.count}</strong>
                      </div>
                    ))}
                  </div>
                </div>
              )}
            </article>

            <article className="panel chart-panel">
              <div className="panel-heading">
                <div>
                  <span className="panel-eyebrow">TODAY</span>
                  <h2>Attendance Overview</h2>
                  <p>Recorded attendance summary</p>
                </div>
                <span className="chart-icon chart-icon-green"><CalendarDays size={18} /></span>
              </div>

              {summary.attendanceStats.length === 0 ? (
                <EmptyState title="No attendance recorded" description="Attendance activity will appear when records are available." />
              ) : (
                <>
                  <div className="attendance-chart">
                    <ResponsiveContainer width="100%" height="100%">
                      <AreaChart data={summary.attendanceStats} margin={{ top: 16, right: 8, left: -20, bottom: 4 }}>
                        <defs>
                          <linearGradient id="attendanceFill" x1="0" y1="0" x2="0" y2="1">
                            <stop offset="0%" stopColor="var(--chart-green)" stopOpacity={0.2} />
                            <stop offset="95%" stopColor="var(--chart-green)" stopOpacity={0} />
                          </linearGradient>
                        </defs>
                        <CartesianGrid vertical={false} stroke="var(--chart-grid)" />
                        <XAxis dataKey="status" tickFormatter={(value: string) => statusLabel(value)} tickLine={false} axisLine={false} tick={{ fill: 'var(--muted)', fontSize: 10 }} interval={0} />
                        <YAxis allowDecimals={false} tickLine={false} axisLine={false} tick={{ fill: 'var(--muted)', fontSize: 11 }} />
                        <Tooltip formatter={(value) => [value, 'Employees']} contentStyle={{ backgroundColor: 'var(--color-surface-elevated)', borderColor: 'var(--color-border)', color: 'var(--color-text)' }} labelStyle={{ color: 'var(--color-text)' }} />
                        <Area type="monotone" dataKey="count" stroke="var(--chart-green)" strokeWidth={2.5} fill="url(#attendanceFill)" />
                      </AreaChart>
                    </ResponsiveContainer>
                  </div>
                  <div className="attendance-totals">
                    {summary.attendanceStats.map((entry) => (
                      <div key={entry.status}>
                        <span className="total-dot" style={{ backgroundColor: palette[(summary.attendanceStats.findIndex((item) => item.status === entry.status) + 1) % palette.length] }} />
                        <span>{statusLabel(String(entry.status))}</span>
                        <strong>{entry.count}</strong>
                      </div>
                    ))}
                  </div>
                </>
              )}
            </article>
          </section>

          <section className="dashboard-grid dashboard-grid-bottom">
            <article className="panel department-panel">
              <div className="panel-heading">
                <div>
                  <span className="panel-eyebrow">TEAM STRUCTURE</span>
                  <h2>Department Overview</h2>
                </div>
                <Link className="text-link" to="/departments">View departments <ArrowUpRight size={15} /></Link>
              </div>

              {summary.departmentStats.length === 0 ? (
                <EmptyState title="No departments configured" description="Departments will appear here once your organization is set up." />
              ) : (
                <div className="department-list">
                  {summary.departmentStats.slice(0, 6).map((department, index) => (
                    <div className="department-row" key={department.departmentId}>
                      <span className="department-avatar" style={{ background: `color-mix(in srgb, ${palette[index % palette.length]} 14%, transparent)`, color: palette[index % palette.length] }}>{String(department.departmentCode ?? 'NA').slice(0, 2)}</span>
                      <div className="department-row-main">
                        <div>
                          <strong>{department.departmentName}</strong>
                          <span>{department.employeeCount} employees</span>
                        </div>
                        <div className="progress-track">
                          <span style={{ width: `${summary.totalEmployees ? (department.employeeCount / summary.totalEmployees) * 100 : 0}%`, background: palette[index % palette.length] }} />
                        </div>
                      </div>
                      <b>{summary.totalEmployees ? Math.round((department.employeeCount / summary.totalEmployees) * 100) : 0}%</b>
                    </div>
                  ))}
                </div>
              )}
            </article>

            <article className="panel recent-panel">
              <div className="panel-heading">
                <div>
                  <span className="panel-eyebrow">RECENTLY JOINED</span>
                  <h2>Recent Employees</h2>
                </div>
                <Link className="text-link" to="/employees">View all <ArrowUpRight size={15} /></Link>
              </div>

              {summary.recentEmployees.length === 0 ? (
                <EmptyState title="No recent employees" description="New employee records will show up here as they are added." />
              ) : (
                <div className="recent-list">
                  {summary.recentEmployees.map((employee) => (
                    <Link className="recent-row" to={`/employees/${employee.id}`} key={employee.id}>
                      <span className="avatar avatar-soft">{employee.employeeName.split(' ').map((part) => part[0]).slice(0, 2).join('')}</span>
                      <span className="recent-main">
                        <strong>{employee.employeeName}</strong>
                        <small>{employee.designation}{employee.departmentName ? ` · ${employee.departmentName}` : ''}</small>
                      </span>
                      <span className="recent-date">{new Intl.DateTimeFormat(undefined, { month: 'short', day: 'numeric' }).format(new Date(`${employee.joiningDate}T00:00:00`))}</span>
                    </Link>
                  ))}
                </div>
              )}
            </article>
          </section>

          <section className="panel department-panel">
            <div className="panel-heading">
              <div>
                <span className="panel-eyebrow">LEAVE REVIEW</span>
                <h2>Pending Leave Requests</h2>
              </div>
              <Link className="text-link" to="/leave">Manage leave <ArrowUpRight size={15} /></Link>
            </div>

            {pendingLeaves.length === 0 ? (
              <EmptyState title="No pending leave requests" description="Everything is up to date." />
            ) : (
              <div className="table-scroll">
                <table className="data-table leave-table">
                  <thead>
                    <tr>
                      <th>EMPLOYEE</th>
                      <th>LEAVE TYPE</th>
                      <th>START DATE</th>
                      <th>END DATE</th>
                      <th>DAYS</th>
                      <th>STATUS</th>
                      {canManageLeaves && <th>ACTION</th>}
                    </tr>
                  </thead>
                  <tbody>
                    {pendingLeaves.map((request) => (
                      <tr key={request.id}>
                        <td>
                          <div className="employee-cell">
                            <span className="avatar avatar-soft">{request.employeeName.split(' ').map((part) => part[0]).slice(0, 2).join('')}</span>
                            <span>
                              <strong>{request.employeeName}</strong>
                              <small>{request.employeeCode}</small>
                            </span>
                          </div>
                        </td>
                        <td>{request.leaveTypeName}</td>
                        <td>{new Intl.DateTimeFormat(undefined, { month: 'short', day: 'numeric', year: 'numeric' }).format(new Date(`${request.startDate}T00:00:00`))}</td>
                        <td>{new Intl.DateTimeFormat(undefined, { month: 'short', day: 'numeric', year: 'numeric' }).format(new Date(`${request.endDate}T00:00:00`))}</td>
                        <td>{request.totalDays}</td>
                        <td><span className="status-badge status-pending"><span className="status-dot" />PENDING</span></td>
                        {canManageLeaves ? (
                          <td>
                            <div className="row-actions">
                              <button className="icon-button row-action action-approve" title="Approve" aria-label={`Approve ${request.employeeName}`} onClick={() => handleLeaveAction('approve', request.id, notify)}>
                                <Check size={16} />
                              </button>
                              <button className="icon-button row-action action-reject" title="Reject" aria-label={`Reject ${request.employeeName}`} onClick={() => handleLeaveAction('reject', request.id, notify)}>
                                <X size={16} />
                              </button>
                            </div>
                          </td>
                        ) : null}
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            )}
          </section>

          <section className="dashboard-grid dashboard-grid-bottom">
            <article className="panel recent-panel">
              <div className="panel-heading">
                <div>
                  <span className="panel-eyebrow">PROJECTS</span>
                  <h2>Project Overview</h2>
                </div>
                <span className="chart-icon"><BriefcaseBusiness size={18} /></span>
              </div>
              <div className="attendance-totals">
                <div><span className="total-dot dot-present" /><span>Total projects</span><strong>{summary.totalProjects}</strong></div>
                <div><span className="total-dot dot-leave" /><span>Active projects</span><strong>{summary.activeProjects}</strong></div>
              </div>
            </article>

            <article className="panel recent-panel">
              <div className="panel-heading">
                <div>
                  <span className="panel-eyebrow">QUICK ACTIONS</span>
                  <h2>Admin Tools</h2>
                </div>
              </div>
              <div className="recent-list">
                {quickActions.map((action) => (
                  <Link key={action.path} className="recent-row" to={action.path}>
                    <span className="avatar avatar-soft">{action.label.slice(0, 2).toUpperCase()}</span>
                    <span className="recent-main">
                      <strong>{action.label}</strong>
                      <small>{action.caption}</small>
                    </span>
                    <ArrowUpRight size={16} />
                  </Link>
                ))}
              </div>
            </article>
          </section>
        </>
      )}
    </div>
  )
}

function StatCard({ title, value, note, icon, tone }: { title: string; value: string; note: string; icon: React.ReactNode; tone: string }) {
  return (
    <article className="stat-card">
      <div className="stat-top">
        <span>{title}</span>
        <span className={`stat-icon stat-icon-${tone}`}>{icon}</span>
      </div>
      <strong className="stat-value">{value}</strong>
      <div className="stat-foot">
        <span className={`stat-trend ${tone === 'amber' ? 'trend-flat' : ''}`}>
          {tone === 'amber' && <Clock3 size={14} />}
          {note}
        </span>
      </div>
    </article>
  )
}

const quickActions = [
  { label: 'Add Employee', path: '/employees/create', caption: 'Create a new hire' },
  { label: 'Manage Employees', path: '/employees', caption: 'Review current staff' },
  { label: 'View Attendance', path: '/attendance', caption: 'Track daily attendance' },
  { label: 'Manage Leave', path: '/leave', caption: 'Approve pending requests' },
  { label: 'Departments', path: '/departments', caption: 'Organizational structure' },
  { label: 'Work Shifts', path: '/work-shifts', caption: 'Schedule operations' },
]

async function handleLeaveAction(action: 'approve' | 'reject', id: number, notify: (message: string, kind?: 'success' | 'error') => void) {
  try {
    if (action === 'approve') {
      await leaveService.approve(id, false)
      notify('Leave request approved.')
    } else {
      await leaveService.reject(id, 'Reviewed by administrator', false)
      notify('Leave request rejected.')
    }
    await Promise.resolve()
  } catch (error) {
    notify(getApiErrorMessage(error), 'error')
  }
}
