package org.iffomko.services;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.iffomko.domain.Reservation;
import org.iffomko.domain.User;
import org.iffomko.domain.Coach;
import org.iffomko.domain.ActivityType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CriteriaApiServiceTest {

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
    private CriteriaApiService criteriaApiService;

    private User testUser;
    private Coach testCoach;
    private Reservation testReservation;
    private Instant reservationDate;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1);
        testUser.setPhone("+79123456789");
        testUser.setFirstName("Иван");
        testUser.setLastName("Иванов");

        testCoach = new Coach();
        testCoach.setId(1);
        testCoach.setFirstName("Василий");
        testCoach.setLastName("Пупкин");
        testCoach.setPhone("+79123753434");
        testCoach.setActivityType(ActivityType.BOXING);

        reservationDate = Instant.now();
        testReservation = new Reservation();
        testReservation.setId(1);
        testReservation.setUser(testUser);
        testReservation.setCoach(testCoach);
        testReservation.setReservationDate(reservationDate);
    }

    @Test
    void findAllByUserIdShouldReturnReservationsWhenUserExists() {
        // Given
        int userId = 1;
        List<Reservation> expectedReservations = Arrays.asList(testReservation);

        when(entityManager.getCriteriaBuilder()).thenReturn(criteriaBuilder);
        when(criteriaBuilder.createQuery(Reservation.class)).thenReturn(criteriaQuery);
        when(criteriaQuery.from(Reservation.class)).thenReturn(root);
        when(root.get("user")).thenReturn(mock(Root.class));
        when(root.get("user").get("id")).thenReturn(mock(Root.class));
        when(criteriaBuilder.equal(any(), eq(userId))).thenReturn(predicate);
        when(criteriaQuery.where(predicate)).thenReturn(criteriaQuery);
        when(entityManager.createQuery(criteriaQuery)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(expectedReservations);

        // When
        List<Reservation> result = criteriaApiService.findAllByUserId(userId);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testReservation.getId(), result.get(0).getId());
        assertEquals(userId, result.get(0).getUser().getId());

        verify(entityManager).getCriteriaBuilder();
        verify(criteriaBuilder).createQuery(Reservation.class);
        verify(criteriaQuery).from(Reservation.class);
        verify(criteriaBuilder).equal(any(), eq(userId));
        verify(criteriaQuery).where(predicate);
        verify(entityManager).createQuery(criteriaQuery);
        verify(typedQuery).getResultList();
    }

    @Test
    void findAllByUserIdShouldReturnEmptyListWhenNoReservationsFound() {
        // Given
        int userId = 999;
        List<Reservation> emptyList = Collections.emptyList();

        when(entityManager.getCriteriaBuilder()).thenReturn(criteriaBuilder);
        when(criteriaBuilder.createQuery(Reservation.class)).thenReturn(criteriaQuery);
        when(criteriaQuery.from(Reservation.class)).thenReturn(root);
        when(root.get("user")).thenReturn(mock(Root.class));
        when(root.get("user").get("id")).thenReturn(mock(Root.class));
        when(criteriaBuilder.equal(any(), eq(userId))).thenReturn(predicate);
        when(criteriaQuery.where(predicate)).thenReturn(criteriaQuery);
        when(entityManager.createQuery(criteriaQuery)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(emptyList);

        // When
        List<Reservation> result = criteriaApiService.findAllByUserId(userId);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void findByReservationDateShouldReturnReservationWhenFound() {
        // Given
        List<Reservation> reservations = Arrays.asList(testReservation);

        when(entityManager.getCriteriaBuilder()).thenReturn(criteriaBuilder);
        when(criteriaBuilder.createQuery(Reservation.class)).thenReturn(criteriaQuery);
        when(criteriaQuery.from(Reservation.class)).thenReturn(root);
        when(root.get("reservationDate")).thenReturn(mock(Root.class));
        when(criteriaBuilder.equal(any(), eq(reservationDate))).thenReturn(predicate);
        when(criteriaQuery.where(predicate)).thenReturn(criteriaQuery);
        when(entityManager.createQuery(criteriaQuery)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(reservations);

        // When
        Optional<Reservation> result = criteriaApiService.findByReservationDate(reservationDate);

        // Then
        assertTrue(result.isPresent());
        assertEquals(testReservation.getId(), result.get().getId());
        assertEquals(reservationDate.toEpochMilli(), result.get().getReservationDate().toEpochMilli());
    }

    @Test
    void findByReservationDateShouldReturnEmptyWhenNotFound() {
        // Given
        Instant reservationDate = Instant.now();
        List<Reservation> emptyList = Collections.emptyList();

        when(entityManager.getCriteriaBuilder()).thenReturn(criteriaBuilder);
        when(criteriaBuilder.createQuery(Reservation.class)).thenReturn(criteriaQuery);
        when(criteriaQuery.from(Reservation.class)).thenReturn(root);
        when(root.get("reservationDate")).thenReturn(mock(Root.class));
        when(criteriaBuilder.equal(any(), eq(reservationDate))).thenReturn(predicate);
        when(criteriaQuery.where(predicate)).thenReturn(criteriaQuery);
        when(entityManager.createQuery(criteriaQuery)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(emptyList);

        // When
        Optional<Reservation> result = criteriaApiService.findByReservationDate(reservationDate);

        // Then
        assertFalse(result.isPresent());
    }

    @Test
    void findByReservationDateShouldReturnFirstReservationWhenMultipleFound() {
        // Given
        Instant reservationDate = Instant.now();
        Reservation secondReservation = new Reservation();
        secondReservation.setId(2);
        secondReservation.setUser(testUser);
        secondReservation.setCoach(testCoach);
        secondReservation.setReservationDate(reservationDate);

        List<Reservation> reservations = Arrays.asList(testReservation, secondReservation);

        when(entityManager.getCriteriaBuilder()).thenReturn(criteriaBuilder);
        when(criteriaBuilder.createQuery(Reservation.class)).thenReturn(criteriaQuery);
        when(criteriaQuery.from(Reservation.class)).thenReturn(root);
        when(root.get("reservationDate")).thenReturn(mock(Root.class));
        when(criteriaBuilder.equal(any(), eq(reservationDate))).thenReturn(predicate);
        when(criteriaQuery.where(predicate)).thenReturn(criteriaQuery);
        when(entityManager.createQuery(criteriaQuery)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(reservations);

        // When
        Optional<Reservation> result = criteriaApiService.findByReservationDate(reservationDate);

        // Then
        assertTrue(result.isPresent());
        assertEquals(testReservation.getId(), result.get().getId()); // Should return first one
    }
}
