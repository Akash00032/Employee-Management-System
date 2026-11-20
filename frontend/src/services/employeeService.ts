import { api } from './api'
import type { Employee, EmployeeRequest, SpringPage } from '../types/api'

export const employeeService = {
  async list(keyword?: string, teamOnly = false) {
    const { data } = teamOnly
      ? await api.get<Employee[]>('/employees/team', { params: keyword ? { keyword } : {} })
      : keyword
        ? await api.get<Employee[]>('/employees/search', { params: { keyword } })
        : await api.get<Employee[]>('/employees')
    return data
  },

  async directory(keyword: string, page: number, size: number, sort: 'name' | 'joiningDate') {
    const { data } = await api.get<SpringPage<Employee>>('/employees/directory', {
      params: { keyword, page, size, sort },
    })
    return data
  },

  async get(id: number, teamOnly = false) {
    const path = teamOnly ? `/employees/team/${id}` : `/employees/${id}`
    const { data } = await api.get<Employee>(path)
    return data
  },

  async byDesignation(designation: string, page = 0, size = 10) {
    const { data } = await api.get<SpringPage<Employee>>('/employees/designation', {
      params: { designation, page, size },
    })
    return data
  },

  async create(request: EmployeeRequest) {
    const { data } = await api.post<Employee>('/employees', request)
    return data
  },

  async update(id: number, request: EmployeeRequest) {
    const { data } = await api.put<Employee>(`/employees/${id}`, request)
    return data
  },

  async remove(id: number) {
    await api.delete(`/employees/${id}`)
  },
}
