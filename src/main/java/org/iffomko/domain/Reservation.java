package org.iffomko.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import org.iffomko.dtos.ReservationDto;

import java.time.Instant;

@Entity
@Table(name = "reservations")
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @JoinColumn(name = "coach_id")
    @ManyToOne
    private Coach coach;
    @JoinColumn(name = "user_id", nullable = false)
    @ManyToOne
    private User user;
    @Column(name = "reservation_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSX", timezone = "UTC")
    private Instant reservationDate;

    public Reservation() {
    }

    public Reservation(int id, Coach coach, User user, Instant reservationDate) {
        this.id = id;
        this.coach = coach;
        this.user = user;
        this.reservationDate = reservationDate;
    }

    public ReservationDto toDto() {
        return new ReservationDto(this);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Coach getCoach() {
        return coach;
    }

    public void setCoach(Coach coach) {
        this.coach = coach;
    }

    public Instant getReservationDate() {
        return reservationDate;
    }

    public void setReservationDate(Instant reservationDate) {
        this.reservationDate = reservationDate;
    }
}
