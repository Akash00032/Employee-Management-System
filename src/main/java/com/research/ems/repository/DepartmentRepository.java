package com.research.ems.repository;

import com.research.ems.entity.Department;

import com.research.ems.dto.dashboard.DepartmentStatsDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;


public interface DepartmentRepository
        extends JpaRepository<Department, Long> {

    Optional<Department> findByCodeIgnoreCase(String code);

    Optional<Department> findByNameIgnoreCase(String name);

    boolean existsByCodeIgnoreCase(String code);

    boolean existsByNameIgnoreCase(String name);

    boolean existsByCodeIgnoreCaseAndIdNot(
            String code,
            Long id
    );

    boolean existsByNameIgnoreCaseAndIdNot(
            String name,
            Long id
    );

    @Query("""
            select new com.research.ems.dto.dashboard.DepartmentStatsDto(
                d.id, d.code, d.name, count(e.id))
            from Department d left join d.employees e
            group by d.id, d.code, d.name
            order by d.name
            """)
    List<DepartmentStatsDto> getDepartmentStats();
}
