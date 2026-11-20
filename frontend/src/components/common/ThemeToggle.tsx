import { Monitor, Moon, Sun } from 'lucide-react'
import { useTheme, type ThemePreference } from '../../context/ThemeContext'

export function ThemeToggle({ className = '' }: { className?: string }) {
  const { theme, setTheme } = useTheme()
  const Icon = theme === 'system' ? Monitor : theme === 'dark' ? Moon : Sun

  return (
    <label className={`theme-control ${className}`}>
      <Icon size={18} aria-hidden="true" />
      <span className="sr-only">Color theme</span>
      <select
        aria-label="Color theme"
        value={theme}
        onChange={(event) => setTheme(event.target.value as ThemePreference)}
      >
        <option value="light">Light mode</option>
        <option value="dark">Dark mode</option>
        <option value="system">System</option>
      </select>
    </label>
  )
}
