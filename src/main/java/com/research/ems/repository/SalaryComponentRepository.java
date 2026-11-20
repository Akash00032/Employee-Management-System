package com.research.ems.repository;

import com.research.ems.entity.SalaryComponent;

import com.research.ems.entity.enums.SalaryComponentType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SalaryComponentRepository
        extends JpaRepository<SalaryComponent, Long> {

    Optional<SalaryComponent> findByCodeIgnoreCase(
            String code
    );

    Optional<SalaryComponent> findByNameIgnoreCase(
            String name
    );

    boolean existsByCodeIgnoreCase(
            String code
    );

    boolean existsByCodeIgnoreCaseAndIdNot(
            String code,
            Long id
    );

    boolean existsByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);

    List<SalaryComponent> findByType(
            SalaryComponentType type
    );

    List<SalaryComponent> findByActiveTrue();

    List<SalaryComponent> findByTypeAndActiveTrue(
            SalaryComponentType type
    );
}
