# Employee Management System — Interview Guide

Use this guide to explain the project clearly and accurately in an interview.
Adapt the short introduction to the role and keep the details grounded in what
is implemented today.

## 60-second introduction

> I built an Employee Management System with a React and TypeScript frontend
> and a Java Spring Boot REST API. It supports employee and department
> management, attendance, leave requests, payroll, projects, work shifts, and
> dashboards. The backend uses Spring Data JPA with MySQL, and Spring Security
> with JWT access and refresh tokens for authentication and role-based access.
> The frontend uses a shared Axios API client, protected routes, and
> server-backed search and pagination. Docker Compose brings up the database,
> API, and Nginx-served frontend together.

## What the application does

- Provides role-aware dashboards and employee directory views.
- Supports employee, department, attendance, leave, and work-shift workflows.
- Includes backend domains for payroll, projects, salary components, employee
  assets and documents, performance reviews, and user accounts.
- Allows employees to use self-service attendance and leave routes, while
  managers have team-scoped views and HR/admin users manage organization data.
- Exposes REST endpoints under `/api/v1` and OpenAPI documentation.

## Technology stack

| Area | Technologies |
|---|---|
| Backend | Java 17, Spring Boot 4, Spring MVC, Spring Data JPA, Hibernate |
| Security | Spring Security, JWT access/refresh tokens, BCrypt password encoding |
| Database | MySQL 8 for runtime; H2 for integration tests |
| Frontend | React 19, TypeScript, Vite, React Router |
| Frontend data/forms | Axios, TanStack Query, React Hook Form, Zod |
| UI | Tailwind CSS 4, Lucide icons, Recharts |
| Delivery | Docker, Docker Compose, Nginx |

## Architecture

### Backend

The backend is a layered Spring application. Source code is in
`backend/src/main/java/com/research/ems/`; Maven configuration and the wrapper
remain at the repository root.

- `controller/` maps REST requests to application services.
- `service/` contains service contracts and `service/impl/` contains their
  implementations and business workflows.
- `repository/` defines Spring Data persistence operations.
- `entity/` contains JPA models and enums for domain concepts and relationships.
- `dto/` contains API request and response models, keeping persistence models
  separate from most REST payloads.
- `mapper/` maps entities to and from DTOs.
- `security/` contains the JWT filter, token provider, authorization helpers,
  and Spring Security configuration.
- `config/` contains application configuration; `exception/` centralizes
  domain errors and REST exception handling.

### Frontend

The React application is in `frontend/src/`. It currently uses a pragmatic
layer-and-page organization rather than fully feature-based modules.

- `pages/` holds dashboard, employee, attendance, department, leave, work-shift,
  and login screens.
- `components/common/` and `components/layout/` hold reusable UI and layout.
- `context/` owns authentication and theme state; `routes/` protects routes.
- `services/` contains the shared Axios client and API-specific functions.
- `types/` defines shared API and domain types.
- `App.tsx` defines routes and lazy-loads application pages.

### Request flow

1. A page calls a function in a frontend service module.
2. The shared Axios client sends the request to `/api/v1` and attaches the
   access token when present.
3. Spring Security authenticates the JWT and checks endpoint and role rules.
4. A controller delegates to a service, which uses repositories to read or
   update JPA entities.
5. The backend returns DTOs; TanStack Query manages frontend server state.

The Vite development server proxies API calls to the backend. In Docker,
Nginx proxies `/api/` to the backend service, so the browser uses the same
origin for UI and API requests.

## Data and security

- MySQL stores the employee and organization data. Relationships are modeled
  with JPA entities.
- Development and Docker profiles currently let Hibernate update the schema;
  production validates the schema. There are no versioned database migrations
  yet.
- Authentication uses short-lived access tokens and refresh tokens. Refresh
  tokens are rotated by the API.
- Passwords are encoded with BCrypt rather than stored as plain text.
- Authorization is enforced on the API, not only in the frontend route guards.
- Admin bootstrap is opt-in and requires an explicit initial password. Use a
  unique value only for initial setup and disable/unset it afterward.
- Configure a strong, independently generated `JWT_SECRET` through the
  deployment environment. Never rely on a repository default for production.

## Strengths and trade-offs

### Strengths

- Responsibilities are separated across controllers, services, repositories,
  DTOs, and frontend API services.
- Request/response DTOs reduce direct exposure of persistence entities.
- Role checks and self/team-scoped API routes support different employee,
  manager, HR, and administrator workflows.
- The frontend centralizes API configuration and token refresh handling.
- The application has integration tests for login, refresh-token rotation,
  role restrictions, employee directory paging, and CRUD behavior.
- Docker Compose provides a repeatable local environment for the database,
  backend, and frontend.

### Trade-offs and limitations

- The backend is organized mainly by technical layer rather than by business
  module. This is straightforward at the current scale, but domain changes can
  span many packages.
- The frontend separates pages and services by concern, but is not yet fully
  feature-based.
- Hibernate schema updates are convenient for development, but production
  schema evolution should move to reviewed, versioned migrations.
- Access and refresh tokens are stored in browser local storage. This is
  convenient for a bearer-token SPA, but JavaScript-accessible tokens increase
  the impact of an XSS issue; an HttpOnly-cookie design is an alternative with
  different CSRF and deployment considerations.
- Automated backend integration coverage exists, but frontend test and lint
  scripts are not currently defined in the package scripts.
- Production startup requires careful environment configuration, particularly
  for the JWT signing key, database credentials, allowed CORS origins, and
  schema provisioning.

## Challenges worth discussing

Choose examples you can explain in detail:

- **Authorization beyond the UI:** protected routes improve navigation, but
  backend rules are essential. Explain how manager-specific routes are
  constrained to direct reports and how sensitive compensation data is
  omitted from manager responses.
- **Refresh-token rotation:** explain how the client handles concurrent 401
  responses with a shared refresh request and retries the failed request after
  successful rotation.
- **Search and pagination:** explain how the employee directory sends search,
  page, and sorting parameters to the API instead of loading the entire
  directory into the browser.
- **Frontend/API integration:** explain how one Axios client centralizes the
  base URL, bearer token, refresh behavior, and error messages, while Vite and
  Nginx provide development and container proxies.
- **Schema compatibility:** explain the difference between development schema
  updates and production schema validation, and why migrations are a planned
  improvement.

## Future implementation roadmap

These are planned improvements, not completed features. Prioritize deployment
correctness and security before expanding the architecture:

1. **Make deployment configuration reliable.** Read the database URL from
   `DB_URL` so Docker Compose connects to its `db` service instead of a fixed
   localhost address. Validate required runtime settings at startup and keep
   local, Docker, and production profiles consistent.
2. **Remove and rotate the exposed JWT fallback.** Require a strong
   deployment-provided signing key, remove usable key material from
   `.env.example`, and rotate any key that has been used outside local tests.
3. **Update vulnerable frontend dependencies.** Upgrade the affected
   transitive and direct packages, preserve the lockfile, and rerun the
   production dependency audit and frontend build.
4. **Adopt versioned database migrations.** Add reviewed migrations for the
   existing MySQL schema, verify them against a copy of representative data,
   then use schema validation in deployed environments instead of automatic
   Hibernate updates.
5. **Strengthen release verification.** Build Docker images in CI, run backend
   integration tests and frontend type-check/build, add frontend tests and
   linting, and verify health checks and API connectivity in a deployment-like
   environment.
6. **Refactor structure incrementally.** Group backend code by business
   domain and frontend pages/services by feature only when regression tests
   protect the current API contracts and workflows.

## Running and testing

Run backend tests from the repository root:

```powershell
.\mvnw.cmd --batch-mode --no-transfer-progress test
```

Run the frontend production type-check/build:

```powershell
Set-Location frontend
npm ci
npm run build
```

Run the full local stack with Docker Compose after configuring `.env` from
`.env.example`:

```powershell
docker compose up --build
```

The backend test suite was last run successfully with **10 tests and no
failures or errors**. Frontend build status should be verified before an
interview or release rather than assumed.

## Common interview questions

**Why use DTOs instead of returning JPA entities?**  
DTOs keep the HTTP contract separate from the persistence model, allow
purpose-specific response shapes, and reduce accidental exposure of internal
fields or relationships.

**Why keep authorization in the backend if the frontend has protected routes?**  
Frontend checks are for user experience and can be bypassed. The API must
enforce authentication, roles, and data scope for every protected operation.

**How would you make this more production-ready?**  
I would first fix deployment configuration so the container uses the Compose
database URL, require a rotated deployment-only JWT key, and update the
vulnerable frontend dependencies. Next I would add and test database
migrations, then add CI coverage for image builds, integration tests, frontend
checks, health checks, and operational procedures such as backups.

**What would you improve architecturally next?**  
Gradually group backend controllers, DTOs, services, and repositories by
business domain, and move frontend pages and API services into feature folders.
Do this incrementally with API-contract and regression tests to preserve
existing behavior.

## Be precise in the interview

- Distinguish implemented functionality from planned improvements.
- Do not claim that every listed domain has a polished frontend workflow unless
  you have verified it in the current UI.
- Describe Hibernate schema updates as the current setup, not as database
  migrations.
- Do not claim a frontend build, Docker image build, or production deployment
  passed unless you have run it.
