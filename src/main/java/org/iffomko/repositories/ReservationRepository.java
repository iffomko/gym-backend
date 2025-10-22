package org.iffomko.repositories;

import org.iffomko.domain.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Integer> {
    List<Reservation> findAllByUserId(int customerId);

    void deleteById(int id);

    Optional<Reservation> findByReservationDate(Instant reservationDate);

    List<Reservation> findAllByReservationDateBetween(Instant start, Instant end);

    @Query(
        """
        select r from Reservation r
        where r.coach.id = :coachId and r.reservationDate between :start and :end
        """
    )
    List<Reservation> findAllByCoachIdAndDatePeriod(int coachId, Instant start, Instant end);
}
