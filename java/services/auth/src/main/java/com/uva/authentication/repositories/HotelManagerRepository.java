package com.uva.authentication.repositories;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

import com.uva.authentication.models.remote.HotelManager;
import com.uva.authentication.models.remote.User;

public interface HotelManagerRepository extends JpaRepository<User, Integer> {
  Optional<HotelManager> findByEmail(String email);
}