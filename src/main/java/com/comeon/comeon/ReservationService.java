package com.comeon.comeon;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.*;

@Service
public class ReservationService {
    private static final Logger log = LoggerFactory.getLogger(ReservationService.class);

    private final ReservationRepository repository;
    public ReservationService(ReservationRepository repository){
        this.repository = repository;
    }

    @GetMapping("/{id}")
    public Reservation getReservationServiceById(Long id) {
        ReservationEntity reservationEntity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Not found exception by id = " + id));
        return toDomainReservation(reservationEntity);
    }

    @GetMapping()
    public List<Reservation> findAllReservations() {
        List <ReservationEntity> allEntities = repository.findAll();
        return allEntities.stream()
                .map(this::toDomainReservation).toList();
    }

    public Reservation createReservation(Reservation reservationToCreate) {
        if (reservationToCreate.id() != null){
            throw new IllegalArgumentException("ID should be empty");
        }
        if (reservationToCreate.status() != null){
            throw new IllegalArgumentException("Status should be empty");
        }
        var entityToSave = new ReservationEntity(null,
                reservationToCreate.userId(),
                reservationToCreate.roomId(),
                reservationToCreate.startDate(),
                reservationToCreate.endDate(),
                ReservationStatus.PENDING
        );

        var savedEntity = repository.save(entityToSave);
        return toDomainReservation(savedEntity);

    }

    public Reservation updateReservation(Long id, Reservation reservationToUpdate) {

        var reservationEntity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Reservation was not found by ID"));

        if (reservationEntity.getStatus() != ReservationStatus.PENDING){
            throw new IllegalStateException("Cannot modify reservation: status = " + reservationEntity.getStatus());
        }
        var reservationToSave = new ReservationEntity(reservationEntity.getId(),
                reservationToUpdate.userId(),
                reservationToUpdate.roomId(),
                reservationToUpdate.startDate(),
                reservationToUpdate.endDate(),
                ReservationStatus.PENDING
        );
        var updatedReservation = repository.save(reservationToSave);
        return toDomainReservation(updatedReservation);
    }

    @Transactional
    public void cancelReservation(Long id) {
        if (!repository.existsById(id))
        {
            throw new EntityNotFoundException("Reservation was not found by ID");
        }
        repository.setStatus(id, ReservationStatus.CANCELLED);
        log.info("Reservation successfully canceled: id = {}", id);
    }

    public Reservation approveReservation(Long id) {
        var reservationEntity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Reservation was not found by ID"));

        if (reservationEntity.getStatus() != ReservationStatus.PENDING){
            throw new IllegalStateException("Cannot modify reservation: status = " + reservationEntity.getStatus());
        }
        var isConflicted = isReservationConflicted(reservationEntity);
        if (isConflicted){
            throw new IllegalStateException("Cannot approve reservation because of conflict of dates");
        }

        reservationEntity.setStatus(ReservationStatus.APPROVED);
        repository.save(reservationEntity);
        return toDomainReservation(reservationEntity);
    }

    private boolean isReservationConflicted(ReservationEntity reservation){
        var allReservations = repository.findAll();
        for (ReservationEntity existingReservation: allReservations){
            if (reservation.getId().equals(existingReservation.getId())){
                continue;
            }
            if (!reservation.getRoomId().equals(existingReservation.getRoomId())){
                continue;
            }
            if (!existingReservation.getStatus().equals(ReservationStatus.APPROVED)){
                continue;
            }
            if (reservation.getStartDate().isBefore(existingReservation.getEndDate())
            && existingReservation.getStartDate().isBefore(reservation.getEndDate())){
                return true;
            }
        }
        return false;
    }
    private Reservation toDomainReservation(ReservationEntity reservation)
    {
        return new Reservation(
                reservation.getId(),
                reservation.getUserId(),
                reservation.getRoomId(),
                reservation.getStartDate(),
                reservation.getEndDate(),
                reservation.getStatus()
        );
    }

}