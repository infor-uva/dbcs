package com.uva.users.Repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.uva.users.Models.User;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByEmail(String email);
}
