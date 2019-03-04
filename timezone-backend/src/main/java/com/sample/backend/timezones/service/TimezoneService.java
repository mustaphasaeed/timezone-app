package com.sample.backend.timezones.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sample.backend.timezones.domain.Timezone;
import com.sample.backend.timezones.domain.User;
import com.sample.backend.timezones.error.TimezoneNotFoundException;
import com.sample.backend.timezones.error.UserNotFoundException;
import com.sample.backend.timezones.repository.TimezoneRepository;
import com.sample.backend.timezones.repository.UserRepository;

@Component
public class TimezoneService {

    private final UserRepository userRepository;

    private final TimezoneRepository timezoneRepository;

    @Autowired
    public TimezoneService(UserRepository userRepository, TimezoneRepository timezoneRepository) {
        this.userRepository = userRepository;
        this.timezoneRepository = timezoneRepository;
    }

    public Timezone addTimeZone(String username, String name, String city, int offset) throws UserNotFoundException {
        User user = userRepository.findByUsername(username);
        if (user == null)
            throw new UserNotFoundException();
        Timezone timezone = new Timezone(name, city, offset, user);
        return timezoneRepository.save(timezone);
    }

    public User getTimezoneOwner(long timezoneId) throws TimezoneNotFoundException {
        Timezone timezone = timezoneRepository.findById(timezoneId);
        if (timezone == null)
            throw new TimezoneNotFoundException();
        return timezone.getUser();
    }

    public void deleteTimeZone(long timezoneId) throws TimezoneNotFoundException {
        Timezone timezone = timezoneRepository.findById(timezoneId);
        if (timezone == null)
            throw new TimezoneNotFoundException();
        timezoneRepository.delete(timezone);
    }

    public List<Timezone> getUserTimezones(String username) throws UserNotFoundException {
        User user = userRepository.findByUsername(username);
        if (user == null)
            throw new UserNotFoundException();
        return timezoneRepository.findAllByUser(user);
    }

    public List<Timezone> getAllTimeZones() {
        return timezoneRepository.findAll();
    }

}
