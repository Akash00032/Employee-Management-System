import { api, setAuthTokens } from './api'
import type { LoginRequest, LoginResponse } from '../types/api'

export const authService = {
  async login(request: LoginRequest) {
    const { data } = await api.post<LoginResponse>('/auth/login', request)
    setAuthTokens({ accessToken: data.accessToken, refreshToken: data.refreshToken })
    return data
  },

  async logout(refreshToken: string) {
    await api.post('/auth/logout', { refreshToken })
    setAuthTokens(null)
  },

  async changePassword(currentPassword: string, newPassword: string, confirmPassword: string) {
    await api.post('/auth/change-password', { currentPassword, newPassword, confirmPassword })
  },
}
