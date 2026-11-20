import type { AttendanceStatus, EmploymentStatus, LeaveStatus } from '../../types/api'

const labels: Record<EmploymentStatus, string> = {
  ACTIVE: 'Active',
  ON_LEAVE: 'On leave',
  SUSPENDED: 'Suspended',
  RESIGNED: 'Resigned',
  TERMINATED: 'Terminated',
  RETIRED: 'Retired',
}

export function StatusBadge({ status }: { status: EmploymentStatus | AttendanceStatus | LeaveStatus }) {
  const label = status in labels ? labels[status as EmploymentStatus] : status.replaceAll('_', ' ').toLowerCase().replace(/\b\w/g, (letter) => letter.toUpperCase())
  return <span className={`status-badge status-${status.toLowerCase().replaceAll('_', '-')}`}><span className="status-dot" />{label}</span>
}
