package com.research.ems;

import com.research.ems.entity.Employee;
import com.research.ems.entity.Department;
import com.research.ems.entity.Attendance;
import com.research.ems.entity.LeaveType;
import com.research.ems.entity.LeaveRequest;
import com.research.ems.entity.UserAccount;
import com.research.ems.entity.enums.AttendanceStatus;
import com.research.ems.entity.enums.EmploymentStatus;
import com.research.ems.entity.enums.EmploymentType;
import com.research.ems.entity.enums.Gender;
import com.research.ems.entity.enums.LeaveStatus;
import com.research.ems.entity.enums.Role;
import com.research.ems.repository.AttendanceRepository;
import com.research.ems.repository.EmployeeRepository;
import com.research.ems.repository.DepartmentRepository;
import com.research.ems.repository.RefreshTokenRepository;
import com.research.ems.repository.LeaveTypeRepository;
import com.research.ems.repository.LeaveRequestRepository;
import com.research.ems.repository.UserAccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.hamcrest.Matchers.nullValue;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthenticationIntegrationTest {

    private static final String USERNAME = "admin";
    private static final String PASSWORD = "Admin123";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private UserAccountRepository userAccountRepository;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private LeaveTypeRepository leaveTypeRepository;

    @Autowired
    private LeaveRequestRepository leaveRequestRepository;

    @Autowired
    private AttendanceRepository attendanceRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void createAdminAccount() {
        refreshTokenRepository.deleteAll();
        userAccountRepository.deleteAll();
        leaveRequestRepository.deleteAll();
        leaveTypeRepository.deleteAll();
        attendanceRepository.deleteAll();
        employeeRepository.deleteAll();
        departmentRepository.deleteAll();

        Department department = new Department();
        department.setCode("AUTH");
        department.setName("Authentication Test");
        department.setActive(true);
        department = departmentRepository.save(department);

        Employee employee = new Employee();
        employee.setEmployeeCode("EMS-AUTH-1");
        employee.setFirstName("Audit");
        employee.setLastName("Admin");
        employee.setEmail("audit-admin@example.test");
        employee.setPhone("1000000001");
        employee.setJoiningDate(LocalDate.now());
        employee.setDesignation("Administrator");
        employee.setEmploymentType(EmploymentType.FULL_TIME);
        employee.setEmploymentStatus(EmploymentStatus.ACTIVE);
        employee.setGender(Gender.OTHER);
        employee.setSalary(new BigDecimal("1.00"));
        employee.setDepartment(department);
        employee = employeeRepository.save(employee);

        UserAccount account = new UserAccount();
        account.setUsername(USERNAME);
        account.setPassword(passwordEncoder.encode(PASSWORD));
        account.setEmail("audit-admin@example.test");
        account.setRole(Role.ADMIN);
        account.setEnabled(true);
        account.setEmployee(employee);
        userAccountRepository.save(account);
    }

    @Test
    void loginIssuesAccessTokenAndProtectsDashboard() throws Exception {
        mockMvc.perform(get("/api/v1/dashboard"))
                .andExpect(status().isUnauthorized());

        MvcResult loginResult = login();
        String accessToken = extractToken(
                loginResult.getResponse().getContentAsString(), "accessToken");

        mockMvc.perform(get("/api/v1/dashboard")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk());
    }

    @Test
    void employeeDirectorySearchIsPagedAndSortedOnServer() throws Exception {
        for (int index = 0; index < 3; index++) {
            Employee employee = new Employee();
            employee.setEmployeeCode("EMS-DIR-" + index);
            employee.setFirstName("First" + index);
            employee.setLastName("Searchable");
            employee.setEmail("directory-" + index + "@example.test");
            employee.setPhone("100000001" + index);
            employee.setJoiningDate(LocalDate.now());
            employee.setDesignation("Employee");
            employee.setEmploymentType(EmploymentType.FULL_TIME);
            employee.setEmploymentStatus(EmploymentStatus.ACTIVE);
            employee.setSalary(new BigDecimal("1.00"));
            employeeRepository.save(employee);
        }

        String accessToken = extractToken(
                login().getResponse().getContentAsString(), "accessToken");
        mockMvc.perform(get("/api/v1/employees/directory")
                        .param("keyword", "searchable")
                        .param("page", "1")
                        .param("size", "2")
                        .param("sort", "name")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(3))
                .andExpect(jsonPath("$.totalPages").value(2))
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].email")
                        .value("directory-2@example.test"));
    }

    @Test
    void refreshTokensAreRotatedAndSingleUse() throws Exception {
        MvcResult loginResult = login();
        String initialRefreshToken = extractToken(
                loginResult.getResponse().getContentAsString(), "refreshToken");

        mockMvc.perform(post("/api/v1/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"refreshToken\":\"" + initialRefreshToken + "\"}"))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/v1/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"refreshToken\":\"" + initialRefreshToken + "\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void employeeRoleCannotAccessManagerDashboard() throws Exception {
        UserAccount account = userAccountRepository
                .findByUsernameIgnoreCase(USERNAME)
                .orElseThrow();
        account.setRole(Role.EMPLOYEE);
        userAccountRepository.save(account);

        MvcResult loginResult = login();
        String accessToken = extractToken(
                loginResult.getResponse().getContentAsString(), "accessToken");

        mockMvc.perform(get("/api/v1/dashboard")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void employeeCanOnlyReadOwnAttendanceAndLeaveRoutes() throws Exception {
        UserAccount account = userAccountRepository
                .findByUsernameIgnoreCase(USERNAME)
                .orElseThrow();
        account.setRole(Role.EMPLOYEE);
        userAccountRepository.save(account);

        String accessToken = extractToken(
                login().getResponse().getContentAsString(), "accessToken");

        mockMvc.perform(get("/api/v1/attendance/me")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk());
        mockMvc.perform(get("/api/v1/attendance")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isForbidden());
        mockMvc.perform(get("/api/v1/leave-requests/me")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk());
        mockMvc.perform(get("/api/v1/leave-requests")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isForbidden());
        mockMvc.perform(post("/api/v1/attendance")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isForbidden());
        mockMvc.perform(post("/api/v1/leave-requests")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isForbidden());

        LeaveType leaveType = new LeaveType();
        leaveType.setCode("TEST");
        leaveType.setName("Test leave");
        leaveType.setMaxDays(10);
        leaveType.setPaidLeave(true);
        leaveType.setActive(true);
        leaveType = leaveTypeRepository.save(leaveType);

        mockMvc.perform(post("/api/v1/leave-requests/me")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "leaveTypeId": %d,
                                  "startDate": "%s",
                                  "endDate": "%s",
                                  "reason": "Personal leave"
                                }
                                """.formatted(
                                leaveType.getId(),
                                LocalDate.now().plusDays(1),
                                LocalDate.now().plusDays(1))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.employeeId")
                        .value(account.getEmployee().getId()));
    }

    @Test
    void managerCanOnlyReadDirectReportsAndReceivesNoSalary() throws Exception {
        Employee adminEmployee = employeeRepository
                .findByEmployeeCodeIgnoreCase("EMS-AUTH-1")
                .orElseThrow();
        Employee manager = new Employee();
        manager.setEmployeeCode("EMS-MANAGER-1");
        manager.setFirstName("Team");
        manager.setLastName("Manager");
        manager.setEmail("team-manager@example.test");
        manager.setPhone("1000000004");
        manager.setJoiningDate(LocalDate.now());
        manager.setDesignation("Team Manager");
        manager.setEmploymentType(EmploymentType.FULL_TIME);
        manager.setEmploymentStatus(EmploymentStatus.ACTIVE);
        manager.setSalary(new BigDecimal("75000.00"));
        manager.setDepartment(adminEmployee.getDepartment());
        manager = employeeRepository.save(manager);

        Employee report = new Employee();
        report.setEmployeeCode("EMS-REPORT-1");
        report.setFirstName("Team");
        report.setLastName("Member");
        report.setEmail("team-member@example.test");
        report.setPhone("1000000003");
        report.setJoiningDate(LocalDate.now());
        report.setDesignation("Analyst");
        report.setEmploymentType(EmploymentType.FULL_TIME);
        report.setEmploymentStatus(EmploymentStatus.ACTIVE);
        report.setSalary(new BigDecimal("60000.00"));
        report.setDepartment(manager.getDepartment());
        report.setManager(manager);
        report = employeeRepository.save(report);

        UserAccount managerAccount = new UserAccount();
        managerAccount.setUsername("audit-manager");
        managerAccount.setPassword(passwordEncoder.encode(PASSWORD));
        managerAccount.setEmail("audit-manager@example.test");
        managerAccount.setRole(Role.MANAGER);
        managerAccount.setEnabled(true);
        managerAccount.setEmployee(manager);
        userAccountRepository.save(managerAccount);

        Attendance attendance = new Attendance();
        attendance.setEmployee(report);
        attendance.setAttendanceDate(LocalDate.now());
        attendance.setStatus(AttendanceStatus.PRESENT);
        attendanceRepository.save(attendance);

        LeaveType leaveType = new LeaveType();
        leaveType.setCode("MGR");
        leaveType.setName("Manager test leave");
        leaveType.setMaxDays(10);
        leaveType.setPaidLeave(true);
        leaveType.setActive(true);
        leaveType = leaveTypeRepository.save(leaveType);

        LeaveRequest teamRequest = new LeaveRequest();
        teamRequest.setEmployee(report);
        teamRequest.setLeaveType(leaveType);
        teamRequest.setStartDate(LocalDate.now().plusDays(1));
        teamRequest.setEndDate(LocalDate.now().plusDays(1));
        teamRequest.setTotalDays(1);
        teamRequest.setStatus(LeaveStatus.PENDING);
        teamRequest.setAppliedAt(java.time.LocalDateTime.now());
        teamRequest = leaveRequestRepository.save(teamRequest);

        LeaveRequest otherRequest = new LeaveRequest();
        otherRequest.setEmployee(adminEmployee);
        otherRequest.setLeaveType(leaveType);
        otherRequest.setStartDate(LocalDate.now().plusDays(1));
        otherRequest.setEndDate(LocalDate.now().plusDays(1));
        otherRequest.setTotalDays(1);
        otherRequest.setStatus(LeaveStatus.PENDING);
        otherRequest.setAppliedAt(java.time.LocalDateTime.now());
        otherRequest = leaveRequestRepository.save(otherRequest);

        String accessToken = extractToken(
                login("audit-manager").getResponse().getContentAsString(),
                "accessToken");

        mockMvc.perform(get("/api/v1/employees/team")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].salary").value(nullValue()));
        mockMvc.perform(get("/api/v1/attendance/team")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].employeeId").value(report.getId()));
        mockMvc.perform(get("/api/v1/leave-requests/team")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                        .patch("/api/v1/leave-requests/team/"
                                + teamRequest.getId() + "/approve")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.approvedById").value(manager.getId()));
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                        .patch("/api/v1/leave-requests/team/"
                                + otherRequest.getId() + "/approve")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isForbidden());
        mockMvc.perform(get("/api/v1/employees/" + report.getId())
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isForbidden());
        mockMvc.perform(get("/api/v1/dashboard")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalPayroll").value(nullValue()));
    }

    @Test
    void healthOpenApiAndEmployeeCrudAreAvailable() throws Exception {
        mockMvc.perform(get("/actuator/health"))
                .andExpect(status().isOk());
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk());

        String accessToken = extractToken(
                login().getResponse().getContentAsString(), "accessToken");
        long departmentId = departmentRepository.findByCodeIgnoreCase("AUTH")
                .orElseThrow()
                .getId();

        mockMvc.perform(post("/api/v1/employees")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "employeeCode": "EMS-CRUD-1",
                                  "firstName": "Test",
                                  "lastName": "Employee",
                                  "email": "test-employee@example.test",
                                  "phone": "1000000002",
                                  "joiningDate": "2026-01-01",
                                  "designation": "Analyst",
                                  "employmentType": "FULL_TIME",
                                  "employmentStatus": "ACTIVE",
                                  "salary": 50000.00,
                                  "departmentId": %d
                                }
                                """.formatted(departmentId)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/v1/employees")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk());
    }

    private MvcResult login() throws Exception {
        return login(USERNAME);
    }

    private MvcResult login(String username) throws Exception {
        return mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"" + username +
                                "\",\"password\":\"" + PASSWORD + "\"}"))
                .andExpect(status().isOk())
                .andReturn();
    }

    private String extractToken(String json, String field) {
        Matcher matcher = Pattern.compile(
                        "\"" + Pattern.quote(field) + "\"\\s*:\\s*\"([^\"]+)\"")
                .matcher(json);
        if (!matcher.find()) {
            throw new AssertionError("Response did not contain " + field);
        }
        return matcher.group(1);
    }
}
