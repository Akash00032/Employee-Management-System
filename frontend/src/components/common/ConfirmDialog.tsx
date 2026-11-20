import { useEffect, useRef } from 'react'
import { AlertTriangle, X } from 'lucide-react'

interface ConfirmDialogProps {
  open: boolean
  title: string
  description: string
  confirmLabel?: string
  busy?: boolean
  onCancel: () => void
  onConfirm: () => void
}

export function ConfirmDialog({
  open,
  title,
  description,
  confirmLabel = 'Delete',
  busy = false,
  onCancel,
  onConfirm,
}: ConfirmDialogProps) {
  const dialogRef = useRef<HTMLDialogElement>(null)

  useEffect(() => {
    const dialog = dialogRef.current
    if (!dialog) return
    if (open && !dialog.open) dialog.showModal()
    if (!open && dialog.open) dialog.close()
  }, [open])

  return (
    <dialog
      ref={dialogRef}
      className="dialog"
      onCancel={(event) => {
        event.preventDefault()
        if (!busy) onCancel()
      }}
      aria-labelledby="confirm-title"
      aria-describedby="confirm-description"
    >
      <div className="dialog-header">
        <span className="dialog-icon"><AlertTriangle size={21} /></span>
        <button className="icon-button" onClick={onCancel} disabled={busy} aria-label="Close dialog"><X size={19} /></button>
      </div>
      <h2 id="confirm-title">{title}</h2>
      <p id="confirm-description">{description}</p>
      <div className="dialog-actions">
        <button className="button button-secondary" onClick={onCancel} disabled={busy}>Cancel</button>
        <button className="button button-danger" onClick={onConfirm} disabled={busy}>
          {busy ? 'Deleting…' : confirmLabel}
        </button>
      </div>
    </dialog>
  )
}
