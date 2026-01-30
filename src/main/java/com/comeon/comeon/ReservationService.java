package com.comeon.comeon;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@Service
public class ReservationService {

    private final Map<Long, Reservation> reservationMap = Map.of(
            1L, new Reservation(1L,
            100L,
            40L,
            LocalDate.now(),
            LocalDate.now().plusDays(5),
            ReservationStatus.APPROVED
            ),
            2L, new Reservation(2L,
                    102L,
                    42L,
                    LocalDate.now(),
                    LocalDate.now().plusDays(5),
                    ReservationStatus.APPROVED
            ),
            3L, new Reservation(3L,
                    103L,
                    43L,
                    LocalDate.now(),
                    LocalDate.now().plusDays(5),
                    ReservationStatus.APPROVED
            )
    );

    @GetMapping("/{id}")
    public Reservation getReservationServiceById(Long id) {
        if (!reservationMap.containsKey(id)){
            throw new NoSuchElementException("Reservation with ID" + id + " was not found");
        }
        return reservationMap.get(id);
    }

    @GetMapping()
    public List<Reservation> findAllReservations() {
        return reservationMap.values().stream().toList();
    }
}