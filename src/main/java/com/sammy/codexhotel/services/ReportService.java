package com.sammy.codexhotel.services;

import com.sammy.codexhotel.data.models.Report;
import com.sammy.codexhotel.data.models.Reservation;
import com.sammy.codexhotel.data.models.ReservationStatus;
import com.sammy.codexhotel.data.models.RoomStatus;
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
    }
}
