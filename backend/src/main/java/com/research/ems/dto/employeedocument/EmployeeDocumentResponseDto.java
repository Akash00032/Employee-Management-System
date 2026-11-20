package com.research.ems.dto.employeedocument;

import com.research.ems.entity.enums.DocumentType;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeDocumentResponseDto {

    private Long id;

    private Long employeeId;

    private String employeeCode;

    private String employeeName;

    private DocumentType documentType;

    private String fileName;

    private String fileUrl;

    private String documentNumber;

    private LocalDateTime uploadedAt;

    private Boolean verified;
}