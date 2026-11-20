export const ROLES = Object.freeze({
  ADMIN: 'ADMIN',
  HR: 'HR',
  MANAGER: 'MANAGER',
  EMPLOYEE: 'EMPLOYEE',
  USER: 'USER',
} as const)

export type RoleKey = (typeof ROLES)[keyof typeof ROLES]
