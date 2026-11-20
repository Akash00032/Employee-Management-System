package com.research.ems.dto.employeedocument;

import com.research.ems.entity.Employee;


import com.research.ems.entity.enums.DocumentType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeDocumentRequestDto {

    @NotNull(message = "Employee ID is required")
    private Long employeeId;

    @NotNull(message = "Document type is required")
    private DocumentType documentType;

    @NotBlank(message = "File name is required")
    @Size(max = 255)
    private String fileName;

    @NotBlank(message = "File URL is required")
    @Size(max = 1000)
    private String fileUrl;

    @Size(max = 100)
    private String documentNumber;

    private Boolean verified;
}