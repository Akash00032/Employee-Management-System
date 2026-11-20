EMS Sample Data Seed Guide
========================================

This project uses the real JPA entity schema, so the seed script follows actual table and column names from the Java entities.

Important schema notes
----------------------
- The table names are based on the actual JPA @Table annotations.
- `employee_addresses` is the actual address table, not `addresses`.
- `work_shifts` is the actual table name, not `workshift`.
- `employee_documents` is the actual document table.
- `employee_assets` is the actual asset table.
- `user_accounts` and `refresh_tokens` are intentionally not seeded directly in SQL because of the BCrypt / JWT security model.
- `EMP001` is reserved for the application administrator and is intentionally not inserted by this seed script.
- The script is designed for MySQL 8.x and uses standard MySQL syntax only.

Prerequisites
-------------
1. A MySQL 8.x database named `employee_management_system` must exist.
2. The MySQL client must be installed and available in PATH.
3. The EMS application schema should be created before running the seed.
4. The application must have a working `PasswordEncoder` and JWT setup for user-account seeding.

Backup recommendation
---------------------
Before loading sample data into an existing database:

1. Take a backup of the current database.
2. Export the schema and data for safety.
3. Validate that the app is not running against that database during the import.

Example backup:

mysqldump -u root -p --databases employee_management_system > backup_employee_management_system.sql

How to execute from Windows PowerShell
-------------------------------------
Use the MySQL client in a pipeline, since PowerShell does not support direct shell redirection in the same way as bash.

Example:

Get-Content "C:\path\ems_sample_data.sql" | mysql -u root -p employee_management_system

Or:

cmd /c 'mysql -u root -p employee_management_system < "C:\path\ems_sample_data.sql"'

How to execute from CMD
-----------------------
cmd /c "mysql -u root -p employee_management_system < "C:\path\ems_sample_data.sql""

How to execute using MySQL SOURCE
---------------------------------
Start the MySQL client:

mysql -u root -p employee_management_system

Then run:

USE employee_management_system;
SOURCE C:/path/ems_sample_data.sql;

How to verify data
------------------
After import, run the following checks:

SELECT COUNT(*) AS total_employees FROM employees;
SELECT COUNT(*) AS sample_employee_count
FROM employees
WHERE CAST(SUBSTRING(employee_code, 4) AS UNSIGNED) BETWEEN 2 AND 1001;
SELECT COUNT(*) AS total_departments FROM departments;
SELECT COUNT(*) AS total_work_shifts FROM work_shifts;
SELECT COUNT(*) AS total_attendance FROM attendance;
SELECT COUNT(*) AS total_leave_requests FROM leave_requests;
SELECT COUNT(*) AS total_payroll FROM payrolls;
SELECT COUNT(*) AS total_projects FROM projects;
SELECT COUNT(*) AS total_employee_projects FROM employee_projects;
SELECT COUNT(*) AS total_reviews FROM performance_reviews;
SELECT COUNT(*) AS total_documents FROM employee_documents;
SELECT COUNT(*) AS total_assets FROM employee_assets;

The seed script also includes validation queries at the end for relationship checks.

Rollback if required
--------------------
If you need to undo the import in a development database, use a rollback from the MySQL console:

START TRANSACTION;
-- review your data first
ROLLBACK;

If you are working in a non-production environment and want to remove seed data completely:

DELETE FROM employee_assets;
DELETE FROM employee_documents;
DELETE FROM performance_reviews;
DELETE FROM payrolls;
DELETE FROM leave_requests;
DELETE FROM attendance;
DELETE FROM employee_projects;
DELETE FROM projects;
DELETE FROM employee_salaries;
DELETE FROM employee_addresses;
DELETE FROM employees
WHERE CAST(SUBSTRING(employee_code, 4) AS UNSIGNED) BETWEEN 2 AND 1001;
DELETE FROM departments WHERE code IN ('IT','HR','FIN','MKT','SAL','OPS','QA','SEC','SUP','PMO');
DELETE FROM work_shifts WHERE name IN ('General Shift','Morning Shift','Evening Shift','Night Shift','Flexible Shift');
DELETE FROM leave_types WHERE code IN ('CL','SL','PL','EL','LWP','ML','PTL','WFH');
DELETE FROM salary_components WHERE code IN ('BASIC','HRA','CONVEYANCE','MEDICAL','SPECIAL','BONUS','PF','TDS','PROF_TAX','ESI','LOAN','OTHER_DED');

Expected row counts
-------------------
- Departments: 10
- Work shifts: 5
- Employees: 1,000 (EMP002 - EMP1001; EMP001 remains reserved for the admin)
- Employment-status samples: 5 each — EMP023-EMP027 (ON_LEAVE), EMP028-EMP032 (SUSPENDED), EMP033-EMP037 (RESIGNED), EMP038-EMP042 (TERMINATED), and EMP043-EMP047 (RETIRED)
- Employee addresses: 1,000
- Projects: 10
- Employee-project assignments: ~2,500
- Salary component records: multiple per employee
- Attendance rows: ~22,000 (September 2026 weekdays)
- Leave requests: ~1,080
- Payroll rows: 3,000 (3 months x 1,000 employees)
- Performance reviews: ~1,020
- Employee documents: ~2,000
- Employee assets: ~1,020

Tables intentionally not seeded in SQL
--------------------------------------
- `user_accounts` (BCrypt password hashes are generated by the application)
- `refresh_tokens` (generated during authentication only)

How UserAccount passwords are created
------------------------------------
The project uses BCrypt via Spring Security `PasswordEncoder`.
The SQL seed does not create user accounts or passwords. The application no longer
creates sample employee accounts with built-in passwords. Existing accounts,
including the EMP001 administrator, are left unchanged. In `dev`, the initializer
creates only the admin account when one does not already exist.

The `dev` profile bootstraps an administrator when one does not already exist.
By default, it uses the local development password `Admin123`; override it with:

- `EMS_ADMIN_BOOTSTRAP_ENABLED=false` to disable the bootstrap
- `EMS_ADMIN_PASSWORD` to set a different password

Do not use the default development password outside an isolated local environment.
If `EMP001` is missing, the dev bootstrap creates a minimal system-administrator
employee record and links the new account to it. Existing admin accounts are not
modified. Bootstrap is not available outside the `dev` profile. Configure
`JWT_SECRET`, `DB_USERNAME`, and `DB_PASSWORD` through the environment as well.
`JWT_SECRET` must be Base64-encoded key material of at least 32 bytes.

Process / run order
-------------------
1. Ensure the database exists and the app schema is applied.
2. Run the SQL file to load the static master and transactional data.
3. For local development only, configure the bootstrap environment variables
   above before starting the application.
4. Verify login, refresh, and role-based access with the administrator account.

Important constraints respected in the seed
-----------------------------------------
- Unique employee codes are maintained (EMP002 - EMP1001).
- Unique emails and phone numbers are maintained.
- No duplicate attendance rows for employee + date.
- No duplicate employee-project rows for employee + project.
- No duplicate department or shift names.
- Existing `EMP001` administrator is not modified.
- Foreign keys are respected by insert order.
- Data is intentionally MySQL 8 compatible.

Recommended verification query block
-----------------------------------
SELECT COUNT(*) AS total_employees FROM employees;
SELECT COUNT(*) AS total_attendance FROM attendance;
SELECT COUNT(*) AS total_leave_requests FROM leave_requests;
SELECT COUNT(*) AS total_payroll FROM payrolls;
SELECT COUNT(*) AS total_projects FROM projects;
SELECT COUNT(*) AS total_employee_projects FROM employee_projects;
SELECT COUNT(*) AS total_reviews FROM performance_reviews;
SELECT COUNT(*) AS total_documents FROM employee_documents;
SELECT COUNT(*) AS total_assets FROM employee_assets;

Verify sample employment statuses:

SELECT employee_code, employment_status
FROM employees
WHERE employee_code BETWEEN 'EMP023' AND 'EMP047'
ORDER BY employee_code;

SELECT employment_status, COUNT(*) AS employee_count
FROM employees
WHERE employment_status IN ('ON_LEAVE', 'SUSPENDED', 'RESIGNED', 'TERMINATED', 'RETIRED')
GROUP BY employment_status
ORDER BY employment_status;

SELECT
  e.employee_code,
  CONCAT(e.first_name, ' ', e.last_name) AS employee_name,
  d.name AS department,
  e.designation
FROM employees e
LEFT JOIN departments d ON d.id = e.department_id
ORDER BY e.id;

If you need assistance with a database refresh or import validation, use the project schema and this file together.
