// RoomRepository.java
package com.uva.roomBooking.Repositories;

import com.uva.roomBooking.Models.Room;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomRepository extends JpaRepository<Room, Integer> {
}
