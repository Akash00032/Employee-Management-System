import { api } from './api'
import type { Attendance, AttendanceRequest, AttendanceStatus } from '../types/api'

export const attendanceService = {
  async list(teamOnly = false) {
    const path = teamOnly ? '/attendance/team' : '/attendance'
    const { data } = await api.get<Attendance[]>(path)
    return data
  },
  async byEmployeeBetween(employeeId: number, startDate: string, endDate: string) {
    const { data } = await api.get<Attendance[]>(`/attendance/employee/${employeeId}/between`, { params: { startDate, endDate } })
    return data
  },
  async byStatus(status: AttendanceStatus) {
    const { data } = await api.get<Attendance[]>(`/attendance/status/${status}`)
    return data
  },
  async create(request: AttendanceRequest) {
    const { data } = await api.post<Attendance>('/attendance', request)
    return data
  },
  async update(id: number, request: AttendanceRequest) {
    const { data } = await api.put<Attendance>(`/attendance/${id}`, request)
    return data
  },
  async remove(id: number) {
    await api.delete(`/attendance/${id}`)
  },
}
