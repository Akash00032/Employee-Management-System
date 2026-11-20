import { lazy, Suspense } from 'react'
import { createBrowserRouter, Navigate, RouterProvider } from 'react-router-dom'
import { ROLES } from './auth/roles'
import { AppLayout } from './components/layout/AppLayout'
import { ErrorState, LoadingState } from './components/common/Feedback'
import { getRoleDashboardPath, useAuth } from './context/AuthContext'
import { LoginPage } from './pages/auth/LoginPage'
import { ProtectedRoute, RoleRoute } from './routes/ProtectedRoute'

const DashboardPage = lazy(() => import('./pages/dashboard/DashboardPage').then((module) => ({ default: module.DashboardPage })))
const HRDashboardPage = lazy(() => import('./pages/dashboard/HRDashboardPage').then((module) => ({ default: module.HRDashboardPage })))
const EmployeeListPage = lazy(() => import('./pages/employee/EmployeeListPage').then((module) => ({ default: module.EmployeeListPage })))
const EmployeeFormPage = lazy(() => import('./pages/employee/EmployeeFormPage').then((module) => ({ default: module.EmployeeFormPage })))
const EmployeeProfilePage = lazy(() => import('./pages/employee/EmployeeProfilePage').then((module) => ({ default: module.EmployeeProfilePage })))
const DepartmentPage = lazy(() => import('./pages/department/DepartmentPage').then((module) => ({ default: module.DepartmentPage })))
const WorkShiftPage = lazy(() => import('./pages/workshift/WorkShiftPage').then((module) => ({ default: module.WorkShiftPage })))
const AttendancePage = lazy(() => import('./pages/attendance/AttendancePage').then((module) => ({ default: module.AttendancePage })))
const LeavePage = lazy(() => import('./pages/leave/LeavePage').then((module) => ({ default: module.LeavePage })))
const LeaveTypesPage = lazy(() => import('./pages/leave/LeaveTypesPage').then((module) => ({ default: module.LeaveTypesPage })))

const ForbiddenPage = () => <ErrorState message="Access denied. You do not have permission to access this area." />

export function App() {
  const { user, isAuthenticated } = useAuth()
  const dashboardPath = getRoleDashboardPath(user?.role)

  const router = createBrowserRouter([
    { path: '/', element: <Navigate to={isAuthenticated ? dashboardPath : '/login'} replace /> },
    { path: '/login', element: <LoginPage /> },
    { path: '/403', element: <ForbiddenPage /> },
    {
      element: <ProtectedRoute />,
      children: [
        {
          element: <AppLayout />,
          children: [
            { index: true, element: <Navigate to={dashboardPath} replace /> },
            { path: 'admin', element: <RoleRoute roles={[ROLES.ADMIN]} />, children: [
              { path: 'dashboard', element: <LazyPage><DashboardPage /></LazyPage> },
            ] },
            { path: 'hr', element: <RoleRoute roles={[ROLES.HR]} />, children: [
              { path: 'dashboard', element: <LazyPage><HRDashboardPage /></LazyPage> },
            ] },
            { path: 'manager', element: <RoleRoute roles={[ROLES.MANAGER]} />, children: [
              { path: 'dashboard', element: <LazyPage><DashboardPage /></LazyPage> },
            ] },
            { path: 'employee', element: <RoleRoute roles={[ROLES.EMPLOYEE]} />, children: [
              { path: 'dashboard', element: <LazyPage><DashboardPage /></LazyPage> },
            ] },
            { path: 'dashboard', element: <RoleRoute roles={[ROLES.USER]} />, children: [
              { index: true, element: <LazyPage><DashboardPage /></LazyPage> },
            ] },
            { element: <RoleRoute roles={[ROLES.ADMIN, ROLES.HR, ROLES.MANAGER]} />, children: [
              { path: 'employees', element: <LazyPage><EmployeeListPage /></LazyPage> },
              { path: 'employees/:id', element: <LazyPage><EmployeeProfilePage /></LazyPage> },
            ] },
            { element: <RoleRoute roles={[ROLES.ADMIN, ROLES.HR]} />, children: [
              { path: 'employees/create', element: <LazyPage><EmployeeFormPage /></LazyPage> },
              { path: 'employees/:id/edit', element: <LazyPage><EmployeeFormPage /></LazyPage> },
              { path: 'departments', element: <LazyPage><DepartmentPage /></LazyPage> },
              { path: 'work-shifts', element: <LazyPage><WorkShiftPage /></LazyPage> },
            ] },
            { element: <RoleRoute roles={[ROLES.ADMIN, ROLES.HR, ROLES.MANAGER]} />, children: [
              { path: 'attendance', element: <LazyPage><AttendancePage /></LazyPage> },
              { path: 'leave', element: <LazyPage><LeavePage /></LazyPage> },
            ] },
            { element: <RoleRoute roles={[ROLES.ADMIN, ROLES.HR]} />, children: [
              { path: 'leave/types', element: <LazyPage><LeaveTypesPage /></LazyPage> },
            ] },
            { path: 'forbidden', element: <ForbiddenPage /> },
            { path: '*', element: <ErrorState message="This page does not exist." /> },
          ],
        },
      ],
    },
    { path: '*', element: <Navigate to={isAuthenticated ? dashboardPath : '/login'} replace /> },
  ])

  return <RouterProvider router={router} />
}

function LazyPage({ children }: { children: React.ReactNode }) {
  return <Suspense fallback={<LoadingState label="Opening page" />}>{children}</Suspense>
}
