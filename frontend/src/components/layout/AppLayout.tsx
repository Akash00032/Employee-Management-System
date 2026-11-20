import { useMemo, useState } from 'react'
import { NavLink, Outlet, useLocation, useNavigate } from 'react-router-dom'
import {
  Bell,
  BriefcaseBusiness,
  Building2,
  CalendarCheck2,
  ChevronDown,
  ChevronsLeft,
  ChevronsRight,
  Command,
  LayoutDashboard,
  LogOut,
  Menu,
  Search,
  UsersRound,
  Plane,
  X,
} from 'lucide-react'
import { ROLES } from '../../auth/roles'
import { getRoleDashboardPath, useAuth } from '../../context/AuthContext'
import { ThemeToggle } from '../common/ThemeToggle'
import { useToast } from '../common/ToastProvider'
import { getApiErrorMessage } from '../../services/api'

const pageNames: Record<string, string> = {
  dashboard: 'Overview',
  employees: 'Employees',
  departments: 'Departments',
  'work-shifts': 'Work shifts',
  attendance: 'Attendance',
  leave: 'Leave requests',
  admin: 'Admin',
  hr: 'HR',
  manager: 'Manager',
  employee: 'Employee',
}

export function AppLayout() {
  const [collapsed, setCollapsed] = useState(false)
  const [mobileOpen, setMobileOpen] = useState(false)
  const [profileOpen, setProfileOpen] = useState(false)
  const { user, logout } = useAuth()
  const notify = useToast()
  const location = useLocation()
  const navigate = useNavigate()
  const currentRole = user?.role ?? ROLES.USER
  const dashboardPath = getRoleDashboardPath(currentRole)
  const pageTitle = pageNames[location.pathname.split('/')[1] ?? ''] ?? 'People operations'

  const navigation = useMemo(() => {
    const items = [
      { label: 'Dashboard', path: dashboardPath, icon: LayoutDashboard, roles: [ROLES.ADMIN, ROLES.HR, ROLES.MANAGER, ROLES.EMPLOYEE, ROLES.USER] },
      { label: 'Employees', path: '/employees', icon: UsersRound, roles: [ROLES.ADMIN, ROLES.HR, ROLES.MANAGER] },
      { label: 'Departments', path: '/departments', icon: Building2, roles: [ROLES.ADMIN, ROLES.HR] },
      { label: 'Work shifts', path: '/work-shifts', icon: BriefcaseBusiness, roles: [ROLES.ADMIN, ROLES.HR] },
      { label: 'Attendance', path: '/attendance', icon: CalendarCheck2, roles: [ROLES.ADMIN, ROLES.HR, ROLES.MANAGER] },
      { label: 'Leave', path: '/leave', icon: Plane, roles: [ROLES.ADMIN, ROLES.HR, ROLES.MANAGER] },
      { label: 'Leave Types', path: '/leave/types', icon: Plane, roles: [ROLES.ADMIN, ROLES.HR] },
    ]

    return items.filter((item) => item.roles.includes(currentRole))
  }, [currentRole, dashboardPath])

  const endSession = async () => {
    try {
      await logout()
    } catch (error) {
      notify(getApiErrorMessage(error), 'error')
    }
    navigate('/login', { replace: true })
  }

  const sidebar = (
    <aside className={`sidebar ${collapsed ? 'sidebar-collapsed' : ''} ${mobileOpen ? 'sidebar-mobile-open' : ''}`}>
      <div className="brand-row">
        <div className="brand-mark"><span>e</span></div>
        {!collapsed && <div className="brand-copy"><strong>ems<span>.</span></strong><small>PEOPLE OPERATIONS</small></div>}
        <button className="icon-button sidebar-mobile-close" onClick={() => setMobileOpen(false)} aria-label="Close navigation"><X size={19} /></button>
      </div>
      <div className="workspace-switcher">
        <div className="workspace-icon"><Building2 size={17} /></div>
        {!collapsed && <><div className="workspace-copy"><strong>Your workspace</strong><span>Employee management</span></div><ChevronDown size={15} /></>}
      </div>
      <div className="nav-label">{!collapsed && 'WORKSPACE'}</div>
      <nav className="sidebar-nav" aria-label="Main navigation">
        {navigation.map((item) => {
          const Icon = item.icon
          return (
            <NavLink
              key={item.path}
              to={item.path}
              className={({ isActive }) => `nav-link ${isActive ? 'nav-link-active' : ''}`}
              onClick={() => setMobileOpen(false)}
              title={collapsed ? item.label : undefined}
            >
              <Icon size={19} strokeWidth={1.8} />
              {!collapsed && <span>{item.label}</span>}
            </NavLink>
          )
        })}
      </nav>
      <div className="sidebar-spacer" />
      {!collapsed && (
        <div className="help-card">
          <div className="help-avatar"><Command size={17} /></div>
          <strong>People first, always.</strong>
          <span>Your workspace for a better workday.</span>
        </div>
      )}
      <button className="collapse-button" onClick={() => setCollapsed((value) => !value)} aria-label={collapsed ? 'Expand sidebar' : 'Collapse sidebar'}>
        {collapsed ? <ChevronsRight size={18} /> : <><ChevronsLeft size={18} /><span>Collapse menu</span></>}
      </button>
    </aside>
  )

  return (
    <div className={`app-shell ${collapsed ? 'app-shell-collapsed' : ''}`}>
      {sidebar}
      {mobileOpen && <button className="mobile-backdrop" aria-label="Close navigation" onClick={() => setMobileOpen(false)} />}
      <div className="main-column">
        <header className="topbar">
          <button className="icon-button mobile-menu-button" onClick={() => setMobileOpen(true)} aria-label="Open navigation"><Menu size={21} /></button>
          <div className="topbar-context"><span>Workspace</span><span className="crumb-divider">/</span><strong>{pageTitle}</strong></div>
          <label className="global-search">
            <Search size={17} />
            <input
              aria-label="Search employees"
              placeholder="Search employees..."
              onKeyDown={(event) => {
                if (event.key === 'Enter') navigate(`/employees?q=${encodeURIComponent(event.currentTarget.value)}`)
              }}
            />
            <kbd>↵</kbd>
          </label>
          <div className="topbar-actions">
            <ThemeToggle />
            <button className="icon-button" aria-label="Notifications are not configured in this API" title="Notifications are not configured in this API">
              <Bell size={19} />
            </button>
            <div className="profile-menu-wrap">
              <button className="profile-trigger" onClick={() => setProfileOpen((value) => !value)} aria-expanded={profileOpen}>
                <span className="avatar">{user?.employeeName?.split(' ').map((part) => part[0]).slice(0, 2).join('') ?? 'U'}</span>
                <span className="profile-text"><strong>{user?.employeeName ?? 'User'}</strong><small>{user?.role ?? currentRole}</small></span>
                <ChevronDown size={15} />
              </button>
              {profileOpen && (
                <div className="profile-dropdown">
                  <div className="dropdown-user"><strong>{user?.employeeName ?? 'User'}</strong><span>{user?.username ?? 'unknown'}</span></div>
                  <span className="role-badge">{user?.role ?? currentRole}</span>
                  <button onClick={() => void endSession()}><LogOut size={16} /> Sign out</button>
                </div>
              )}
            </div>
          </div>
        </header>
        <main className="main-content"><Outlet /></main>
      </div>
    </div>
  )
}
