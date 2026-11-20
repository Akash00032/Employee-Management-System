import { api } from './api'
import type { LeaveRequest, LeaveRequestInput, LeaveStatus } from '../types/api'

export const leaveService = {
  async requests(teamOnly = false) {
    const path = teamOnly ? '/leave-requests/team' : '/leave-requests'
    const { data } = await api.get<LeaveRequest[]>(path)
    return data
  },
  async byStatus(status: LeaveStatus) {
    const { data } = await api.get<LeaveRequest[]>(`/leave-requests/status/${status}`)
    return data
  },
  async create(request: LeaveRequestInput) {
    const { data } = await api.post<LeaveRequest>('/leave-requests', request)
    return data
  },
  async createMy(request: Omit<LeaveRequestInput, 'employeeId'>) {
    const { data } = await api.post<LeaveRequest>('/leave-requests/me', request)
    return data
  },
  async update(id: number, request: LeaveRequestInput) {
    const { data } = await api.put<LeaveRequest>(`/leave-requests/${id}`, request)
    return data
  },
  async approve(id: number, teamOnly = false) {
    const path = teamOnly
      ? `/leave-requests/team/${id}/approve`
      : `/leave-requests/${id}/approve`
    const { data } = await api.patch<LeaveRequest>(path)
    return data
  },
  async reject(id: number, reason: string, teamOnly = false) {
    const path = teamOnly
      ? `/leave-requests/team/${id}/reject`
      : `/leave-requests/${id}/reject`
    const { data } = await api.patch<LeaveRequest>(path, null, {
      params: { reason },
    })
    return data
  },
  async cancelMy(id: number) {
    await api.delete(`/leave-requests/me/${id}`)
  },
  async remove(id: number) {
    await api.delete(`/leave-requests/${id}`)
  },
}
