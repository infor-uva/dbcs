package com.uva.roomBooking.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.uva.roomBooking.Models.User;

public interface UserRepository extends JpaRepository<User, Integer> {

}
