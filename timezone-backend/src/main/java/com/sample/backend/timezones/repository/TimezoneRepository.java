package com.sample.backend.timezones.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sample.backend.timezones.domain.Timezone;
import com.sample.backend.timezones.domain.User;

public interface TimezoneRepository extends JpaRepository<Timezone, Long> {

    List<Timezone> findAllByUser(User user);

    Timezone findById(long id);

    void deleteByUser(User user);

}
