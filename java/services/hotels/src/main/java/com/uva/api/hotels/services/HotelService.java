package com.uva.api.hotels.services;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.uva.api.hotels.apis.BookingAPI;
import com.uva.api.hotels.apis.ManagerAPI;
import com.uva.api.hotels.exceptions.HotelNotFoundException;
import com.uva.api.hotels.exceptions.InvalidDateRangeException;
import com.uva.api.hotels.exceptions.InvalidRequestException;
import com.uva.api.hotels.models.Hotel;
import com.uva.api.hotels.models.Room;
import com.uva.api.hotels.models.external.users.UserRol;
import com.uva.api.hotels.repositories.HotelRepository;
import com.uva.api.hotels.repositories.RoomRepository;

@Service
public class HotelService {
    @Autowired
    private TokenService tokenService;
    @Autowired
    private HotelRepository hotelRepository;
    @Autowired
    private RoomRepository roomRepository;
    @Autowired
    private BookingAPI bookingAPI;
    @Autowired
    private ManagerAPI managerAPI;

    public ResponseEntity<List<Hotel>> getAllHotels(String token, Integer managerId, LocalDate start, LocalDate end) {

        if (managerId != null)
            tokenService.assertPermission(token, managerId);

        List<Hotel> hotels = (managerId != null)
                ? hotelRepository.findAllByManagerId(managerId)
                : hotelRepository.findAll();
        if (start != null && end != null) {
            if (start.isAfter(end))
                throw new InvalidDateRangeException("La fecha de inicio debe ser anterior a la fecha de fin");

            Set<Integer> notAvailableRoomsId = bookingAPI.getNotAvailableRooms(start, end);

            hotels = hotels.stream().map(h -> {
                List<Room> rooms = h.getRooms().stream()
                        .filter(r -> !notAvailableRoomsId.contains(r.getId()) && r.isAvailable())
                        .toList();
                h.setRooms(rooms);
                return h;
            }).filter(h -> !h.getRooms().isEmpty()).toList();
        }
        if (hotels.isEmpty())
            throw new InvalidRequestException("No hotels");

        if (!tokenService.hasAnyRole(token, UserRol.ADMIN, UserRol.MANAGER))
            hotels = hotels.stream().map(h -> {
                h.setManagerId(null);
                return h;
            }).toList();
        return ResponseEntity.ok(hotels);
    }

    public ResponseEntity<Hotel> addHotel(Hotel hotel) {
        boolean exist = managerAPI.existsManagerById(hotel.getManagerId());
        System.out.println("Id del manager" + hotel.getManagerId());
        if (!exist) {
            throw new InvalidRequestException("No existe el manager con id " + hotel.getManagerId());
        }
        hotel = hotelRepository.save(hotel);
        return new ResponseEntity<>(hotel, HttpStatus.CREATED);
    }

    public ResponseEntity<Hotel> getHotelById(String token, int id) {
        Hotel h = hotelRepository.findById(id)
                .orElseThrow(() -> new HotelNotFoundException(id));
        tokenService.assertPermission(token, h.getManagerId());
        return ResponseEntity.ok(h);
    }

    public ResponseEntity<List<Hotel>> deleteHotelsByManagerId(String token, int managerId) {
        tokenService.assertPermission(token, managerId);
        List<Hotel> hotels = hotelRepository.findAllByManagerId(managerId);
        if (hotels.isEmpty())
            throw new InvalidRequestException("No hay hoteles para el manager con id " + managerId);

        bookingAPI.deleteAllByManagerId(managerId);
        hotelRepository.deleteAll(hotels);
        return ResponseEntity.ok(hotels);
    }

    public ResponseEntity<Hotel> deleteHotel(String token, int id) {
        Hotel target = hotelRepository.findById(id)
                .orElseThrow(() -> new HotelNotFoundException(id));
        tokenService.assertPermission(token, target.getManagerId());
        bookingAPI.deleteAllByHotelId(id);
        hotelRepository.delete(target);
        return ResponseEntity.ok(target);
    }

    public ResponseEntity<List<Room>> getRoomsFromHotel(int hotelId, LocalDate start, LocalDate end) {
        List<Room> rooms = roomRepository.findAllByHotelId(hotelId);
        if (start != null && end != null) {
            if (!start.isBefore(end)) {
                throw new InvalidDateRangeException("La fecha de inicio debe ser anterior a la fecha de fin");
            }
            Set<Integer> notAvailableRoomsId = bookingAPI.getNotAvailableRooms(hotelId, start, end);
            rooms = rooms.stream()
                    .filter(r -> !notAvailableRoomsId.contains(r.getId()) && r.isAvailable())
                    .toList();
        }
        return ResponseEntity.ok(rooms);
    }

    public ResponseEntity<Room> updateRoomAvailability(String token, int hotelId, int roomId, boolean available) {
        Hotel hotel = hotelRepository.findById(hotelId).orElseThrow(() -> new HotelNotFoundException(roomId));
        Room targetRoom = roomRepository.findByIdAndHotelId(roomId, hotelId)
                .orElseThrow(() -> new InvalidRequestException("Habitación no encontrada"));
        tokenService.assertPermission(token, hotel.getManagerId());
        targetRoom.setAvailable(available);
        targetRoom = roomRepository.save(targetRoom);
        return ResponseEntity.ok(targetRoom);
    }

    public ResponseEntity<Room> getRoomByIdFromHotel(int hotelId, int roomId) {
        Room r = roomRepository.findByIdAndHotelId(roomId, hotelId)
                .orElseThrow(() -> new HotelNotFoundException(hotelId));
        return ResponseEntity.ok(r);
    }
}
