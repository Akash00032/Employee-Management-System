package com.research.ems.dto.dashboard;

import com.research.ems.entity.enums.PayrollStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PayrollStatsDto {

    private PayrollStatus status;

    private long count;

    private BigDecimal totalAmount;
}