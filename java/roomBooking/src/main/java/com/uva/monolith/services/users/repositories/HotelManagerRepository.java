package com.uva.api.services.users.repositories;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

import com.uva.api.services.users.models.HotelManager;

public interface HotelManagerRepository extends JpaRepository<HotelManager, Integer> {
  Optional<HotelManager> findByEmail(String email);
}