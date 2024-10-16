// RoomRepository.java
package com.uva.roomBooking.Repositories;

import com.uva.roomBooking.Models.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RoomRepository extends JpaRepository<Room, Integer> {
    List<Room> findByHotelId(Long hotelId);
}
