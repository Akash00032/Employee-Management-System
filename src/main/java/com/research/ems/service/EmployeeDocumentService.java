package com.research.ems.service;

import com.research.ems.dto.employeedocument.*;
import com.research.ems.entity.enums.DocumentType;

import java.util.List;

public interface EmployeeDocumentService {

    EmployeeDocumentResponseDto uploadDocument(
            EmployeeDocumentRequestDto requestDto
    );

    EmployeeDocumentResponseDto getDocumentById(Long id);

    List<EmployeeDocumentResponseDto> getAllDocuments();

    List<EmployeeDocumentResponseDto> getEmployeeDocuments(
            Long employeeId
    );

    List<EmployeeDocumentResponseDto> getDocumentsByType(
            DocumentType documentType
    );

    List<EmployeeDocumentResponseDto> getVerifiedDocuments(
            Long employeeId
    );

    List<EmployeeDocumentResponseDto> getUnverifiedDocuments(
            Long employeeId
    );

    EmployeeDocumentResponseDto verifyDocument(Long id);

    EmployeeDocumentResponseDto updateDocument(
            Long id,
            EmployeeDocumentRequestDto requestDto
    );

    void deleteDocument(Long id);
}
