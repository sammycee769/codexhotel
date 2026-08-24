package com.sammy.codexhotel.services;

import com.sammy.codexhotel.data.models.*;
import com.sammy.codexhotel.data.repositories.ReportRepo;
import com.sammy.codexhotel.data.repositories.ReservationRepo;
import com.sammy.codexhotel.data.repositories.RoomRepo;
import com.sammy.codexhotel.dtos.requests.ReportRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportService {
    private final ReportRepo reportRepository;
    private final ReservationRepo reservationRepository;
    private final RoomRepo roomRepository;

    public Report generateReport(ReportRequest request){
        List<Reservation> completedInPeriod = new ArrayList<>();

        List<Reservation> reservations =
                reservationRepository.findByReservationStatus(ReservationStatus.COMPLETED);

        for (Reservation reservation : reservations) {
            if (!reservation.getCheckInDate().isBefore(request.getStartDate()) &&
                    !reservation.getCheckInDate().isAfter(request.getEndDate())) {

                completedInPeriod.add(reservation);
            }
        }
        double totalRevenue =0;
        for (Reservation reservation : completedInPeriod) {
            totalRevenue += reservation.getTotalPayment();
        }
        int occupied = roomRepository.findByRoomStatus(RoomStatus.OCCUPIED).size();
        int available = roomRepository.findByRoomStatus(RoomStatus.AVAILABLE).size();
        int maintenance = roomRepository.findByRoomStatus(RoomStatus.MAINTENANCE).size();

        Report report = new Report();
        report.setReportType(request.getReportType());
        report.setStartDate(request.getStartDate());
        report.setEndDate(request.getEndDate());
        report.setTotalRevenue(totalRevenue);
        report.setTotalRoomsOccupied(occupied);
        report.setTotalRoomsAvailable(available);
        report.setRoomsUnderMaintainance(maintenance);
        report.setOccupancyRate(occupancyRate(occupied, available, maintenance));
        return reportRepository.save(report);
    }
    /**
     * Occupied rooms over the whole inventory, so the value is always between 0 and 1.
     * Guards the empty-hotel case, which would otherwise divide by zero and yield NaN —
     * unserialisable as JSON.
     */
    private double occupancyRate(int occupied, int available, int maintenance) {
        int totalRooms = occupied + available + maintenance;
        if (totalRooms == 0) {
            return 0.0;
        }
        return (double) occupied / totalRooms;
    }

    public List<Report> getReportByType(ReportType reportType){
        return reportRepository.findByReportType(reportType);
    }
    public List<Report> getAllReports(){
        return reportRepository.findAll();
    }
}
