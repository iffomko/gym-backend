package org.iffomko.controllers;

import org.iffomko.domain.Reservation;
import org.iffomko.dtos.ReservationDto;
import org.iffomko.services.ReservationService;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

import static org.iffomko.domain.ControllerNames.*;

@RestController
@RequestMapping(RESERVATION_URL)
public class ReservationController {
    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @PostMapping
    public void makeReservation(@RequestBody ReservationDto reservationDto) {
        reservationService.save(reservationDto.toEntity());
    }

    @GetMapping
    public List<ReservationDto> loadReservations(@RequestParam("userPhone") String phone) {
        return reservationService.byUserPhone(phone)
                .stream()
                .map(Reservation::toDto)
                .toList();
    }

    @DeleteMapping("/{id}")
    public void deleteReservation(@PathVariable("id") int id) {
        reservationService.delete(id);
    }

    @GetMapping(COACHES_URI_PART + "/{coachId}")
    public List<ReservationDto> loadByDayForCoach(@PathVariable("coachId") Integer coachId) {
        return reservationService.findForCoachIdAndCurrentDay(coachId)
                .stream()
                .map(Reservation::toDto)
                .toList();
    }

    @GetMapping
    public List<ReservationDto> loadForPeriodAndUser(
            @RequestParam("begin") Instant begin,
            @RequestParam("end") Instant end
    ) {
        return reservationService.findByPeriod(begin, end)
                .stream()
                .map(Reservation::toDto)
                .toList();
    }
}
