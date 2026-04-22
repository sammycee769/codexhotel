package com.sammy.codexhotel.data.repositories;

import com.sammy.codexhotel.data.models.Report;
import com.sammy.codexhotel.data.models.ReportType;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ReportRepo extends MongoRepository<Report, String> {
    List<Report> findByReportType(ReportType reportType);
}
