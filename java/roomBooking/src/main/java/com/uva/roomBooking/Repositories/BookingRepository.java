// BookingRepository.java
package com.uva.roomBooking.Repositories;

import com.uva.roomBooking.Models.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingRepository extends JpaRepository<Booking, Integer> {
}
