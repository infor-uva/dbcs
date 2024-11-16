package com.uva.roomBooking.Repositories;

import com.uva.roomBooking.Models.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HotelRepository extends JpaRepository<Hotel, Integer> {

}
