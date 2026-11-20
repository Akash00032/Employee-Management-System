import { createContext, useCallback, useContext, useMemo, useState } from 'react'
import { CheckCircle2, CircleAlert, X } from 'lucide-react'

type ToastKind = 'success' | 'error'
interface ToastItem {
  id: number
  message: string
  kind: ToastKind
}

const ToastContext = createContext<{ notify: (message: string, kind?: ToastKind) => void } | null>(null)

export function ToastProvider({ children }: { children: React.ReactNode }) {
  const [items, setItems] = useState<ToastItem[]>([])
  const dismiss = useCallback((id: number) => {
    setItems((current) => current.filter((item) => item.id !== id))
  }, [])

  const notify = useCallback((message: string, kind: ToastKind = 'success') => {
    const id = Date.now() + Math.random()
    setItems((current) => [...current, { id, message, kind }])
    window.setTimeout(() => dismiss(id), 4500)
  }, [dismiss])

  const value = useMemo(() => ({ notify }), [notify])
  return (
    <ToastContext.Provider value={value}>
      {children}
      <div className="toast-region" aria-live="polite" aria-atomic="false">
        {items.map((item) => {
          const Icon = item.kind === 'success' ? CheckCircle2 : CircleAlert
          return (
            <div className={`toast toast-${item.kind}`} key={item.id} role="status">
              <Icon size={19} aria-hidden="true" />
              <span>{item.message}</span>
              <button className="icon-button toast-close" onClick={() => dismiss(item.id)} aria-label="Dismiss notification">
                <X size={16} />
              </button>
            </div>
          )
        })}
      </div>
    </ToastContext.Provider>
  )
}

export function useToast() {
  const value = useContext(ToastContext)
  if (!value) throw new Error('useToast must be used within ToastProvider')
  return value.notify
}
