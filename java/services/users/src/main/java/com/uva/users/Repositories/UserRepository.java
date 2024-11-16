package com.uva.users.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.uva.users.Models.User;

public interface UserRepository extends JpaRepository<User, Integer> {

}
