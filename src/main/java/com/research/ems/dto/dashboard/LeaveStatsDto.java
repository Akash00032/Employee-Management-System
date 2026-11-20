package com.research.ems.dto.dashboard;

import com.research.ems.entity.enums.LeaveStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LeaveStatsDto {

    private LeaveStatus status;

    private long count;
}