package com.uva.api.bookings.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import com.uva.api.bookings.api.HotelApi;
import com.uva.api.bookings.api.UserApi;
import com.uva.api.bookings.exceptions.BookingNotFoundException;
import com.uva.api.bookings.exceptions.InvalidDateRangeException;
import com.uva.api.bookings.models.Booking;
import com.uva.api.bookings.models.external.users.ClientDTO;
import com.uva.api.bookings.models.external.users.ClientStatus;
import com.uva.api.bookings.repositories.BookingRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private HotelApi hotelApi;

    @Autowired
    private UserApi userApi;

    /**
     * Consulta por bloques filtrados
     * - fechas
     * - roomId/hotelId
     * - userId
     * 
     * @param start
     * @param end
     * @param hotelId
     * @param roomId
     * @param userId
     * @return
     */
    public ResponseEntity<?> getBookings(
            LocalDate start, LocalDate end,
            Integer hotelId, Integer roomId,
            Integer userId, Integer managerId) {
        List<Booking> bookings = null;

        if (start != null && end != null) {
            if (start.isAfter(end))
                throw new InvalidDateRangeException("Start can't be before than end");

            bookings = bookingRepository.findAllInDateRange(start, end);
        }

        if (roomId != null) {
            if (bookings == null) {
                bookings = bookingRepository.findAllByRoomId(roomId);
            } else {
                bookings = bookings.stream()
                        .filter(booking -> booking.getRoomId() == roomId)
                        .toList();
            }
        } else if (hotelId != null) {
            if (bookings == null) {
                bookings = bookingRepository.findAllByHotelId(hotelId);
            } else {
                bookings = bookings.stream()
                        .filter(booking -> booking.getHotelId() == hotelId)
                        .toList();
            }
        }

        if (userId != null) {
            if (bookings == null) {
                bookings = bookingRepository.findAllByUserId(userId);
            } else {
                bookings = bookings.stream()
                        .filter(booking -> booking.getUserId() == userId)
                        .toList();
            }
        } else if (managerId != null) {
            if (bookings == null) {
                bookings = bookingRepository.findAllByManagerId(managerId);
            } else {
                bookings = bookings.stream()
                        .filter(booking -> booking.getManagerId() == managerId)
                        .toList();
            }
        }

        if (bookings == null) {
            bookings = bookingRepository.findAll();
        }

        return ResponseEntity.ok(bookings);
    }

    public ResponseEntity<Booking> createBooking(Booking booking) {
        if (booking.getId() != null)
            booking.setId(null);

        if (booking.getStart().isAfter(booking.getEnd()))
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST,
                    "La reserva no puede acabar antes de que empiece");

        int userId = booking.getUserId();
        int roomId = booking.getRoomId();
        int hotelId = booking.getHotelId();
        int managerId = booking.getManagerId();

        List<Booking> existingBookings = bookingRepository.findAllByRoomIdInDateRange(roomId, booking.getStart(),
                booking.getEnd());

        // Local checks first
        if (!existingBookings.isEmpty())
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST,
                    "Room is not available for the selected dates");

        if (!userApi.existsManagerById(managerId))
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Manager not found");

        if (!hotelApi.existsById(hotelId, roomId))
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Hotel or room not found");

        ClientDTO client = userApi.findClientById(userId);

        if (client == null)
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "User not found");

        if (client.getStatus() != ClientStatus.WITH_ACTIVE_BOOKINGS)
            userApi.updateClientState(userId, ClientStatus.WITH_ACTIVE_BOOKINGS);

        booking = bookingRepository.save(booking);

        return ResponseEntity.ok(booking);
    }

    public Booking findById(Integer id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new BookingNotFoundException(id));
    }

    public ResponseEntity<?> getBookingById(Integer id) {
        Booking booking = findById(id);
        bookingRepository.deleteById(id);
        return ResponseEntity.ok(booking);
    }

    private ClientStatus calculateClientStatus(int id) {
        return calculateClientStatus(id, null);
    }

    private ClientStatus calculateClientStatus(int id, LocalDate date) {

        date = date != null ? date : LocalDate.now();

        boolean hasActiveBookings = bookingRepository.existsActiveByUserIdAndDate(id, date);
        boolean hasInactiveBookings = bookingRepository.existsInactiveByUserIdAndDate(id, date);

        ClientStatus status;
        if (hasActiveBookings) {
            status = ClientStatus.WITH_ACTIVE_BOOKINGS;
        } else if (hasInactiveBookings) {
            status = ClientStatus.WITH_INACTIVE_BOOKINGS;
        } else {
            status = ClientStatus.NO_BOOKINGS;
        }
        return status;
    }

    public ResponseEntity<?> deleteBooking(Integer id) {
        Booking booking = findById(id);
        bookingRepository.deleteById(id);

        ClientStatus status = calculateClientStatus(id);
        // In this case, the check if the client has already de state is expensive
        userApi.updateClientState(id, status);

        return ResponseEntity.ok(booking);
    }

    public List<Booking> deleteAllByHotelId(int hotelId) {
        // Extraer reservas realizadas al hotel
        List<Booking> bookings = bookingRepository.findAllByHotelId(hotelId);
        if (bookings.isEmpty()) {
            return new ArrayList<>();
        }
        bookingRepository.deleteAllByHotelId(hotelId);
        return bookings;
    }

    public List<Booking> deleteAllByManagerId(int managerId) {
        List<Booking> bookings = bookingRepository.findAllByManagerId(managerId);
        if (bookings.isEmpty()) {
            return new ArrayList<>();
        }
        bookingRepository.deleteAllByManagerId(managerId);
        return bookings;
    }

    public List<Booking> deleteAllByUserId(Integer userId) {
        List<Booking> bookings = bookingRepository.findAllByUserId(userId);
        if (bookings.isEmpty()) {
            return new ArrayList<>();
        }
        bookingRepository.deleteAllByUserId(userId);
        return bookings;
    }

    public ResponseEntity<?> deleteBookings(
            Integer hotelId, Integer managerId, Integer userId) {
        List<Booking> bookings;
        String message;
        if (managerId != null) {
            bookings = deleteAllByManagerId(managerId);
            message = "No bookings for this manager";
        } else if (hotelId != null) {
            bookings = deleteAllByHotelId(hotelId);
            message = "No bookings for this hotel";
        } else if (userId != null) {
            bookings = deleteAllByUserId(userId);
            message = "No bookings for this hotel";
        } else {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST);
        }
        if (bookings.isEmpty()) {
            throw new HttpClientErrorException(HttpStatus.NOT_FOUND, message);
        }
        return ResponseEntity.ok(bookings);
    }

    public long performDailyClientsStateUpdate() {
        LocalDate yesterday = LocalDate.now().minusDays(1);

        List<Booking> passedBookings = bookingRepository.findAllPassed(yesterday);

        Stream<Integer> userIds = passedBookings.stream().map(b -> b.getUserId()).distinct();
        userIds.forEach(userId -> {
            ClientStatus status = calculateClientStatus(userId);
            userApi.updateClientState(userId, status);
        });

        return userIds.count();
    }
}
