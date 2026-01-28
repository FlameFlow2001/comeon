package com.comeon.comeon;

import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class ReservationService {

    public Reservation getReservationServiceById(Long id) {
        return new Reservation(id, 100L, 40L, LocalDate.now(), LocalDate.now().plusDays(5), ReservationStatus.APPROVED);
    }
}
