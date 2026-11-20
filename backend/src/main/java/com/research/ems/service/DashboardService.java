package com.research.ems.service;

import com.research.ems.dto.dashboard.DashboardSummaryDto;

public interface DashboardService {

    DashboardSummaryDto getDashboardSummary(boolean includePayroll);
}
