package org.iffomko.dtos;

import org.iffomko.domain.Coach;
import org.iffomko.domain.Reservation;

import java.time.Instant;

public class ReservationDto {
    private int id;
    private Coach coach;
    private UserDto user;
    private Instant reservationDate;

    public ReservationDto() {
    }

    public ReservationDto(Reservation reservation) {
        this.id = reservation.getId();
        this.coach = reservation.getCoach();
        this.user = reservation.getUser().toDto();
        this.reservationDate = reservation.getReservationDate();
    }

    public Reservation toEntity() {
        return new Reservation(id, coach, user.toEntity(), reservationDate);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Coach getCoach() {
        return coach;
    }

    public void setCoach(Coach coach) {
        this.coach = coach;
    }

    public UserDto getUser() {
        return user;
    }

    public void setUser(UserDto user) {
        this.user = user;
    }

    public Instant getReservationDate() {
        return reservationDate;
    }

    public void setReservationDate(Instant reservationDate) {
        this.reservationDate = reservationDate;
    }
}
