import { AlertCircle, LoaderCircle, RefreshCw } from 'lucide-react'

export function LoadingState({ label = 'Loading information' }: { label?: string }) {
  return (
    <div className="state-panel" role="status">
      <LoaderCircle className="spin" size={24} aria-hidden="true" />
      <span>{label}</span>
    </div>
  )
}

export function ErrorState({ message, retry }: { message: string; retry?: () => void }) {
  return (
    <div className="state-panel state-error" role="alert">
      <AlertCircle size={23} aria-hidden="true" />
      <div>
        <strong>We couldn’t load this information</strong>
        <p>{message}</p>
        {retry && <button className="button button-secondary button-small" onClick={retry}><RefreshCw size={15} /> Try again</button>}
      </div>
    </div>
  )
}

export function EmptyState({ title, description }: { title: string; description: string }) {
  return (
    <div className="empty-state">
      <div className="empty-mark"><span /></div>
      <h3>{title}</h3>
      <p>{description}</p>
    </div>
  )
}
