import { api } from './api'
import type { Department, DepartmentRequest, LeaveType, LeaveTypeRequest, WorkShift, WorkShiftRequest } from '../types/api'

export const organizationService = {
  async departments() {
    const { data } = await api.get<Department[]>('/departments')
    return data
  },

  async workShifts() {
    const { data } = await api.get<WorkShift[]>('/work-shifts/active')
    return data
  },

  async allWorkShifts() {
    const { data } = await api.get<WorkShift[]>('/work-shifts')
    return data
  },

  async createDepartment(request: DepartmentRequest) {
    const { data } = await api.post<Department>('/departments', request)
    return data
  },

  async updateDepartment(id: number, request: DepartmentRequest) {
    const { data } = await api.put<Department>(`/departments/${id}`, request)
    return data
  },

  async deleteDepartment(id: number) {
    await api.delete(`/departments/${id}`)
  },

  async createWorkShift(request: WorkShiftRequest) {
    const { data } = await api.post<WorkShift>('/work-shifts', request)
    return data
  },

  async updateWorkShift(id: number, request: WorkShiftRequest) {
    const { data } = await api.put<WorkShift>(`/work-shifts/${id}`, request)
    return data
  },

  async deleteWorkShift(id: number) {
    await api.delete(`/work-shifts/${id}`)
  },

  async leaveTypes() {
    const { data } = await api.get<LeaveType[]>('/leave-types')
    return data
  },

  async createLeaveType(request: LeaveTypeRequest) {
    const { data } = await api.post<LeaveType>('/leave-types', request)
    return data
  },

  async updateLeaveType(id: number, request: LeaveTypeRequest) {
    const { data } = await api.put<LeaveType>(`/leave-types/${id}`, request)
    return data
  },

  async deleteLeaveType(id: number) {
    await api.delete(`/leave-types/${id}`)
  },
}
