package com.uva.hotelService.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.uva.hotelService.Models.Room;
import java.util.List;
import java.util.Optional;

public interface RoomRepository extends JpaRepository<Room, Integer> {

    Optional<Room> findByIdAndHotelId(int id, int hotelId);

    List<Room> findAllByHotelId(int hotelId);

    List<Room> findAllByHotelIdAndAvailable(int hotelId, boolean available);
}
