package com.uva.api.hotels.services;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uva.api.hotels.apis.BookingAPI;
import com.uva.api.hotels.apis.ManagerAPI;
import com.uva.api.hotels.exceptions.HotelNotFoundException;
import com.uva.api.hotels.exceptions.InvalidDateRangeException;
import com.uva.api.hotels.exceptions.InvalidRequestException;
import com.uva.api.hotels.models.Hotel;
import com.uva.api.hotels.models.Room;
import com.uva.api.hotels.repositories.HotelRepository;
import com.uva.api.hotels.repositories.RoomRepository;

@Service
public class HotelService {
    @Autowired
    private HotelRepository hotelRepository;
    @Autowired
    private RoomRepository roomRepository;
    @Autowired
    private BookingAPI bookingAPI;
    @Autowired
    private ManagerAPI managerAPI;

    public List<Hotel> getAllHotels(Integer managerId, LocalDate start, LocalDate end) {
        List<Hotel> hotels = (managerId != null)
                ? hotelRepository.findAllByManagerId(managerId)
                : hotelRepository.findAll();
        if (start != null && end != null) {
            if (start.isAfter(end))
                throw new InvalidDateRangeException("La fecha de inicio debe ser anterior a la fecha de fin");

            Set<Integer> notAvailableRoomsId = bookingAPI.getNotAvailableRooms(start, end);
            notAvailableRoomsId.forEach(k -> System.err.println(k));
            hotels = hotels.stream().map(h -> {
                List<Room> rooms = h.getRooms().stream()
                        .filter(r -> !notAvailableRoomsId.contains(r.getId()) && r.isAvailable())
                        .toList();
                h.setRooms(rooms);
                return h;
            }).filter(h -> !h.getRooms().isEmpty()).toList();
        }
        return hotels;
    }

    public Hotel addHotel(Hotel hotel) {
        boolean exist = managerAPI.existsManagerById(hotel.getManagerId());
        if (!exist) {
            throw new InvalidRequestException("No existe el manager con id " + hotel.getManagerId());
        }
        return hotelRepository.save(hotel);
    }

    public Hotel getHotelById(int id) {
        return hotelRepository.findById(id)
                .orElseThrow(() -> new HotelNotFoundException(id));
    }

    public List<Hotel> deleteHotelsByManagerId(int managerId) {
        List<Hotel> hotels = hotelRepository.findAllByManagerId(managerId);
        if (hotels.isEmpty()) {
            throw new InvalidRequestException("No hay hoteles para el manager con id " + managerId);
        }
        bookingAPI.deleteAllByManagerId(managerId);
        hotelRepository.deleteAll(hotels);
        return hotels;
    }

    public void deleteHotel(int id) {
        Hotel target = hotelRepository.findById(id)
                .orElseThrow(() -> new HotelNotFoundException(id));
        bookingAPI.deleteAllByHotelId(id);
        hotelRepository.delete(target);
    }

    public List<Room> getRoomsFromHotel(int hotelId, LocalDate start, LocalDate end) {
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
        return rooms;
    }

    public Room updateRoomAvailability(int hotelId, int roomId, boolean available) {
        Room targetRoom = roomRepository.findByIdAndHotelId(roomId, hotelId)
                .orElseThrow(() -> new InvalidRequestException("Habitación no encontrada"));
        targetRoom.setAvailable(available);
        return roomRepository.save(targetRoom);
    }

    public Room getRoomByIdFromHotel(int hotelId, int roomId) {
        return roomRepository.findByIdAndHotelId(roomId, hotelId)
                .orElseThrow(() -> new HotelNotFoundException(hotelId));
    }
}
