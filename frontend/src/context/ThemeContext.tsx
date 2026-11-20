import { createContext, useContext, useEffect, useMemo, useState } from 'react'

export type ThemePreference = 'light' | 'dark' | 'system'
export type ResolvedTheme = Exclude<ThemePreference, 'system'>

type ThemeContextValue = {
  theme: ThemePreference
  resolvedTheme: ResolvedTheme
  setTheme: (theme: ThemePreference) => void
}

const ThemeContext = createContext<ThemeContextValue | null>(null)

function getStoredTheme(): ThemePreference {
  const saved = localStorage.getItem('ems-theme')
  return saved === 'light' || saved === 'dark' || saved === 'system' ? saved : 'system'
}

function getSystemTheme(): ResolvedTheme {
  return window.matchMedia('(prefers-color-scheme: dark)').matches ? 'dark' : 'light'
}

export function ThemeProvider({ children }: { children: React.ReactNode }) {
  const [theme, setTheme] = useState<ThemePreference>(getStoredTheme)
  const [systemTheme, setSystemTheme] = useState<ResolvedTheme>(getSystemTheme)
  const resolvedTheme = theme === 'system' ? systemTheme : theme

  useEffect(() => {
    const mediaQuery = window.matchMedia('(prefers-color-scheme: dark)')
    const updateSystemTheme = (event: MediaQueryListEvent) => setSystemTheme(event.matches ? 'dark' : 'light')
    mediaQuery.addEventListener('change', updateSystemTheme)
    return () => mediaQuery.removeEventListener('change', updateSystemTheme)
  }, [])

  useEffect(() => {
    const root = document.documentElement
    root.dataset.theme = resolvedTheme
    root.classList.toggle('dark', resolvedTheme === 'dark')
    document.querySelector<HTMLMetaElement>('meta[name="theme-color"]')?.setAttribute(
      'content',
      resolvedTheme === 'dark' ? '#020617' : '#F8FAFC',
    )
  }, [resolvedTheme])

  useEffect(() => {
    localStorage.setItem('ems-theme', theme)
  }, [theme])

  const value = useMemo(() => ({ theme, resolvedTheme, setTheme }), [theme, resolvedTheme])
  return <ThemeContext.Provider value={value}>{children}</ThemeContext.Provider>
}

export function useTheme() {
  const value = useContext(ThemeContext)
  if (!value) throw new Error('useTheme must be used within ThemeProvider')
  return value
}
