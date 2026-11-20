package com.research.ems.entity;


import com.research.ems.entity.enums.SalaryComponentType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "salary_components",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_salary_component_code",
                        columnNames = "code"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SalaryComponent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 30)
    private String code;

    @Column(nullable = false, length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SalaryComponentType type;

    @Column(length = 500)
    private String description;

    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;
}
