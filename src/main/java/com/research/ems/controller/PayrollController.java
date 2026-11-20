package com.research.ems.controller;

import com.research.ems.dto.payroll.PayrollRequestDto;
import com.research.ems.dto.payroll.PayrollResponseDto;
import com.research.ems.entity.enums.PayrollStatus;
import com.research.ems.service.PayrollService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/payrolls")
@RequiredArgsConstructor
public class PayrollController {

    private final PayrollService payrollService;

    @PostMapping
    public ResponseEntity<PayrollResponseDto> createPayroll(
            @Valid @RequestBody PayrollRequestDto requestDto) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(payrollService.createPayroll(requestDto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PayrollResponseDto> getPayrollById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                payrollService.getPayrollById(id)
        );
    }

    @GetMapping
    public ResponseEntity<List<PayrollResponseDto>> getAllPayrolls() {

        return ResponseEntity.ok(
                payrollService.getAllPayrolls()
        );
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<PayrollResponseDto>> getEmployeePayrolls(
            @PathVariable Long employeeId) {

        return ResponseEntity.ok(
                payrollService.getEmployeePayrolls(employeeId)
        );
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<PayrollResponseDto>> getPayrollsByStatus(
            @PathVariable PayrollStatus status) {

        return ResponseEntity.ok(
                payrollService.getPayrollsByStatus(status)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<PayrollResponseDto> updatePayroll(
            @PathVariable Long id,
            @Valid @RequestBody PayrollRequestDto requestDto) {

        return ResponseEntity.ok(
                payrollService.updatePayroll(id, requestDto)
        );
    }

    @PatchMapping("/{id}/process")
    public ResponseEntity<PayrollResponseDto> processPayroll(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                payrollService.processPayroll(id)
        );
    }

    @PatchMapping("/{id}/pay")
    public ResponseEntity<PayrollResponseDto> markPayrollAsPaid(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                payrollService.markPayrollAsPaid(id)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePayroll(
            @PathVariable Long id) {

        payrollService.deletePayroll(id);
        return ResponseEntity.noContent().build();
    }
}