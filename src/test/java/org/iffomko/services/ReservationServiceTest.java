package org.iffomko.services;

import org.iffomko.domain.ActivityType;
import org.iffomko.domain.Coach;
import org.iffomko.domain.Reservation;
import org.iffomko.domain.User;
import org.iffomko.exceptions.LocalizedException;
import org.iffomko.repositories.ReservationRepository;
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
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private ReservationRepository reservationRepository;

    @InjectMocks
    private ReservationService reservationService;

    private User testUser;
    private Coach testCoach;
    private Reservation testReservation;

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

        testReservation = new Reservation();
        testReservation.setId(1);
        testReservation.setUser(testUser);
        testReservation.setCoach(testCoach);
        testReservation.setReservationDate(Instant.now());
    }

    @Test
    void saveShouldSaveReservationWhenValidData() {
        // Given
        when(userService.byId(testUser.getId())).thenReturn(Optional.of(testUser));
        when(reservationRepository.findByReservationDate(testReservation.getReservationDate()))
                .thenReturn(Optional.empty());
        when(reservationRepository.save(any(Reservation.class))).thenReturn(testReservation);

        // When
        reservationService.save(testReservation);

        // Then
        verify(userService).byId(testUser.getId());
        verify(reservationRepository).findByReservationDate(testReservation.getReservationDate());
        verify(reservationRepository).save(testReservation);
    }

    @Test
    void saveShouldThrowExceptionWhenUserNotFound() {
        // Given
        when(userService.byId(testUser.getId())).thenReturn(Optional.empty());

        // When & Then
        LocalizedException exception = assertThrows(LocalizedException.class,
                () -> reservationService.save(testReservation));

        verify(userService).byId(testUser.getId());
        verify(reservationRepository, never()).save(any(Reservation.class));
    }

    @Test
    void saveShouldThrowExceptionWhenReservationAlreadyExists() {
        // Given
        when(userService.byId(testUser.getId())).thenReturn(Optional.of(testUser));
        when(reservationRepository.findByReservationDate(testReservation.getReservationDate()))
                .thenReturn(Optional.of(testReservation));

        // When & Then
        LocalizedException exception = assertThrows(LocalizedException.class,
                () -> reservationService.save(testReservation));

        verify(userService).byId(testUser.getId());
        verify(reservationRepository).findByReservationDate(testReservation.getReservationDate());
        verify(reservationRepository, never()).save(any(Reservation.class));
    }

    @Test
    void byUserPhoneShouldReturnReservationsWhenUserExists() {
        // Given
        String phone = "+79123456789";
        List<Reservation> reservations = Arrays.asList(testReservation);

        when(userService.byPhone(phone)).thenReturn(Optional.of(testUser));
        when(reservationRepository.findAllByUserId(testUser.getId())).thenReturn(reservations);

        // When
        List<Reservation> result = reservationService.byUserPhone(phone);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testReservation.getId(), result.get(0).getId());
        verify(userService).byPhone(phone);
        verify(reservationRepository).findAllByUserId(testUser.getId());
    }

    @Test
    void byUserPhoneShouldThrowExceptionWhenUserNotFound() {
        // Given
        String phone = "+79999999999";
        when(userService.byPhone(phone)).thenReturn(Optional.empty());

        // When & Then
        LocalizedException exception = assertThrows(LocalizedException.class,
                () -> reservationService.byUserPhone(phone));

        verify(userService).byPhone(phone);
        verify(reservationRepository, never()).findAllByUserId(anyInt());
    }

    @Test
    void deleteShouldDeleteReservation() {
        // Given
        int reservationId = 1;

        // When
        reservationService.delete(reservationId);

        // Then
        verify(reservationRepository).deleteById(reservationId);
    }

    @Test
    void findForCoachIdAndCurrentDayShouldReturnReservationsWhenFound() {
        // Given
        int coachId = 1;
        List<Reservation> reservations = Arrays.asList(testReservation);

        when(reservationRepository.findAllByCoachIdAndDatePeriod(anyInt(), any(Instant.class), any(Instant.class)))
                .thenReturn(reservations);

        // When
        List<Reservation> result = reservationService.findForCoachIdAndCurrentDay(coachId);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(reservationRepository).findAllByCoachIdAndDatePeriod(eq(coachId), any(Instant.class), any(Instant.class));
    }

    @Test
    void findForCoachIdAndCurrentDayShouldReturnEmptyListWhenNoReservations() {
        // Given
        int coachId = 1;
        when(reservationRepository.findAllByCoachIdAndDatePeriod(anyInt(), any(Instant.class), any(Instant.class)))
                .thenReturn(Collections.emptyList());

        // When
        List<Reservation> result = reservationService.findForCoachIdAndCurrentDay(coachId);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(reservationRepository).findAllByCoachIdAndDatePeriod(eq(coachId), any(Instant.class), any(Instant.class));
    }

    @Test
    void findByPeriodShouldReturnReservationsWhenFound() {
        // Given
        Instant start = Instant.now();
        Instant end = Instant.now().plusSeconds(3600);
        List<Reservation> reservations = Arrays.asList(testReservation);

        when(reservationRepository.findAllByReservationDateBetween(start, end))
                .thenReturn(reservations);

        // When
        List<Reservation> result = reservationService.findByPeriod(start, end);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testReservation.getId(), result.get(0).getId());
        verify(reservationRepository).findAllByReservationDateBetween(start, end);
    }

    @Test
    void findByPeriodShouldReturnEmptyListWhenNoReservations() {
        // Given
        Instant start = Instant.now();
        Instant end = Instant.now().plusSeconds(3600);

        when(reservationRepository.findAllByReservationDateBetween(start, end))
                .thenReturn(Collections.emptyList());

        // When
        List<Reservation> result = reservationService.findByPeriod(start, end);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(reservationRepository).findAllByReservationDateBetween(start, end);
    }
}
