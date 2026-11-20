package com.research.ems.service.impl;

import com.research.ems.entity.Employee;
import com.research.ems.entity.EmployeeDocument;

import com.research.ems.dto.employeedocument.EmployeeDocumentRequestDto;
import com.research.ems.dto.employeedocument.EmployeeDocumentResponseDto;
import com.research.ems.entity.enums.DocumentType;
import com.research.ems.mapper.EmployeeDocumentMapper;
import com.research.ems.repository.EmployeeDocumentRepository;
import com.research.ems.repository.EmployeeRepository;
import com.research.ems.service.EmployeeDocumentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class EmployeeDocumentServiceImpl
        implements EmployeeDocumentService {

    private final EmployeeDocumentRepository repository;
    private final EmployeeRepository employeeRepository;
    private final EmployeeDocumentMapper mapper;

    public EmployeeDocumentServiceImpl(
            EmployeeDocumentRepository repository,
            EmployeeRepository employeeRepository,
            EmployeeDocumentMapper mapper) {

        this.repository = repository;
        this.employeeRepository = employeeRepository;
        this.mapper = mapper;
    }

    @Override
    public EmployeeDocumentResponseDto uploadDocument(
            EmployeeDocumentRequestDto dto) {

        Employee employee = findEmployee(dto.getEmployeeId());

        EmployeeDocument document =
                mapper.toEntity(dto);

        document.setEmployee(employee);

        return mapper.toResponseDto(
                repository.save(document));
    }

    @Override
    @Transactional(readOnly = true)
    public EmployeeDocumentResponseDto getDocumentById(
            Long id) {

        return mapper.toResponseDto(find(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmployeeDocumentResponseDto>
    getAllDocuments() {

        return repository.findAll()
                .stream()
                .map(mapper::toResponseDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmployeeDocumentResponseDto>
    getEmployeeDocuments(Long employeeId) {

        findEmployee(employeeId);

        return repository.findByEmployeeId(employeeId)
                .stream()
                .map(mapper::toResponseDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmployeeDocumentResponseDto>
    getDocumentsByType(DocumentType documentType) {
        return repository.findByDocumentType(documentType)
                .stream()
                .map(mapper::toResponseDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmployeeDocumentResponseDto> getVerifiedDocuments(
            Long employeeId) {
        findEmployee(employeeId);
        return repository.findByEmployeeIdAndVerifiedTrue(employeeId)
                .stream().map(mapper::toResponseDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmployeeDocumentResponseDto> getUnverifiedDocuments(
            Long employeeId) {
        findEmployee(employeeId);
        return repository.findByEmployeeIdAndVerifiedFalse(employeeId)
                .stream().map(mapper::toResponseDto).toList();
    }

    @Override
    public EmployeeDocumentResponseDto verifyDocument(Long id) {
        EmployeeDocument document = find(id);
        document.setVerified(true);
        return mapper.toResponseDto(repository.save(document));
    }

    @Override
    public EmployeeDocumentResponseDto updateDocument(
            Long id,
            EmployeeDocumentRequestDto dto) {

        EmployeeDocument document = find(id);
        Employee employee = findEmployee(dto.getEmployeeId());

        mapper.updateEntity(document, dto);

        document.setEmployee(employee);

        return mapper.toResponseDto(
                repository.save(document));
    }

    @Override
    public void deleteDocument(Long id) {

        repository.delete(find(id));
    }

    private EmployeeDocument find(Long id) {

        return repository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Employee document not found with id: " + id));
    }

    private Employee findEmployee(Long id) {

        return employeeRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Employee not found with id: " + id));
    }
}