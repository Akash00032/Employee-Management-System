package com.research.ems.dto.leavetype;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LeaveTypeResponseDto {

    private Long id;

    private String code;

    private String name;

    private Integer maxDays;

    private Boolean paidLeave;

    private Boolean active;

    private String description;
}