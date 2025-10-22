package org.iffomko.criteria;

import org.iffomko.domain.Reservation;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface ReservationCriteriaRepository {
    
    /**
     * Найти все бронирования по ID пользователя
     * @param userId ID пользователя
     * @return список бронирований
     */
    List<Reservation> findAllByUserId(int userId);
    
    /**
     * Найти бронирование по дате
     * @param reservationDate дата бронирования
     * @return опциональное бронирование
     */
    Optional<Reservation> findByReservationDate(Instant reservationDate);
}
