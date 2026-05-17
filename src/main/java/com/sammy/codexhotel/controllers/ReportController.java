package com.sammy.codexhotel.controllers;

import com.sammy.codexhotel.data.models.Report;
import com.sammy.codexhotel.data.models.ReportType;
import com.sammy.codexhotel.dtos.requests.ReportRequest;
import com.sammy.codexhotel.dtos.responses.ApiResponse;
import com.sammy.codexhotel.services.ReportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reports")
public class ReportController {
    private final ReportService reportService;

    @PostMapping("/generate")
    public ResponseEntity<ApiResponse> generateReport(@Valid @RequestBody ReportRequest reportRequest){
        Report report = reportService.generateReport(reportRequest);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ApiResponse(true, "Report Generated Successfully", report));
    }

    @GetMapping("/{type}")
    public ResponseEntity<ApiResponse> getReportByType(@PathVariable ReportType type){
        List<Report> reportByType = reportService.getReportByType(type);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ApiResponse(true, "Report Found", reportByType));
    }

    @GetMapping("/all")
    public  ResponseEntity<ApiResponse> getAllReports(){
        List<Report> reports = reportService.getAllReports();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ApiResponse(true, "Report Found", reports));
    }
}
