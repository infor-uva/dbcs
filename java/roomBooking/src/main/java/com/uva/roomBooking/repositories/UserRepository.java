package com.uva.roomBooking.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.uva.roomBooking.models.User;

public interface UserRepository extends JpaRepository<User, Integer> {
  Optional<User> findByEmail(String email);
}
