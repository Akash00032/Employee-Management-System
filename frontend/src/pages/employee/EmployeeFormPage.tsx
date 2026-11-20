import { useEffect } from 'react'
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { zodResolver } from '@hookform/resolvers/zod'
import { ArrowLeft, Check } from 'lucide-react'
import { useForm, type FieldPath } from 'react-hook-form'
import { Link, useNavigate, useParams } from 'react-router-dom'
import { z } from 'zod'
import { ErrorState, LoadingState } from '../../components/common/Feedback'
import { PageHeader } from '../../components/common/PageHeader'
import { useToast } from '../../components/common/ToastProvider'
import { employeeService } from '../../services/employeeService'
import { organizationService } from '../../services/organizationService'
import { getApiErrorMessage, getValidationErrors } from '../../services/api'
import type { EmployeeRequest, EmploymentStatus, EmploymentType, Gender } from '../../types/api'

const formSchema = z.object({
  employeeCode: z.string().trim().min(1, 'Employee code is required').max(30),
  firstName: z.string().trim().min(2, 'Use at least 2 characters').max(50),
  lastName: z.string().trim().min(2, 'Use at least 2 characters').max(50),
  email: z.email('Enter a valid email address').max(100),
  phone: z.string().trim().min(10, 'Use at least 10 characters').max(15),
  dateOfBirth: z.string().optional(),
  gender: z.enum(['MALE', 'FEMALE', 'OTHER']).or(z.literal('')),
  joiningDate: z.string().min(1, 'Joining date is required'),
  designation: z.string().trim().min(1, 'Designation is required').max(100),
  employmentType: z.enum(['FULL_TIME', 'PART_TIME', 'CONTRACT', 'INTERN', 'TEMPORARY']),
  employmentStatus: z.enum(['ACTIVE', 'ON_LEAVE', 'SUSPENDED', 'RESIGNED', 'TERMINATED', 'RETIRED']),
  salary: z.string().refine((value) => Number.isFinite(Number(value)) && Number(value) > 0, 'Salary must be greater than zero').transform(Number),
  departmentId: z.string().min(1, 'Choose a department').transform(Number).refine(Number.isInteger, 'Choose a valid department').refine((value) => value > 0, 'Choose a valid department'),
  managerId: z.string(),
  workShiftId: z.string(),
})

type EmployeeFormInput = z.input<typeof formSchema>
type EmployeeFormValues = z.output<typeof formSchema>
const enumLabels: Record<string, string> = {
  FULL_TIME: 'Full-time',
  PART_TIME: 'Part-time',
  CONTRACT: 'Contract',
  INTERN: 'Intern',
  TEMPORARY: 'Temporary',
  ON_LEAVE: 'On leave',
  SUSPENDED: 'Suspended',
  RESIGNED: 'Resigned',
  TERMINATED: 'Terminated',
  RETIRED: 'Retired',
}
const today = () => {
  const date = new Date()
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
}

export function EmployeeFormPage() {
  const { id } = useParams()
  const employeeId = id ? Number(id) : undefined
  const isEditing = employeeId !== undefined
  const navigate = useNavigate()
  const queryClient = useQueryClient()
  const notify = useToast()

  const employeeQuery = useQuery({
    queryKey: ['employees', employeeId],
    queryFn: () => employeeService.get(employeeId!),
    enabled: isEditing && Number.isInteger(employeeId) && employeeId! > 0,
  })
  const departmentsQuery = useQuery({
    queryKey: ['departments'],
    queryFn: organizationService.departments,
  })
  const shiftsQuery = useQuery({
    queryKey: ['work-shifts', 'active'],
    queryFn: organizationService.workShifts,
  })
  const managersQuery = useQuery({
    queryKey: ['employees', { keyword: '' }],
    queryFn: () => employeeService.list(),
  })

  const {
    register,
    handleSubmit,
    reset,
    setError,
    formState: { errors, isSubmitting },
  } = useForm<EmployeeFormInput, unknown, EmployeeFormValues>({
    resolver: zodResolver(formSchema),
    defaultValues: {
      employeeCode: '',
      firstName: '',
      lastName: '',
      email: '',
      phone: '',
      dateOfBirth: '',
      gender: '',
      joiningDate: today(),
      designation: '',
      employmentType: 'FULL_TIME',
      employmentStatus: 'ACTIVE',
      salary: '1',
      departmentId: '',
      managerId: '',
      workShiftId: '',
    },
  })

  useEffect(() => {
    const employee = employeeQuery.data
    if (!employee) return
    reset({
      employeeCode: employee.employeeCode,
      firstName: employee.firstName,
      lastName: employee.lastName,
      email: employee.email,
      phone: employee.phone,
      dateOfBirth: employee.dateOfBirth ?? '',
      gender: employee.gender ?? '',
      joiningDate: employee.joiningDate,
      designation: employee.designation,
      employmentType: employee.employmentType,
      employmentStatus: employee.employmentStatus,
      salary: employee.salary?.toString() ?? '',
      departmentId: employee.departmentId?.toString() ?? '',
      managerId: employee.managerId?.toString() ?? '',
      workShiftId: employee.workShiftId?.toString() ?? '',
    })
  }, [employeeQuery.data, reset])

  const mutation = useMutation({
    mutationFn: (request: EmployeeRequest) => isEditing
      ? employeeService.update(employeeId!, request)
      : employeeService.create(request),
    onSuccess: async (employee) => {
      await queryClient.invalidateQueries({ queryKey: ['employees'] })
      await queryClient.invalidateQueries({ queryKey: ['dashboard'] })
      notify(isEditing ? 'Employee details updated.' : 'Employee added to the directory.')
      navigate(`/employees/${employee.id}`, { replace: true })
    },
    onError: (error) => {
      const validation = getValidationErrors(error)
      const fields = ['employeeCode', 'firstName', 'lastName', 'email', 'phone', 'dateOfBirth', 'gender', 'joiningDate', 'designation', 'employmentType', 'employmentStatus', 'salary', 'departmentId', 'managerId', 'workShiftId'] as const
      let mapped = false
      fields.forEach((field) => {
        if (validation[field]) {
          setError(field as FieldPath<EmployeeFormInput>, { message: validation[field] })
          mapped = true
        }
      })
      if (!mapped) notify(getApiErrorMessage(error), 'error')
    },
  })

  const submit = handleSubmit((values) => {
    const request: EmployeeRequest = {
      employeeCode: values.employeeCode.trim(),
      firstName: values.firstName.trim(),
      lastName: values.lastName.trim(),
      email: values.email.trim(),
      phone: values.phone.trim(),
      dateOfBirth: values.dateOfBirth || null,
      gender: values.gender ? values.gender as Gender : null,
      joiningDate: values.joiningDate,
      designation: values.designation.trim(),
      employmentType: values.employmentType as EmploymentType,
      employmentStatus: values.employmentStatus as EmploymentStatus,
      salary: values.salary,
      departmentId: values.departmentId,
      managerId: values.managerId ? Number(values.managerId) : null,
      workShiftId: values.workShiftId ? Number(values.workShiftId) : null,
    }
    mutation.mutate(request)
  })

  if (isEditing && (!Number.isInteger(employeeId) || employeeId! <= 0)) {
    return <ErrorState message="The employee ID in this URL is invalid." />
  }
  if (isEditing && employeeQuery.isLoading) return <LoadingState label="Loading employee record" />
  if (isEditing && employeeQuery.isError) {
    return <ErrorState message={getApiErrorMessage(employeeQuery.error)} retry={() => void employeeQuery.refetch()} />
  }
  if (departmentsQuery.isError || shiftsQuery.isError || managersQuery.isError) {
    const error = departmentsQuery.error ?? shiftsQuery.error ?? managersQuery.error
    return <ErrorState message={getApiErrorMessage(error)} retry={() => {
      void departmentsQuery.refetch()
      void shiftsQuery.refetch()
      void managersQuery.refetch()
    }} />
  }

  return (
    <div className="page-stack">
      <Link to={isEditing ? `/employees/${employeeId}` : '/employees'} className="back-link"><ArrowLeft size={16} /> {isEditing ? 'Back to profile' : 'Back to directory'}</Link>
      <PageHeader
        eyebrow={isEditing ? 'EMPLOYEE PROFILE' : 'GROW YOUR TEAM'}
        title={isEditing ? 'Edit employee' : 'Add a new employee'}
        description={isEditing ? 'Update employment and contact information.' : 'Add someone new to your people directory.'}
      />
      <form className="panel employee-form" onSubmit={(event) => void submit(event)} noValidate>
        <FormSection title="Personal details" description="Basic information used to identify and contact this employee.">
          <Field label="First name" error={errors.firstName?.message}><input className="text-input" autoComplete="given-name" {...register('firstName')} /></Field>
          <Field label="Last name" error={errors.lastName?.message}><input className="text-input" autoComplete="family-name" {...register('lastName')} /></Field>
          <Field label="Work email" error={errors.email?.message}><input className="text-input" type="email" autoComplete="email" {...register('email')} /></Field>
          <Field label="Phone number" error={errors.phone?.message}><input className="text-input" type="tel" autoComplete="tel" {...register('phone')} /></Field>
          <Field label="Date of birth" error={errors.dateOfBirth?.message}><input className="text-input" type="date" {...register('dateOfBirth')} /></Field>
          <Field label="Gender" error={errors.gender?.message}>
            <select className="text-input" {...register('gender')}><option value="">Not specified</option><option value="FEMALE">Female</option><option value="MALE">Male</option><option value="OTHER">Other</option></select>
          </Field>
        </FormSection>

        <FormSection title="Employment details" description="Role, team and compensation information.">
          <Field label="Employee code" error={errors.employeeCode?.message}><input className="text-input" {...register('employeeCode')} /></Field>
          <Field label="Job title" error={errors.designation?.message}><input className="text-input" placeholder="e.g. Product designer" {...register('designation')} /></Field>
          <Field label="Joining date" error={errors.joiningDate?.message}><input className="text-input" type="date" {...register('joiningDate')} /></Field>
          <Field label="Employment type" error={errors.employmentType?.message}>
            <select className="text-input" {...register('employmentType')}>
              {['FULL_TIME', 'PART_TIME', 'CONTRACT', 'INTERN', 'TEMPORARY'].map((type) => <option key={type} value={type}>{enumLabels[type]}</option>)}
            </select>
          </Field>
          <Field label="Employment status" error={errors.employmentStatus?.message}>
            <select className="text-input" {...register('employmentStatus')}>
              {['ACTIVE', 'ON_LEAVE', 'SUSPENDED', 'RESIGNED', 'TERMINATED', 'RETIRED'].map((status) => <option key={status} value={status}>{enumLabels[status] ?? 'Active'}</option>)}
            </select>
          </Field>
          <Field label="Salary amount" error={errors.salary?.message}><input className="text-input" type="number" min="0.01" step="0.01" {...register('salary')} /><small className="field-hint">Enter the amount in your organization’s configured currency.</small></Field>
          <Field label="Department" error={errors.departmentId?.message}>
            <select className="text-input" {...register('departmentId')}>
              <option value="">Choose a department</option>
              {(departmentsQuery.data ?? []).map((department) => <option key={department.id} value={department.id}>{department.name}</option>)}
            </select>
          </Field>
          <Field label="Manager" error={errors.managerId?.message}>
            <select className="text-input" {...register('managerId')}>
              <option value="">No manager assigned</option>
              {(managersQuery.data ?? []).filter((manager) => manager.id !== employeeId).map((manager) => <option key={manager.id} value={manager.id}>{manager.fullName}</option>)}
            </select>
          </Field>
          <Field label="Work shift" error={errors.workShiftId?.message}>
            <select className="text-input" {...register('workShiftId')}>
              <option value="">No shift assigned</option>
              {(shiftsQuery.data ?? []).map((shift) => <option key={shift.id} value={shift.id}>{shift.name} · {shift.startTime.slice(0, 5)}–{shift.endTime.slice(0, 5)}</option>)}
            </select>
          </Field>
        </FormSection>

        {mutation.isError && !Object.keys(getValidationErrors(mutation.error)).length && <div className="form-alert" role="alert">{getApiErrorMessage(mutation.error)}</div>}
        <div className="form-actions">
          <Link className="button button-secondary" to={isEditing ? `/employees/${employeeId}` : '/employees'}>Cancel</Link>
          <button className="button button-primary" type="submit" disabled={isSubmitting || mutation.isPending || departmentsQuery.isLoading || managersQuery.isLoading || shiftsQuery.isLoading}>
            <Check size={17} /> {mutation.isPending ? 'Saving…' : isEditing ? 'Save changes' : 'Create employee'}
          </button>
        </div>
      </form>
    </div>
  )
}

function Field({ label, error, children }: { label: string; error?: string; children: React.ReactNode }) {
  return (
    <label className="form-field">
      <span className="field-label">{label}</span>
      {children}
      {error && <span className="field-error">{error}</span>}
    </label>
  )
}

function FormSection({ title, description, children }: { title: string; description: string; children: React.ReactNode }) {
  return (
    <section className="form-section">
      <div className="form-section-heading"><h2>{title}</h2><p>{description}</p></div>
      <div className="form-grid">{children}</div>
    </section>
  )
}
