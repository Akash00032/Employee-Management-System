import { Navigate, Outlet, useLocation } from 'react-router-dom'
import { LoadingState } from '../components/common/Feedback'
import { useAuth } from '../context/AuthContext'
import type { Role } from '../types/api'

export function ProtectedRoute() {
  const { isAuthenticated, isLoading } = useAuth()
  const location = useLocation()

  if (isLoading) {
    return <LoadingState label="Checking your session" />
  }

  return isAuthenticated ? <Outlet /> : <Navigate to="/login" replace state={{ from: location }} />
}

export function RoleRoute({ roles }: { roles: Role[] }) {
  const { hasAnyRole, isAuthenticated, isLoading } = useAuth()

  if (isLoading) {
    return <LoadingState label="Checking access" />
  }

  if (!isAuthenticated) {
    return <Navigate to="/login" replace state={{ from: useLocation().pathname }} />
  }

  return hasAnyRole(...roles) ? <Outlet /> : <Navigate to="/403" replace />
}
