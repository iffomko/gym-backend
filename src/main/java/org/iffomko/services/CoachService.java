package org.iffomko.services;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.iffomko.domain.Coach;
import org.iffomko.domain.Reservation;
import org.iffomko.exceptions.LocalizedException;
import org.iffomko.repositories.CoachesRepository;
import org.iffomko.repositories.ReservationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CoachService {
    
    private static final String COACH_NOT_FOUND_MESSAGE = "validation.coach.not-found";
    
    private final CoachesRepository coachesRepository;
    private final ReservationRepository reservationRepository;
    private final EntityManager entityManager;
    
    public CoachService(CoachesRepository coachesRepository, 
                       ReservationRepository reservationRepository,
                       EntityManager entityManager) {
        this.coachesRepository = coachesRepository;
        this.reservationRepository = reservationRepository;
        this.entityManager = entityManager;
    }
    
    /**
     * Транзакционный метод для удаления тренера и всех связанных с ним бронирований.
     * Обеспечивает целостность данных - если удаление бронирований не удастся,
     * то и удаление тренера будет отменено.
     * 
     * @param coachId ID тренера для удаления
     * @throws LocalizedException если тренер не найден
     */
    @Transactional
    public void deleteCoachWithReservations(int coachId) {
        Optional<Coach> coachOptional = coachesRepository.findById(coachId);
        if (coachOptional.isEmpty()) {
            throw new LocalizedException(COACH_NOT_FOUND_MESSAGE);
        }
        
        List<Reservation> reservations = findReservationsByCoachId(coachId);
        
        for (Reservation reservation : reservations) {
            reservationRepository.deleteById(reservation.getId());
        }
        
        coachesRepository.deleteById(coachId);
    }
    
    /**
     * Найти все бронирования для конкретного тренера используя Criteria API
     * @param coachId ID тренера
     * @return список бронирований
     */
    private List<Reservation> findReservationsByCoachId(int coachId) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Reservation> cq = cb.createQuery(Reservation.class);
        Root<Reservation> reservation = cq.from(Reservation.class);
        
        Predicate coachIdPredicate = cb.equal(reservation.get("coach").get("id"), coachId);
        cq.where(coachIdPredicate);
        
        TypedQuery<Reservation> query = entityManager.createQuery(cq);
        return query.getResultList();
    }
    
    /**
     * Получить тренера по ID
     * @param coachId ID тренера
     * @return опциональный тренер
     */
    public Optional<Coach> findById(int coachId) {
        return coachesRepository.findById(coachId);
    }
    
    /**
     * Получить всех тренеров
     * @return список всех тренеров
     */
    public List<Coach> findAll() {
        return coachesRepository.findAll();
    }
}
