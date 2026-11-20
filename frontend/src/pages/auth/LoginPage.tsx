import { useState } from 'react'
import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { ArrowRight, Eye, EyeOff, LockKeyhole, UsersRound } from 'lucide-react'
import { z } from 'zod'
import { Navigate, useLocation, useNavigate } from 'react-router-dom'
import { getRoleDashboardPath, useAuth } from '../../context/AuthContext'
import { getApiErrorMessage } from '../../services/api'
import { ThemeToggle } from '../../components/common/ThemeToggle'

const loginSchema = z.object({
  username: z.string().min(1, 'Enter your username'),
  password: z.string().min(1, 'Enter your password'),
})

type LoginValues = z.infer<typeof loginSchema>

export function LoginPage() {
  const { login, isAuthenticated, user } = useAuth()
  const navigate = useNavigate()
  const location = useLocation()

  if (isAuthenticated && user) {
    const intendedPath = getRoleDashboardPath(user.role)
    return <Navigate to={intendedPath} replace />
  }
  const [showPassword, setShowPassword] = useState(false)
  const [submitError, setSubmitError] = useState('')
  const {
    register,
    handleSubmit,
    formState: { errors, isSubmitting },
  } = useForm<LoginValues>({ resolver: zodResolver(loginSchema) })

  const submit = handleSubmit(async (values) => {
    setSubmitError('')
    try {
      const response = await login(values)
      const from = (location.state as { from?: { pathname?: string } } | null)?.from?.pathname
      const destination = from && from !== '/login' ? from : getRoleDashboardPath(response.role)
      navigate(destination, { replace: true })
    } catch (error) {
      setSubmitError(getApiErrorMessage(error))
    }
  })

  return (
    <main className="login-screen">
      <section className="login-story">
        <div className="login-brand">
          <span className="brand-mark"><span>e</span></span>
          <span className="login-brand-copy"><strong>ems.</strong><small>PEOPLE OPERATIONS</small></span>
        </div>
        <div className="login-quote">
          <span className="login-quote-mark">“</span>
          <h1>People are your greatest work.</h1>
          <p>A calmer, clearer way to look after the people who make your organization what it is.</p>
          <div className="login-perks"><span><UsersRound size={14} /> People, in focus</span><span><LockKeyhole size={14} /> Secure workspace</span></div>
        </div>
        <span className="login-footnote">© {new Date().getFullYear()} EMS · Built around people</span>
      </section>

      <section className="login-form-wrap">
        <ThemeToggle className="login-theme-control" />
        <div className="login-form-card">
          <div className="login-welcome"><span>SECURE WORKSPACE</span><h2>Welcome back</h2><p>Sign in to your organization workspace.</p></div>
          <form className="login-form" onSubmit={(event) => void submit(event)} noValidate>
            <label className="form-field">
              <span className="field-label">Username</span>
              <input className="text-input" autoComplete="username" placeholder="Your username" {...register('username')} />
              {errors.username && <span className="field-error">{errors.username.message}</span>}
            </label>
            <label className="form-field">
              <span className="field-label">Password</span>
              <span className="password-wrap">
                <input
                className="text-input"
                id="password"
                type={showPassword ? 'text' : 'password'}
                autoComplete="current-password"
                placeholder="Enter your password"
                {...register('password')}
              />
                <button type="button" className="icon-button password-toggle" onClick={() => setShowPassword((value) => !value)} aria-label={showPassword ? 'Hide password' : 'Show password'}>
                  {showPassword ? <EyeOff size={18} /> : <Eye size={18} />}
                </button>
              </span>
              {errors.password && <span className="field-error">{errors.password.message}</span>}
            </label>
            {submitError && <div className="form-alert" role="alert">{submitError}</div>}
            <button className="button button-primary login-submit" type="submit" disabled={isSubmitting}>
              {isSubmitting ? 'Signing in…' : <>Sign in to workspace <ArrowRight size={17} /> </>}
            </button>
          </form>
          <div className="login-footer">Access is managed by your EMS administrator.</div>
        </div>
      </section>
    </main>
  )
}
