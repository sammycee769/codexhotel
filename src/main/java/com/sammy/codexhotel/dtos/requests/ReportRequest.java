package com.sammy.codexhotel.dtos.requests;

import com.sammy.codexhotel.data.models.ReportType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ReportRequest {

    @NotNull(message = "Report type is required")
    private ReportType reportType;

    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    private LocalDate endDate;
}
