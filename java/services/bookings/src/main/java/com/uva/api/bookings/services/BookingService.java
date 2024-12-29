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
import java.util.function.Consumer;
import java.util.function.Function;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private HotelApi hotelApi;

    @Autowired
    private UserApi userApi;

    public ResponseEntity<?> getBookings(
            String token,
            LocalDate start, LocalDate end,
            Integer hotelId, Integer roomId,
            Integer userId, Integer managerId) {
        List<Booking> bookings = null;

        if (hotelId != null)
            tokenService.assertPermission(token, hotelId);
        if (userId != null)
            tokenService.assertPermission(token, userId);
        if (managerId != null)
            tokenService.assertPermission(token, managerId);

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

    /**
     * Consulta una reserva por id y asegura que la entidad que la consulte sea un
     * servicio/administrador o el dueño (cliente)
     * 
     * @param token
     * @param id
     * @return
     */
    public Booking findById(String token, Integer id) {
        Booking b = bookingRepository.findById(id)
                .orElseThrow(() -> new BookingNotFoundException(id));
        tokenService.assertPermission(token, b.getUserId());
        return b;
    }

    public ResponseEntity<?> getBookingById(String token, Integer id) {
        Booking booking = findById(token, id);
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

    public ResponseEntity<?> deleteBooking(String token, Integer id) {
        Booking booking = findById(token, id);
        bookingRepository.deleteById(id);
        bookingRepository.flush();

        ClientStatus status = calculateClientStatus(booking.getUserId());
        // In this case, the check if the client has already de state is expensive
        userApi.updateClientState(booking.getUserId(), status);

        return ResponseEntity.ok(booking);
    }

    private List<Booking> deleteAll(int id,
            Function<Integer, List<Booking>> findAction,
            Consumer<Integer> deleteAction) {
        List<Booking> bookings = findAction.apply(id);

        if (bookings.isEmpty())
            return new ArrayList<>();

        deleteAction.accept(id);

        return bookings;
    }

    private List<Booking> deleteAllByHotelId(Integer userId) {
        return deleteAll(userId,
                bookingRepository::findAllByHotelId,
                bookingRepository::deleteAllByHotelId);
    }

    private List<Booking> deleteAllByManagerId(Integer userId) {
        return deleteAll(userId,
                bookingRepository::findAllByManagerId,
                bookingRepository::deleteAllByManagerId);
    }

    private List<Booking> deleteAllByUserId(Integer userId) {
        return deleteAll(userId,
                bookingRepository::findAllByUserId,
                bookingRepository::deleteAllByUserId);
    }

    public ResponseEntity<?> deleteBookings(
            Integer hotelId,
            Integer managerId, Integer userId) {

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

    /**
     * Obtiene los ids de los cliente cuyas reservas finalizaron el dia anterior y
     * actualiza su estado al nuevo
     * 
     * @return
     */
    public long performDailyClientsStateUpdate() {
        LocalDate yesterday = LocalDate.now().minusDays(1);

        List<Booking> passedBookings = bookingRepository.findAllPassed(yesterday);

        return passedBookings.stream().map(Booking::getUserId).distinct().map(userId -> {
            ClientStatus status = calculateClientStatus(userId);
            userApi.updateClientState(userId, status);
            return userId;
        }).count();
    }
}
