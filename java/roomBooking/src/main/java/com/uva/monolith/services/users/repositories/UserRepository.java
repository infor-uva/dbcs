package com.uva.api.services.users.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.uva.api.services.users.models.User;

public interface UserRepository extends JpaRepository<User, Integer> {
  Optional<User> findByEmail(String email);
}
