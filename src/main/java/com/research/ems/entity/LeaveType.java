package com.research.ems.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "leave_types",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_leave_type_code",
                        columnNames = "code"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeaveType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20)
    private String code;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(name = "max_days", nullable = false)
    private Integer maxDays;

    @Column(name = "paid_leave", nullable = false)
    @Builder.Default
    private Boolean paidLeave = true;

    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;

    @Column(length = 500)
    private String description;
}