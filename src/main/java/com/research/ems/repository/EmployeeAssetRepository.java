package com.research.ems.repository;

import com.research.ems.entity.EmployeeAsset;

import com.research.ems.entity.enums.AssetStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;

public interface EmployeeAssetRepository
        extends JpaRepository<EmployeeAsset, Long> {

    Optional<EmployeeAsset> findBySerialNumberIgnoreCase(
            String serialNumber
    );

    boolean existsBySerialNumberIgnoreCase(
            String serialNumber
    );

    boolean existsBySerialNumberIgnoreCaseAndIdNot(
            String serialNumber,
            Long id
    );

    List<EmployeeAsset> findByEmployeeId(
            Long employeeId
    );

    Page<EmployeeAsset> findByEmployeeId(
            Long employeeId,
            Pageable pageable
    );

    List<EmployeeAsset> findByEmployeeIdAndStatus(
            Long employeeId,
            AssetStatus status
    );

    List<EmployeeAsset> findByStatus(
            AssetStatus status
    );

    long countByEmployeeIdAndStatus(
            Long employeeId,
            AssetStatus status
    );
}
