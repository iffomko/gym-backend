package org.iffomko.services;

import org.iffomko.domain.Reservation;
import org.iffomko.exceptions.LocalizedException;
import org.iffomko.repositories.ReservationRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

@Service
public class ReservationService {
    private static final String YOU_ALREADY_RESERVED = "validation.reservation.you-already-reserved";
    private static final String USER_NOT_EXIST_MESSAGE = "validation.user.not-found";
    private static final String NOT_VALID_RESERVATION_MESSAGE = "validation.reservation.not-valid";
    private static final String TABLE_RESERVED_MESSAGE = "validation.reservation.table-reserved";

    private final UserService userService;
    private final ReservationRepository reservationRepository;

    public ReservationService(UserService userService,
                              ReservationRepository reservationRepository) {
        this.userService = userService;
        this.reservationRepository = reservationRepository;
    }

    public void save(Reservation reservation) {
        userService.byId(reservation.getUser().getId())
                .map(user -> {
                    if (isReserved(reservation, user.getId())) {
                        throw new LocalizedException(TABLE_RESERVED_MESSAGE);
                    }
                    reservation.setUser(user);
                    return reservationRepository.save(reservation);
                })
                .orElseThrow(() -> new LocalizedException(USER_NOT_EXIST_MESSAGE));
    }

    public List<Reservation> byUserPhone(String phone) {
        return userService.byPhone(phone)
                .map(user -> reservationRepository.findAllByUserId(user.getId()))
                .orElseThrow(() -> new LocalizedException(USER_NOT_EXIST_MESSAGE));
    }

    public void delete(int id) {
        reservationRepository.deleteById(id);
    }

    public List<Reservation> findForCoachIdAndCurrentDay(int coachId) {
        Instant beingDay = LocalDate.now()
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant();
        Instant endDay = LocalDate.now()
                .plusDays(1)
                .atStartOfDay(ZoneId.systemDefault())
                .minusSeconds(1)
                .toInstant();
        return reservationRepository.findAllByCoachIdAndDatePeriod(coachId, beingDay, endDay);
    }

    public List<Reservation> findByPeriod(Instant start, Instant end) {
        return reservationRepository.findAllByReservationDateBetween(start, end);
    }

    private boolean isReserved(Reservation reservation, int customerId) {
        if (reservation == null || reservation.getReservationDate() == null) {
            throw new LocalizedException(NOT_VALID_RESERVATION_MESSAGE);
        }
        Optional<Reservation> findReservation = reservationRepository.findByReservationDate(reservation.getReservationDate());
        if (findReservation.isPresent() && findReservation.get().getUser().getId() == customerId) {
            throw new LocalizedException(YOU_ALREADY_RESERVED);
        }
        return findReservation.isPresent();
    }
}
