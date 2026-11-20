import { createContext, useCallback, useContext, useEffect, useMemo, useState } from 'react'
import { ROLES } from '../auth/roles'
import { authService } from '../services/authService'
import { getAuthTokens, setAuthTokens } from '../services/api'
import type { LoginRequest, LoginResponse, Role } from '../types/api'

const USER_STORAGE_KEY = 'ems:user'

interface AuthContextValue {
  user: LoginResponse | null
  accessToken: string | null
  isAuthenticated: boolean
  isLoading: boolean
  hasRole: (role: Role) => boolean
  hasAnyRole: (...roles: Role[]) => boolean
  login: (request: LoginRequest) => Promise<LoginResponse>
  logout: () => Promise<void>
}

const AuthContext = createContext<AuthContextValue | null>(null)

export function getRoleDashboardPath(role?: Role | null) {
  switch (role) {
    case ROLES.ADMIN:
      return '/admin/dashboard'
    case ROLES.HR:
      return '/hr/dashboard'
    case ROLES.MANAGER:
      return '/manager/dashboard'
    case ROLES.EMPLOYEE:
      return '/employee/dashboard'
    case ROLES.USER:
      return '/dashboard'
    default:
      return '/403'
  }
}

function readStoredUser(): LoginResponse | null {
  if (typeof window === 'undefined') return null

  try {
    const raw = window.localStorage.getItem(USER_STORAGE_KEY)
    if (!raw) return null

    const parsed = JSON.parse(raw) as Partial<LoginResponse>
    if (!parsed || !parsed.username || !parsed.role) {
      return null
    }

    return parsed as LoginResponse
  } catch {
    return null
  }
}

function persistUser(user: LoginResponse | null) {
  if (typeof window === 'undefined') return

  if (user) {
    window.localStorage.setItem(USER_STORAGE_KEY, JSON.stringify(user))
    return
  }

  window.localStorage.removeItem(USER_STORAGE_KEY)
}

export function AuthProvider({ children }: { children: React.ReactNode }) {
  const [user, setUser] = useState<LoginResponse | null>(() => readStoredUser())
  const [isLoading, setIsLoading] = useState(true)

  const normalizeStoredRole = useCallback((role: string | undefined) => {
    const normalized = String(role ?? '').toUpperCase()
    if (normalized === ROLES.ADMIN || normalized === ROLES.HR || normalized === ROLES.MANAGER || normalized === ROLES.EMPLOYEE || normalized === ROLES.USER) {
      return normalized as Role
    }
    return null
  }, [])

  const logout = useCallback(async () => {
    const refreshToken = getAuthTokens()?.refreshToken
    try {
      if (refreshToken) await authService.logout(refreshToken)
    } finally {
      setAuthTokens(null)
      persistUser(null)
      setUser(null)
    }
  }, [])

  useEffect(() => {
    const expireSession = () => {
      setAuthTokens(null)
      persistUser(null)
      setUser(null)
    }

    const storedUser = readStoredUser()
    const storedTokens = getAuthTokens()
    const normalizedRole = storedUser ? normalizeStoredRole(storedUser.role) : null

    if (storedTokens?.accessToken && storedUser && normalizedRole) {
      setUser({ ...storedUser, role: normalizedRole })
    } else {
      setAuthTokens(null)
      persistUser(null)
      setUser(null)
    }

    setIsLoading(false)
    window.addEventListener('ems:session-expired', expireSession)
    return () => window.removeEventListener('ems:session-expired', expireSession)
  }, [normalizeStoredRole])

  const login = useCallback(async (request: LoginRequest): Promise<LoginResponse> => {
    const response = await authService.login(request)
    persistUser(response)
    setUser(response)
    return response
  }, [])

  const hasRole = useCallback(
    (role: Role) => user?.role === role,
    [user],
  )

  const hasAnyRole = useCallback(
    (...roles: Role[]) => Boolean(user && roles.includes(user.role)),
    [user],
  )

  const accessToken = getAuthTokens()?.accessToken ?? null

  const value = useMemo(
    () => ({ user, accessToken, isAuthenticated: Boolean(user), isLoading, hasRole, hasAnyRole, login, logout }),
    [user, accessToken, isLoading, hasRole, hasAnyRole, login, logout],
  )

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}

export function useAuth() {
  const value = useContext(AuthContext)
  if (!value) throw new Error('useAuth must be used within AuthProvider')
  return value
}
