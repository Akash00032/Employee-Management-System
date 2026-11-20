package com.research.ems.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "work_shifts",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_shift_name",
                        columnNames = "name"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkShift {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
            nullable = false,
            length = 100
    )
    private String name;

    @Column(
            name = "start_time",
            nullable = false
    )
    private LocalTime startTime;

    @Column(
            name = "end_time",
            nullable = false
    )
    private LocalTime endTime;

    @Column(
            name = "break_start_time"
    )
    private LocalTime breakStartTime;

    @Column(
            name = "break_end_time"
    )
    private LocalTime breakEndTime;

    @Column(
            nullable = false
    )
    @Builder.Default
    private Boolean overnight = false;

    @Column(
            nullable = false
    )
    @Builder.Default
    private Boolean active = true;

    /*
     * Employees assigned to this work shift.
     */
    @OneToMany(
            mappedBy = "workShift"
    )
    @Builder.Default
    private List<Employee> employees = new ArrayList<>();
}