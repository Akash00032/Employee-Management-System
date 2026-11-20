package com.research.ems.repository;

import com.research.ems.entity.EmployeeDocument;

import com.research.ems.entity.enums.DocumentType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmployeeDocumentRepository
        extends JpaRepository<EmployeeDocument, Long> {

    List<EmployeeDocument> findByEmployeeId(
            Long employeeId
    );

    List<EmployeeDocument> findByDocumentType(
            DocumentType documentType
    );

    List<EmployeeDocument> findByEmployeeIdAndDocumentType(
            Long employeeId,
            DocumentType documentType
    );

    List<EmployeeDocument> findByEmployeeIdAndVerifiedTrue(
            Long employeeId
    );

    List<EmployeeDocument> findByEmployeeIdAndVerifiedFalse(
            Long employeeId
    );

    boolean existsByEmployeeIdAndDocumentType(
            Long employeeId,
            DocumentType documentType
    );
}
