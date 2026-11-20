package com.research.ems.mapper;

import com.research.ems.entity.EmployeeDocument;

import com.research.ems.dto.employeedocument.EmployeeDocumentRequestDto;
import com.research.ems.dto.employeedocument.EmployeeDocumentResponseDto;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class EmployeeDocumentMapper {

    public EmployeeDocument toEntity(
            EmployeeDocumentRequestDto dto) {

        if (dto == null) {
            return null;
        }

        EmployeeDocument document =
                new EmployeeDocument();

        document.setDocumentType(dto.getDocumentType());
        document.setFileName(dto.getFileName());
        document.setFileUrl(dto.getFileUrl());
        document.setDocumentNumber(dto.getDocumentNumber());
        document.setVerified(
                dto.getVerified() != null
                        && dto.getVerified()
        );

        document.setUploadedAt(LocalDateTime.now());

        return document;
    }

    public void updateEntity(
            EmployeeDocument document,
            EmployeeDocumentRequestDto dto) {

        if (document == null || dto == null) {
            return;
        }

        document.setDocumentType(dto.getDocumentType());
        document.setFileName(dto.getFileName());
        document.setFileUrl(dto.getFileUrl());
        document.setDocumentNumber(dto.getDocumentNumber());

        if (dto.getVerified() != null) {
            document.setVerified(dto.getVerified());
        }
    }

    public EmployeeDocumentResponseDto toResponseDto(
            EmployeeDocument document) {

        if (document == null) {
            return null;
        }

        Long employeeId = null;
        String employeeCode = null;
        String employeeName = null;

        if (document.getEmployee() != null) {
            employeeId = document.getEmployee().getId();
            employeeCode = document.getEmployee().getEmployeeCode();

            employeeName = buildFullName(
                    document.getEmployee().getFirstName(),
                    document.getEmployee().getLastName()
            );
        }

        return new EmployeeDocumentResponseDto(
                document.getId(),
                employeeId,
                employeeCode,
                employeeName,
                document.getDocumentType(),
                document.getFileName(),
                document.getFileUrl(),
                document.getDocumentNumber(),
                document.getUploadedAt(),
                document.getVerified()
        );
    }

    private String buildFullName(
            String firstName,
            String lastName) {

        if (firstName == null) {
            return lastName;
        }

        if (lastName == null) {
            return firstName;
        }

        return firstName + " " + lastName;
    }
}