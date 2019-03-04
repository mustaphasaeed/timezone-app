package com.sample.backend.timezones.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sample.backend.timezones.domain.User;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByUsername(String username);
}
