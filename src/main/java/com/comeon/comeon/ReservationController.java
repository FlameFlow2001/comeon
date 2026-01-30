package com.comeon.comeon;

import org.slf4j.Logger;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

@RestController
public class ReservationController
{
    private static final Logger log = getLogger(ReservationController.class);
    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService){
        this.reservationService = reservationService;
    }

    @GetMapping("/{id}")
    public Reservation getReservationById(@PathVariable("id") Long id){
        return reservationService.getReservationServiceById(id);
    }
    @GetMapping()
    public List<Reservation> getAllReservations(){
        log.info("Called getAllReservations method");
        return reservationService.findAllReservations();
    }
}
