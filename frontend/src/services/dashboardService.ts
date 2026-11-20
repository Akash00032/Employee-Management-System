import { api } from './api'
import type { DashboardSummary } from '../types/api'

export const dashboardService = {
  async summary() {
    const { data } = await api.get<DashboardSummary>('/dashboard')
    return data
  },
}
