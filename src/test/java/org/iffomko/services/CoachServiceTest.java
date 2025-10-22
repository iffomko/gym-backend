package org.iffomko.services;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.iffomko.domain.Coach;
import org.iffomko.domain.Reservation;
import org.iffomko.domain.User;
import org.iffomko.domain.ActivityType;
import org.iffomko.exceptions.LocalizedException;
import org.iffomko.repositories.CoachesRepository;
import org.iffomko.repositories.ReservationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.*;
import java.util.ResourceBundle;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CoachServiceTest {

    @Mock
    private CoachesRepository coachesRepository;

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private EntityManager entityManager;

    @Mock
    private CriteriaBuilder criteriaBuilder;

    @Mock
    private CriteriaQuery<Reservation> criteriaQuery;

    @Mock
    private Root<Reservation> root;

    @Mock
    private Predicate predicate;

    @Mock
    private TypedQuery<Reservation> typedQuery;

    @InjectMocks
    private CoachService coachService;

    private Coach testCoach;
    private User testUser;
    private Reservation testReservation;

    private ResourceBundle bundle = ResourceBundle.getBundle("messages");

    @BeforeEach
    void setUp() {
        testCoach = new Coach();
        testCoach.setId(1);
        testCoach.setFirstName("Василий");
        testCoach.setLastName("Пупкин");
        testCoach.setPhone("+79123753434");
        testCoach.setActivityType(ActivityType.BOXING);

        testUser = new User();
        testUser.setId(1);
        testUser.setPhone("+79123456789");
        testUser.setFirstName("Иван");
        testUser.setLastName("Иванов");

        testReservation = new Reservation();
        testReservation.setId(1);
        testReservation.setUser(testUser);
        testReservation.setCoach(testCoach);
        testReservation.setReservationDate(Instant.now());
    }

    @Test
    void findByIdShouldReturnCoachWhenExists() {
        // Given
        int coachId = 1;
        when(coachesRepository.findById(coachId)).thenReturn(Optional.of(testCoach));

        // When
        Optional<Coach> result = coachService.findById(coachId);

        // Then
        assertTrue(result.isPresent());
        assertEquals(testCoach.getId(), result.get().getId());
        assertEquals(testCoach.getFirstName(), result.get().getFirstName());
        verify(coachesRepository).findById(coachId);
    }

    @Test
    void findByIdShouldReturnEmptyWhenNotExists() {
        // Given
        int coachId = 999;
        when(coachesRepository.findById(coachId)).thenReturn(Optional.empty());

        // When
        Optional<Coach> result = coachService.findById(coachId);

        // Then
        assertFalse(result.isPresent());
        verify(coachesRepository).findById(coachId);
    }

    @Test
    void findAllShouldReturnAllCoaches() {
        // Given
        Coach secondCoach = new Coach();
        secondCoach.setId(2);
        secondCoach.setFirstName("Василиса");
        secondCoach.setLastName("Чудесная");
        secondCoach.setPhone("+79123845767");
        secondCoach.setActivityType(ActivityType.DANCING);

        List<Coach> coaches = Arrays.asList(testCoach, secondCoach);
        when(coachesRepository.findAll()).thenReturn(coaches);

        // When
        List<Coach> result = coachService.findAll();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(testCoach.getId(), result.get(0).getId());
        assertEquals(secondCoach.getId(), result.get(1).getId());
        verify(coachesRepository).findAll();
    }

    @Test
    void findAllShouldReturnEmptyListWhenNoCoaches() {
        // Given
        when(coachesRepository.findAll()).thenReturn(Collections.emptyList());

        // When
        List<Coach> result = coachService.findAll();

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(coachesRepository).findAll();
    }

    @Test
    void deleteCoachWithReservationsShouldThrowExceptionWhenCoachNotFound() {
        // Given
        int coachId = 999;
        when(coachesRepository.findById(coachId)).thenReturn(Optional.empty());

        // When & Then
        LocalizedException exception = assertThrows(LocalizedException.class, 
            () -> coachService.deleteCoachWithReservations(coachId));
        
        assertEquals(bundle.getString("validation.coach.not-found"), exception.getMessage());
        verify(coachesRepository).findById(coachId);
        verify(coachesRepository, never()).deleteById(anyInt());
        verify(reservationRepository, never()).deleteById(anyInt());
    }

    @Test
    void deleteCoachWithReservationsShouldDeleteCoachAndReservationsWhenCoachExists() {
        // Given
        int coachId = 1;
        List<Reservation> reservations = Arrays.asList(testReservation);

        when(coachesRepository.findById(coachId)).thenReturn(Optional.of(testCoach));
        when(entityManager.getCriteriaBuilder()).thenReturn(criteriaBuilder);
        when(criteriaBuilder.createQuery(Reservation.class)).thenReturn(criteriaQuery);
        when(criteriaQuery.from(Reservation.class)).thenReturn(root);
        when(root.get("coach")).thenReturn(mock(Root.class));
        when(root.get("coach").get("id")).thenReturn(mock(Root.class));
        when(criteriaBuilder.equal(any(), eq(coachId))).thenReturn(predicate);
        when(criteriaQuery.where(predicate)).thenReturn(criteriaQuery);
        when(entityManager.createQuery(criteriaQuery)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(reservations);

        // When
        coachService.deleteCoachWithReservations(coachId);

        // Then
        verify(coachesRepository).findById(coachId);
        verify(reservationRepository).deleteById(testReservation.getId());
        verify(coachesRepository).deleteById(coachId);
    }

    @Test
    void deleteCoachWithReservationsShouldDeleteCoachOnlyWhenNoReservations() {
        // Given
        int coachId = 1;
        List<Reservation> emptyReservations = Collections.emptyList();

        when(coachesRepository.findById(coachId)).thenReturn(Optional.of(testCoach));
        when(entityManager.getCriteriaBuilder()).thenReturn(criteriaBuilder);
        when(criteriaBuilder.createQuery(Reservation.class)).thenReturn(criteriaQuery);
        when(criteriaQuery.from(Reservation.class)).thenReturn(root);
        when(root.get("coach")).thenReturn(mock(Root.class));
        when(root.get("coach").get("id")).thenReturn(mock(Root.class));
        when(criteriaBuilder.equal(any(), eq(coachId))).thenReturn(predicate);
        when(criteriaQuery.where(predicate)).thenReturn(criteriaQuery);
        when(entityManager.createQuery(criteriaQuery)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(emptyReservations);

        // When
        coachService.deleteCoachWithReservations(coachId);

        // Then
        verify(coachesRepository).findById(coachId);
        verify(reservationRepository, never()).deleteById(anyInt());
        verify(coachesRepository).deleteById(coachId);
    }
}
