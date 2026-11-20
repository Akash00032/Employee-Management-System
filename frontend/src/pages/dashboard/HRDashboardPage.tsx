import { useQuery } from '@tanstack/react-query'
import {
  Activity,
  ArrowUpRight,
  BriefcaseBusiness,
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
import { StatusBadge } from '../../components/common/StatusBadge'
import { useAuth } from '../../context/AuthContext'
import { getApiErrorMessage } from '../../services/api'
import { leaveService } from '../../services/leaveService'
import { hrDashboardService } from '../../services/hrDashboardService'

const palette = ['var(--chart-blue)', 'var(--chart-green)', 'var(--chart-amber)', 'var(--chart-red)', 'var(--chart-violet)', 'var(--chart-slate)']
const money = new Intl.NumberFormat(undefined, { maximumFractionDigits: 2 })

export function HRDashboardPage() {
  const { user, hasAnyRole } = useAuth()

  const hrDashboardQuery = useQuery({
    queryKey: ['hr-dashboard'],
    queryFn: hrDashboardService.load,
  })

  const data = hrDashboardQuery.data
  const summary = data?.summary
  const pendingLeaves = (data?.leaveRequests ?? []).filter((request) => request.status === 'PENDING').slice(0, 5)
  const canApprove = hasAnyRole('ADMIN', 'HR', 'MANAGER')
  const canManageLeave = hasAnyRole('ADMIN', 'HR', 'MANAGER')

  const attendanceStatusCounts = summary?.attendanceStats ?? []
  const attendancePresent = attendanceStatusCounts.find((entry) => entry.status === 'PRESENT')?.count ?? 0
  const attendanceAbsent = attendanceStatusCounts.find((entry) => entry.status === 'ABSENT')?.count ?? 0
  const attendanceLate = attendanceStatusCounts.find((entry) => entry.status === 'LATE')?.count ?? 0
  const attendanceHalfDay = attendanceStatusCounts.find((entry) => entry.status === 'HALF_DAY')?.count ?? 0
  const attendanceWfh = attendanceStatusCounts.find((entry) => entry.status === 'WORK_FROM_HOME')?.count ?? 0
  const attendanceOnLeave = attendanceStatusCounts.find((entry) => entry.status === 'ON_LEAVE')?.count ?? 0

  const payrollStatusCounts = (data?.payrolls ?? []).reduce<Record<string, number>>((counts, payroll) => {
    const status = String((payroll as { status?: string }).status ?? 'UNKNOWN')
    counts[status] = (counts[status] ?? 0) + 1
    return counts
  }, {})

  const projectStatusCounts = (data?.projects ?? []).reduce<Record<string, number>>((counts, project) => {
    const status = String((project as { status?: string }).status ?? 'UNKNOWN')
    counts[status] = (counts[status] ?? 0) + 1
    return counts
  }, {})

  const employeeStatusCounts = summary?.employeeStatusStats ?? []
  const onLeaveEmployees = employeeStatusCounts.find((entry) => entry.status === 'ON_LEAVE')?.count ?? 0
  const activeEmployees = employeeStatusCounts.find((entry) => entry.status === 'ACTIVE')?.count ?? 0
  const recentEmployees = summary?.recentEmployees ?? []

  const refreshDashboard = async () => {
    await hrDashboardQuery.refetch()
  }

  const totalPayroll = Array.isArray(data?.payrolls) && data.payrolls.length > 0
    ? data.payrolls.reduce((sum, payroll) => sum + (Number((payroll as { netSalary?: number | string }).netSalary ?? 0) || 0), 0)
    : null

  return (
    <div className="page-stack">
      <PageHeader
        eyebrow="HR PORTAL"
        title={`Welcome, ${user?.employeeName || 'Human Resources'}`}
        description="HR Management Overview"
        action={
          <button className="button button-secondary" onClick={() => void refreshDashboard()} disabled={hrDashboardQuery.isFetching}>
            <RefreshCcw size={15} className={hrDashboardQuery.isFetching ? 'spin' : ''} />
            Refresh
          </button>
        }
      />

      {hrDashboardQuery.isLoading && <LoadingState label="Loading HR dashboard..." />}

      {data && !summary && hrDashboardQuery.isFetched && (
        <ErrorState message="Unable to load HR dashboard data." retry={() => void refreshDashboard()} />
      )}

      {hrDashboardQuery.isError && (
        <ErrorState message={getApiErrorMessage(hrDashboardQuery.error)} retry={() => void refreshDashboard()} />
      )}

      {summary && (
        <>
          <section className="stat-grid" aria-label="HR KPI cards">
            <StatCard title="Total Employees" value={summary.totalEmployees.toLocaleString()} note={`${activeEmployees} active`} icon={<UsersRound size={19} />} tone="blue" />
            <StatCard title="Active Employees" value={activeEmployees.toLocaleString()} note={`${summary.totalEmployees - activeEmployees} inactive`} icon={<UsersRound size={19} />} tone="green" />
            <StatCard title="New Employees" value={recentEmployees.length.toLocaleString()} note="Recently joined" icon={<CalendarDays size={19} />} tone="violet" />
            <StatCard title="Employees On Leave" value={onLeaveEmployees.toLocaleString()} note="Current leave status" icon={<Clock3 size={19} />} tone="amber" />
            <StatCard title="Pending Leave Requests" value={summary.pendingLeaveRequests.toLocaleString()} note="Awaiting approval" icon={<Clock3 size={19} />} tone="amber" />
            <StatCard title="Attendance Today" value={`${attendancePresent.toLocaleString()} / ${(attendancePresent + attendanceAbsent + attendanceLate + attendanceHalfDay + attendanceWfh + attendanceOnLeave).toLocaleString()}`} note={`${attendancePresent} present`} icon={<CalendarCheck2 size={19} />} tone="blue" />
            <StatCard title="Pending Payroll" value={payrollStatusCounts.PENDING ? payrollStatusCounts.PENDING.toLocaleString() : 'N/A'} note="Requires action" icon={<CircleDollarSign size={19} />} tone="green" />
            <StatCard title="Active Projects" value={summary.activeProjects.toLocaleString()} note={`${summary.totalProjects} total`} icon={<BriefcaseBusiness size={19} />} tone="violet" />
          </section>

          <section className="dashboard-grid">
            <article className="panel chart-panel">
              <div className="panel-heading">
                <div>
                  <span className="panel-eyebrow">WORKFORCE</span>
                  <h2>Employee Overview</h2>
                  <p>Current employment distribution</p>
                </div>
                <span className="chart-icon"><Activity size={18} /></span>
              </div>

              {employeeStatusCounts.length === 0 ? (
                <EmptyState title="No employee status data" description="Employment status counts will appear here once employee data is available." />
              ) : (
                <div className="chart-content chart-content-stack">
                  <div className="chart-legend chart-legend-wide">
                    {employeeStatusCounts.map((entry, index) => (
                      <div className="legend-row" key={entry.status}>
                        <span className="legend-dot" style={{ backgroundColor: palette[index % palette.length] }} />
                        <span>{entry.status.replaceAll('_', ' ').replace(/\b\w/g, (letter) => letter.toUpperCase())}</span>
                        <strong>{entry.count}</strong>
                      </div>
                    ))}
                  </div>
                </div>
              )}
            </article>

            <article className="panel department-panel">
              <div className="panel-heading">
                <div>
                  <span className="panel-eyebrow">ORGANIZATION</span>
                  <h2>Department Overview</h2>
                </div>
                <Link className="text-link" to="/departments">Manage departments <ArrowUpRight size={15} /></Link>
              </div>

              {data.departments.length === 0 ? (
                <EmptyState title="No departments yet" description="Department metrics will appear once departments are configured." />
              ) : (
                <div className="department-list">
                  {data.departments.slice(0, 6).map((department, index) => (
                    <div className="department-row" key={department.id}>
                      <span className="department-avatar" style={{ background: `color-mix(in srgb, ${palette[index % palette.length]} 14%, transparent)`, color: palette[index % palette.length] }}>{department.code.slice(0, 2).toUpperCase()}</span>
                      <div className="department-row-main">
                        <div>
                          <strong>{department.name}</strong>
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
          </section>

          <section className="panel chart-panel">
            <div className="panel-heading">
              <div>
                <span className="panel-eyebrow">ATTENDANCE</span>
                <h2>Attendance Overview</h2>
                <p>Recorded attendance breakdown for today</p>
              </div>
              <span className="chart-icon chart-icon-green"><CalendarDays size={18} /></span>
            </div>

            {attendanceStatusCounts.length === 0 ? (
              <EmptyState title="No attendance recorded" description="Attendance data will appear here once staff check in." />
            ) : (
              <>
                <div className="attendance-chart">
                  <ResponsiveContainer width="100%" height="100%">
                    <AreaChart data={attendanceStatusCounts} margin={{ top: 16, right: 8, left: -20, bottom: 4 }}>
                      <defs>
                        <linearGradient id="hrAttendanceFill" x1="0" y1="0" x2="0" y2="1">
                          <stop offset="0%" stopColor="var(--chart-green)" stopOpacity={0.2} />
                          <stop offset="95%" stopColor="var(--chart-green)" stopOpacity={0} />
                        </linearGradient>
                      </defs>
                      <CartesianGrid vertical={false} stroke="var(--chart-grid)" />
                      <XAxis dataKey="status" tickFormatter={(value: string) => value.replaceAll('_', ' ').replace(/\b\w/g, (letter) => letter.toUpperCase())} tickLine={false} axisLine={false} tick={{ fill: 'var(--muted)', fontSize: 10 }} interval={0} />
                      <YAxis allowDecimals={false} tickLine={false} axisLine={false} tick={{ fill: 'var(--muted)', fontSize: 11 }} />
                      <Tooltip formatter={(value) => [value, 'Employees']} contentStyle={{ backgroundColor: 'var(--color-surface-elevated)', borderColor: 'var(--color-border)', color: 'var(--color-text)' }} labelStyle={{ color: 'var(--color-text)' }} />
                      <Area type="monotone" dataKey="count" stroke="var(--chart-green)" strokeWidth={2.5} fill="url(#hrAttendanceFill)" />
                    </AreaChart>
                  </ResponsiveContainer>
                </div>
                <div className="attendance-totals">
                  <div><span className="total-dot dot-present" /><span>Present</span><strong>{attendancePresent}</strong></div>
                  <div><span className="total-dot dot-away" /><span>Absent</span><strong>{attendanceAbsent}</strong></div>
                  <div><span className="total-dot dot-leave" /><span>Half day</span><strong>{attendanceHalfDay}</strong></div>
                  <div><span className="total-dot dot-present" /><span>Late</span><strong>{attendanceLate}</strong></div>
                  <div><span className="total-dot dot-away" /><span>WFH</span><strong>{attendanceWfh}</strong></div>
                  <div><span className="total-dot dot-leave" /><span>On leave</span><strong>{attendanceOnLeave}</strong></div>
                </div>
              </>
            )}
          </section>

          <section className="panel department-panel">
            <div className="panel-heading">
              <div>
                <span className="panel-eyebrow">LEAVE OPS</span>
                <h2>Leave Management</h2>
              </div>
              <Link className="text-link" to="/leave">Open leave queue <ArrowUpRight size={15} /></Link>
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
                      {canManageLeave && <th>ACTION</th>}
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
                        <td><StatusBadge status={request.status} /></td>
                        {canApprove ? (
                          <td>
                            <div className="row-actions">
                              <button className="icon-button row-action action-approve" title="Approve" aria-label={`Approve ${request.employeeName}`} onClick={() => void handleLeaveAction('approve', request.id)}>
                                <Check size={16} />
                              </button>
                              <button className="icon-button row-action action-reject" title="Reject" aria-label={`Reject ${request.employeeName}`} onClick={() => void handleLeaveAction('reject', request.id)}>
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
                  <span className="panel-eyebrow">PAYROLL</span>
                  <h2>Payroll Overview</h2>
                </div>
                <span className="chart-icon"><CircleDollarSign size={18} /></span>
              </div>
              <div className="attendance-totals">
                <div><span className="total-dot dot-away" /><span>Pending</span><strong>{payrollStatusCounts.PENDING ?? 0}</strong></div>
                <div><span className="total-dot dot-present" /><span>Processed</span><strong>{payrollStatusCounts.PROCESSED ?? 0}</strong></div>
                <div><span className="total-dot dot-leave" /><span>Paid</span><strong>{payrollStatusCounts.PAID ?? 0}</strong></div>
                <div><span className="total-dot dot-away" /><span>Failed</span><strong>{payrollStatusCounts.FAILED ?? 0}</strong></div>
              </div>
              {totalPayroll !== null && (
                <div className="payroll-strip" style={{ marginTop: '16px' }}>
                  <div className="payroll-strip-icon"><CircleDollarSign size={21} /></div>
                  <div><span>TOTAL PAYROLL</span><strong>{money.format(totalPayroll)}</strong></div>
                  <p>Based on current payroll records</p>
                </div>
              )}
            </article>

            <article className="panel recent-panel">
              <div className="panel-heading">
                <div>
                  <span className="panel-eyebrow">PROJECTS</span>
                  <h2>Project Overview</h2>
                </div>
                <Link className="text-link" to="/employees">View team <ArrowUpRight size={15} /></Link>
              </div>
              <div className="attendance-totals">
                <div><span className="total-dot dot-present" /><span>Total</span><strong>{summary.totalProjects}</strong></div>
                <div><span className="total-dot dot-away" /><span>Active</span><strong>{summary.activeProjects}</strong></div>
                <div><span className="total-dot dot-leave" /><span>Completed</span><strong>{projectStatusCounts.COMPLETED ?? 0}</strong></div>
              </div>
            </article>
          </section>

          <section className="dashboard-grid dashboard-grid-bottom">
            <article className="panel recent-panel">
              <div className="panel-heading">
                <div>
                  <span className="panel-eyebrow">LATEST JOINERS</span>
                  <h2>Recently Joined Employees</h2>
                </div>
                <Link className="text-link" to="/employees">View all <ArrowUpRight size={15} /></Link>
              </div>

              {recentEmployees.length === 0 ? (
                <EmptyState title="No recent employees" description="New employee records will appear here as they are added." />
              ) : (
                <div className="recent-list">
                  {recentEmployees.map((employee) => (
                    <Link className="recent-row" to={`/employees/${employee.id}`} key={employee.id}>
                      <span className="avatar avatar-soft">{employee.employeeName.split(' ').map((part) => part[0]).slice(0, 2).join('')}</span>
                      <span className="recent-main">
                        <strong>{employee.employeeName}</strong>
                        <small>{employee.designation}{employee.departmentName ? ` · ${employee.departmentName}` : ''}</small>
                      </span>
                      <span className="recent-date">{new Intl.DateTimeFormat(undefined, { month: 'short', day: 'numeric', year: 'numeric' }).format(new Date(`${employee.joiningDate}T00:00:00`))}</span>
                    </Link>
                  ))}
                </div>
              )}
            </article>

            <article className="panel recent-panel">
              <div className="panel-heading">
                <div>
                  <span className="panel-eyebrow">QUICK ACTIONS</span>
                  <h2>HR Tools</h2>
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
  { label: 'Manage Employees', path: '/employees', caption: 'Review employee records' },
  { label: 'Attendance', path: '/attendance', caption: 'Daily attendance review' },
  { label: 'Leave Requests', path: '/leave', caption: 'Approve or reject time off' },
  { label: 'Departments', path: '/departments', caption: 'Team structure overview' },
  { label: 'Work Shifts', path: '/work-shifts', caption: 'Schedule coverage' },
]

async function handleLeaveAction(action: 'approve' | 'reject', id: number) {
  try {
    if (action === 'approve') {
      await leaveService.approve(id, false)
    } else {
      await leaveService.reject(id, 'Reviewed by HR', false)
    }
  } catch (error) {
    console.error(error)
  }
}
