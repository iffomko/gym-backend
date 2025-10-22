package org.iffomko.services;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.iffomko.criteria.ReservationCriteriaRepository;
import org.iffomko.domain.Reservation;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class CriteriaApiService implements ReservationCriteriaRepository {

    private final EntityManager entityManager;

    public CriteriaApiService(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public List<Reservation> findAllByUserId(int userId) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Reservation> cq = cb.createQuery(Reservation.class);
        Root<Reservation> reservation = cq.from(Reservation.class);
        
        Predicate userIdPredicate = cb.equal(reservation.get("user").get("id"), userId);
        cq.where(userIdPredicate);
        
        TypedQuery<Reservation> query = entityManager.createQuery(cq);
        return query.getResultList();
    }

    @Override
    public Optional<Reservation> findByReservationDate(Instant reservationDate) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Reservation> cq = cb.createQuery(Reservation.class);
        Root<Reservation> reservation = cq.from(Reservation.class);
        
        Predicate datePredicate = cb.equal(reservation.get("reservationDate"), reservationDate);
        cq.where(datePredicate);
        
        TypedQuery<Reservation> query = entityManager.createQuery(cq);
        List<Reservation> results = query.getResultList();
        
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }
}
