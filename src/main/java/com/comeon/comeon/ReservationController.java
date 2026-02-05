package com.comeon.comeon;

import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

import static org.slf4j.LoggerFactory.getLogger;

@RestController
@RequestMapping("/reservation")
public class ReservationController
{
    private static final Logger log = getLogger(ReservationController.class);
    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService){
        this.reservationService = reservationService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Reservation> getReservationById(@PathVariable("id") Long id){
        return ResponseEntity.status(HttpStatus.OK)
                .body(reservationService.getReservationServiceById(id));
    }
    @GetMapping()
    public ResponseEntity<List<Reservation>> getAllReservations(){
        log.info("Called getAllReservations method");
        return ResponseEntity.ok(reservationService.findAllReservations());
    }

    @PostMapping()
    public ResponseEntity<Reservation> createReservation(@RequestBody Reservation reservationToCreate){
        log.info("createReservation method is called");
        return ResponseEntity.status(HttpStatus.CREATED)
                .header("test-header", "123")
                .body(reservationService.createReservation(reservationToCreate));
        //return reservationService.createReservation(reservationToCreate);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Reservation> updateReservation(
            @PathVariable("id") Long id,
            @RequestBody Reservation reservationToUpdate
    )
    {
        log.info("Called method updateReservation: id = {}, reservationToUpdate = {}", id, reservationToUpdate);
        var updatedReservation = reservationService.updateReservation(id, reservationToUpdate);
        return ResponseEntity.ok(updatedReservation);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReservation(
            @PathVariable("id") Long id
    )
    {
        log.info("Called method deleteReservation: id = {}", id);
        try {
            reservationService.deleteReservation(id);
            return ResponseEntity.ok()
                    .build();
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound()
                    .build();
        }
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<Reservation> approveReservation(
            @PathVariable("id") Long id
    ){
        log.info("Method approveReservation is called: id = {}", id);
        var reservation = reservationService.approveReservation(id);
        return ResponseEntity.ok(reservation);
    }
}
