package com.research.ems.entity;



import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(
        name = "employee_projects",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_employee_project",
                        columnNames = {"employee_id", "project_id"}
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeProject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @Column(length = 100)
    private String projectRole;

    @Column(name = "allocation_percentage")
    private Integer allocationPercentage;

    @Column(name = "assigned_date")
    private LocalDate assignedDate;

    @Column(name = "released_date")
    private LocalDate releasedDate;

    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;
}
