package com.research.ems.controller;

import com.research.ems.dto.employeedocument.EmployeeDocumentRequestDto;
import com.research.ems.dto.employeedocument.EmployeeDocumentResponseDto;
import com.research.ems.entity.enums.DocumentType;
import com.research.ems.service.EmployeeDocumentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/employee-documents")
@RequiredArgsConstructor
public class EmployeeDocumentController {

    private final EmployeeDocumentService employeeDocumentService;

    @PostMapping
    public ResponseEntity<EmployeeDocumentResponseDto> uploadDocument(
            @Valid @RequestBody EmployeeDocumentRequestDto requestDto) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(employeeDocumentService.uploadDocument(requestDto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmployeeDocumentResponseDto> getDocumentById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                employeeDocumentService.getDocumentById(id)
        );
    }

    @GetMapping
    public ResponseEntity<List<EmployeeDocumentResponseDto>> getAllDocuments() {

        return ResponseEntity.ok(
                employeeDocumentService.getAllDocuments()
        );
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<EmployeeDocumentResponseDto>> getEmployeeDocuments(
            @PathVariable Long employeeId) {

        return ResponseEntity.ok(
                employeeDocumentService.getEmployeeDocuments(employeeId)
        );
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<List<EmployeeDocumentResponseDto>> getDocumentsByType(
            @PathVariable DocumentType type) {

        return ResponseEntity.ok(
                employeeDocumentService.getDocumentsByType(type)
        );
    }

    @PatchMapping("/{id}/verify")
    public ResponseEntity<EmployeeDocumentResponseDto> verifyDocument(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                employeeDocumentService.verifyDocument(id)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<EmployeeDocumentResponseDto> updateDocument(
            @PathVariable Long id,
            @Valid @RequestBody EmployeeDocumentRequestDto requestDto) {

        return ResponseEntity.ok(
                employeeDocumentService.updateDocument(id, requestDto)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDocument(
            @PathVariable Long id) {

        employeeDocumentService.deleteDocument(id);
        return ResponseEntity.noContent().build();
    }
}