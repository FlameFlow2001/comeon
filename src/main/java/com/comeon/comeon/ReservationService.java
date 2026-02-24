package com.comeon.comeon;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class ReservationService {

    private final Map<Long, Reservation> reservationMap;
    private final AtomicLong idCounter;
    public ReservationService(){
        reservationMap = new HashMap<>();
        idCounter = new AtomicLong();
    }
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

    public Reservation createReservation(Reservation reservationToCreate) {
        if (reservationToCreate.id() != null){
            throw new IllegalArgumentException("ID should be empty");
        }
        if (reservationToCreate.status() != null){
            throw new IllegalArgumentException("Status should be empty");
        }
        var newReservation = new Reservation(idCounter.incrementAndGet(),
                reservationToCreate.userId(),
                reservationToCreate.roomId(),
                reservationToCreate.startDate(),
                reservationToCreate.endDate(),
                ReservationStatus.PENDING
        );

        reservationMap.put(newReservation.id(), newReservation);
        return newReservation;

    }

    public Reservation updateReservation(Long id, Reservation reservationToUpdate) {
        if (!reservationMap.containsKey(id))
        {
            throw new NoSuchElementException("Reservation was not found by ID");
        }
        var reservation = reservationMap.get(id);
        if (reservation.status() != ReservationStatus.PENDING){
            throw new IllegalStateException("Cannot modify reservation: status = " + reservation.status());
        }
        var updatedReservation = new Reservation(reservation.id(),
                reservationToUpdate.userId(),
                reservationToUpdate.roomId(),
                reservationToUpdate.startDate(),
                reservationToUpdate.endDate(),
                ReservationStatus.PENDING
        );
        reservationMap.put(updatedReservation.id(), updatedReservation);
        return updatedReservation;
    }

    public void deleteReservation(Long id) {
        if (!reservationMap.containsKey(id))
        {
            throw new NoSuchElementException("Reservation was not found by ID");
        }
        reservationMap.remove(id);
    }

    public Reservation approveReservation(Long id) {
        if (!reservationMap.containsKey(id))
        {
            throw new NoSuchElementException("Reservation was not found by ID");
        }
        var reservation = reservationMap.get(id);
        if (reservation.status() != ReservationStatus.PENDING){
            throw new IllegalStateException("Cannot modify reservation: status = " + reservation.status());
        }
        var isConflicted = isReservationConflicted(reservation);
        if (isConflicted){
            throw new IllegalStateException("Cannot approve reservation because of conflict of dates");
        }
        var approvedReservation = new Reservation(reservation.id(),
                reservation.userId(),
                reservation.roomId(),
                reservation.startDate(),
                reservation.endDate(),
                ReservationStatus.APPROVED
        );
        reservationMap.put(reservation.id(), approvedReservation);
        return approvedReservation;
    }

    private boolean isReservationConflicted(Reservation reservation){
        for (Reservation existingReservation: reservationMap.values()){
            if (reservation.id().equals(existingReservation.id())){
                continue;
            }
            if (!reservation.roomId().equals(existingReservation.roomId())){
                continue;
            }
            if (!existingReservation.status().equals(ReservationStatus.APPROVED)){
                continue;
            }
            if (reservation.startDate().isBefore(existingReservation.endDate())
            && existingReservation.startDate().isBefore(reservation.endDate())){
                return true;
            }
        }
        return false;
    }
}