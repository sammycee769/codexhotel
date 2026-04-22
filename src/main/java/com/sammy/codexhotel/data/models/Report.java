package com.sammy.codexhotel.data.models;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Document(collection = "reports")
public class Report {
    @Id
    private String reportId;
    private ReportType reportType;
    @NotNull(message = "Start date is required")
    private LocalDate startDate;
    @NotNull(message = "End date is required")
    private LocalDate endDate;
    @Positive(message = "Total Rooms Occupied must be greater than zero")
    private int totalRoomsOccupied;
    @Positive(message = "Total Rooms Available must be greater than zero")
    private int totalRoomsAvailable;
    @Positive(message = "Total Revenue must be greater than zero")
    private double totalRevenue;
    private LocalDateTime generatedAt = LocalDateTime.now();
    private double occupancyRate = (double) totalRoomsOccupied / totalRoomsAvailable;

}
