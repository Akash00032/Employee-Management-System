import { api } from './api'
import { attendanceService } from './attendanceService'
import { dashboardService } from './dashboardService'
import { employeeService } from './employeeService'
import { leaveService } from './leaveService'
import { organizationService } from './organizationService'
import type { Attendance, DashboardSummary, Department, Employee, LeaveRequest, WorkShift } from '../types/api'

export interface HrDashboardData {
  summary: DashboardSummary | null
  employees: Employee[]
  departments: Department[]
  attendance: Attendance[]
  leaveRequests: LeaveRequest[]
  payrolls: Array<Record<string, unknown>>
  projects: Array<Record<string, unknown>>
  documents: Array<Record<string, unknown>>
  assets: Array<Record<string, unknown>>
  workShifts: WorkShift[]
  partialFailures: string[]
}

export const hrDashboardService = {
  async load(): Promise<HrDashboardData> {
    const requests = [
      { label: 'summary', promise: dashboardService.summary().then((data) => ({ kind: 'summary', value: data })) },
      { label: 'employees', promise: employeeService.list().then((data) => ({ kind: 'employees', value: data })) },
      { label: 'departments', promise: organizationService.departments().then((data) => ({ kind: 'departments', value: data })) },
      { label: 'attendance', promise: attendanceService.list().then((data) => ({ kind: 'attendance', value: data })) },
      { label: 'leaveRequests', promise: leaveService.requests(false).then((data) => ({ kind: 'leaveRequests', value: data })) },
      { label: 'payrolls', promise: api.get('/payrolls').then((response) => ({ kind: 'payrolls', value: normalizeList(response.data) })) },
      { label: 'projects', promise: api.get('/projects').then((response) => ({ kind: 'projects', value: normalizeList(response.data) })) },
      { label: 'documents', promise: api.get('/employee-documents').then((response) => ({ kind: 'documents', value: normalizeList(response.data) })) },
      { label: 'assets', promise: api.get('/employee-assets').then((response) => ({ kind: 'assets', value: normalizeList(response.data) })) },
      { label: 'workShifts', promise: organizationService.allWorkShifts().then((data) => ({ kind: 'workShifts', value: data })) },
    ]

    const settled = await Promise.allSettled(requests.map((request) => request.promise))

    const data: HrDashboardData = {
      summary: null,
      employees: [],
      departments: [],
      attendance: [],
      leaveRequests: [],
      payrolls: [],
      projects: [],
      documents: [],
      assets: [],
      workShifts: [],
      partialFailures: [],
    }

    for (let index = 0; index < settled.length; index += 1) {
      const result = settled[index]
      const label = requests[index].label
      if (result.status === 'fulfilled') {
        const value = result.value.value
        switch (result.value.kind) {
          case 'summary':
            data.summary = value as DashboardSummary
            break
          case 'employees':
            data.employees = value as Employee[]
            break
          case 'departments':
            data.departments = value as Department[]
            break
          case 'attendance':
            data.attendance = value as Attendance[]
            break
          case 'leaveRequests':
            data.leaveRequests = value as LeaveRequest[]
            break
          case 'payrolls':
            data.payrolls = value as Array<Record<string, unknown>>
            break
          case 'projects':
            data.projects = value as Array<Record<string, unknown>>
            break
          case 'documents':
            data.documents = value as Array<Record<string, unknown>>
            break
          case 'assets':
            data.assets = value as Array<Record<string, unknown>>
            break
          case 'workShifts':
            data.workShifts = value as WorkShift[]
            break
          default:
            break
        }
      } else {
        data.partialFailures.push(`Unable to load ${label}.`)
      }
    }

    return data
  },
}

function normalizeList(value: unknown): Array<Record<string, unknown>> {
  if (Array.isArray(value)) return value as Array<Record<string, unknown>>
  if (value && typeof value === 'object' && 'content' in value && Array.isArray((value as { content?: unknown[] }).content)) {
    return (value as { content: Record<string, unknown>[] }).content
  }
  return []
}
