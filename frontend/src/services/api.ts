import axios, { AxiosError, type InternalAxiosRequestConfig } from 'axios'
import type { ApiErrorPayload, LoginResponse } from '../types/api'

interface AuthTokens {
  accessToken: string
  refreshToken: string
}

const AUTH_TOKEN_STORAGE_KEY = 'ems:auth-tokens'

declare module 'axios' {
  export interface AxiosRequestConfig {
    _retry?: boolean
  }
}

function readStoredAuthTokens(): AuthTokens | null {
  if (typeof window === 'undefined') return null

  try {
    const raw = window.localStorage.getItem(AUTH_TOKEN_STORAGE_KEY)
    if (!raw) return null

    const parsed = JSON.parse(raw) as Partial<AuthTokens>
    if (!parsed.accessToken || !parsed.refreshToken) return null

    return parsed as AuthTokens
  } catch {
    return null
  }
}

let tokens: AuthTokens | null = readStoredAuthTokens()
let refreshInFlight: Promise<AuthTokens> | null = null

export function setAuthTokens(authTokens: AuthTokens | null) {
  tokens = authTokens

  if (typeof window === 'undefined') return

  if (authTokens) {
    window.localStorage.setItem(AUTH_TOKEN_STORAGE_KEY, JSON.stringify(authTokens))
    return
  }

  window.localStorage.removeItem(AUTH_TOKEN_STORAGE_KEY)
}

export function getAuthTokens() {
  return tokens
}

export const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '/api/v1',
  headers: { 'Content-Type': 'application/json' },
  timeout: 20_000,
})

const refreshClient = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '/api/v1',
  headers: { 'Content-Type': 'application/json' },
  timeout: 20_000,
})

api.interceptors.request.use((config: InternalAxiosRequestConfig) => {
  if (tokens?.accessToken) {
    config.headers.Authorization = `Bearer ${tokens.accessToken}`
  }
  return config
})

api.interceptors.response.use(
  (response) => response,
  async (error: AxiosError<ApiErrorPayload>) => {
    const request = error.config
    const url = request?.url ?? ''
    if (
      error.response?.status !== 401 ||
      !request ||
      request._retry ||
      url.includes('/auth/login') ||
      url.includes('/auth/refresh')
    ) {
      return Promise.reject(error)
    }

    if (!tokens?.refreshToken) {
      window.dispatchEvent(new Event('ems:session-expired'))
      return Promise.reject(error)
    }

    request._retry = true
    try {
      refreshInFlight ??= refreshClient
        .post<LoginResponse>('/auth/refresh', { refreshToken: tokens.refreshToken })
        .then(({ data }) => {
          const refreshed = { accessToken: data.accessToken, refreshToken: data.refreshToken }
          setAuthTokens(refreshed)
          return refreshed
        })
        .finally(() => {
          refreshInFlight = null
        })

      const refreshed = await refreshInFlight
      request.headers.Authorization = `Bearer ${refreshed.accessToken}`
      return api(request)
    } catch (refreshError) {
      setAuthTokens(null)
      window.dispatchEvent(new Event('ems:session-expired'))
      return Promise.reject(refreshError)
    }
  },
)

export function getApiErrorMessage(error: unknown): string {
  if (axios.isAxiosError<ApiErrorPayload>(error)) {
    const payload = error.response?.data
    if (payload?.message) return payload.message
    if (error.response?.status === 403) return 'You do not have permission to perform this action.'
    if (error.response?.status === 404) return 'The requested record could not be found.'
    if (error.response?.status === 409) return 'This change conflicts with an existing record.'
    if (!error.response) return 'Unable to reach the EMS server. Check your connection and try again.'
  }
  return 'Something went wrong. Please try again.'
}

export function getValidationErrors(error: unknown): Record<string, string> {
  if (!axios.isAxiosError<ApiErrorPayload>(error)) return {}
  const data = error.response?.data?.data
  if (!data || typeof data !== 'object' || Array.isArray(data)) return {}
  return Object.fromEntries(
    Object.entries(data).filter((entry): entry is [string, string] => typeof entry[1] === 'string'),
  )
}
