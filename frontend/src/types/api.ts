export type Role = 'ADMIN' | 'HR' | 'MANAGER' | 'EMPLOYEE' | 'USER'
export type Gender = 'MALE' | 'FEMALE' | 'OTHER'
export type EmploymentType = 'FULL_TIME' | 'PART_TIME' | 'CONTRACT' | 'INTERN' | 'TEMPORARY'
export type EmploymentStatus = 'ACTIVE' | 'ON_LEAVE' | 'SUSPENDED' | 'RESIGNED' | 'TERMINATED' | 'RETIRED'

export interface LoginRequest {
  username: string
  password: string
}

export interface LoginResponse {
  accessToken: string
  refreshToken: string
  tokenType: string
  expiresIn: number
  userId: number
  employeeId: number
  username: string
  employeeName: string
  role: Role
}

export interface EmployeeRequest {
  employeeCode: string
  firstName: string
  lastName: string
  email: string
  phone: string
  dateOfBirth: string | null
  gender: Gender | null
  joiningDate: string
  designation: string
  employmentType: EmploymentType
  employmentStatus: EmploymentStatus
  salary: number
  departmentId: number
  managerId: number | null
  workShiftId: number | null
}

export interface Employee {
  id: number
  employeeCode: string
  firstName: string
  lastName: string
  fullName: string
  email: string
  phone: string
  dateOfBirth: string | null
  gender: Gender | null
  joiningDate: string
  designation: string
  employmentType: EmploymentType
  employmentStatus: EmploymentStatus
  salary: number | null
  departmentId: number | null
  departmentCode: string | null
  departmentName: string | null
  managerId: number | null
  managerName: string | null
  workShiftId: number | null
  workShiftName: string | null
}

export interface DepartmentRequest {
  code: string
  name: string
  description: string | null
  active: boolean | null
}

export interface Department {
  id: number
  code: string
  name: string
  description: string | null
  active: boolean
  employeeCount: number
}

export interface WorkShift {
  id: number
  name: string
  startTime: string
  endTime: string
  breakStartTime: string | null
  breakEndTime: string | null
  overnight: boolean
  active: boolean
  employeeCount: number
}

export interface WorkShiftRequest {
  name: string
  startTime: string
  endTime: string
  breakStartTime: string | null
  breakEndTime: string | null
  overnight: boolean | null
  active: boolean | null
}

export interface DashboardSummary {
  totalEmployees: number
  activeEmployees: number
  inactiveEmployees: number
  totalDepartments: number
  totalProjects: number
  activeProjects: number
  pendingLeaveRequests: number
  presentToday: number
  absentToday: number
  onLeaveToday: number
  totalPayroll: number | null
  departmentStats: { departmentId: number; departmentCode: string; departmentName: string; employeeCount: number }[]
  attendanceStats: { status: string; count: number; percentage: number }[]
  employeeStatusStats: { status: EmploymentStatus; count: number; percentage: number }[]
  recentEmployees: {
    id: number
    employeeCode: string
    employeeName: string
    designation: string
    departmentName: string | null
    joiningDate: string
  }[]
}

export type AttendanceStatus = 'PRESENT' | 'ABSENT' | 'HALF_DAY' | 'LATE' | 'WORK_FROM_HOME' | 'ON_LEAVE'
export type LeaveStatus = 'PENDING' | 'APPROVED' | 'REJECTED' | 'CANCELLED'

export interface AttendanceRequest {
  employeeId: number
  attendanceDate: string
  checkIn: string | null
  checkOut: string | null
  status: AttendanceStatus
  remarks: string | null
}

export interface Attendance {
  id: number
  employeeId: number
  employeeCode: string
  employeeName: string
  attendanceDate: string
  checkIn: string | null
  checkOut: string | null
  workingHours: number | null
  status: AttendanceStatus
  remarks: string | null
}

export interface LeaveType {
  id: number
  code: string
  name: string
  maxDays: number
  paidLeave: boolean
  active: boolean
  description: string | null
}

export interface LeaveTypeRequest {
  code: string
  name: string
  maxDays: number
  paidLeave: boolean | null
  active: boolean | null
  description: string | null
}

export interface LeaveRequest {
  id: number
  employeeId: number
  employeeCode: string
  employeeName: string
  leaveTypeId: number
  leaveTypeCode: string
  leaveTypeName: string
  startDate: string
  endDate: string
  totalDays: number
  reason: string
  status: LeaveStatus
  approvedById: number | null
  approvedByName: string | null
  appliedAt: string
  approvedAt: string | null
  rejectionReason: string | null
}

export interface LeaveRequestInput {
  employeeId: number
  leaveTypeId: number
  startDate: string
  endDate: string
  reason: string | null
}

export interface ApiErrorPayload {
  success?: boolean
  message?: string
  data?: Record<string, string> | unknown
  timestamp?: string
}

export interface SpringPage<T> {
  content: T[]
  totalElements: number
  totalPages: number
  number: number
  size: number
  first: boolean
  last: boolean
}
