package com.sample.backend.timezones.service;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.sample.backend.timezones.def.UserRole;
import com.sample.backend.timezones.domain.User;
import com.sample.backend.timezones.error.UserAlreadyExisitException;
import com.sample.backend.timezones.error.UserNotFoundException;
import com.sample.backend.timezones.repository.TimezoneRepository;
import com.sample.backend.timezones.repository.UserRepository;

@Component
public class UserService {

    private final UserRepository userRepository;

    private final TimezoneRepository timezoneRepository;

    @Autowired
    public UserService(UserRepository userRepository, TimezoneRepository timezoneRepository) {
        this.userRepository = userRepository;
        this.timezoneRepository = timezoneRepository;
    }

    public User registerUser(String name, String username, String password) throws UserAlreadyExisitException {
        User user = userRepository.findByUsername(username);
        if (user != null)
            throw new UserAlreadyExisitException();
        user = new User(name, username, password, UserRole.USER);
        user = userRepository.save(user);
        user.setPassword(null);
        return user;
    }

    public void resetPassword(String username, String newPassword) throws UserNotFoundException {
        User user = userRepository.findByUsername(username);
        if (user == null)
            throw new UserNotFoundException();
        user.setPassword(newPassword);
        userRepository.save(user);
    }

    public void updateProfile(User user, String newName, String password) {
        if (!StringUtils.isEmpty(newName))
            user.setName(newName);
        if (!StringUtils.isEmpty(password))
            user.setPassword(password);
        userRepository.save(user);
    }

    public void setUserStatus(String username, boolean status) throws UserNotFoundException {
        User user = userRepository.findByUsername(username);
        if (user == null)
            throw new UserNotFoundException();
        user.setActive(status);
        userRepository.save(user);
    }

    public void setRole(String username, UserRole role) throws UserNotFoundException {
        User user = userRepository.findByUsername(username);
        if (user == null)
            throw new UserNotFoundException();
        user.setUserRole(role);
        userRepository.save(user);
    }

    @Transactional
    public void deleteUser(String username) throws UserNotFoundException {
        User user = userRepository.findByUsername(username);
        if (user == null)
            throw new UserNotFoundException();
        timezoneRepository.deleteByUser(user);
        userRepository.delete(user);
    }

    public List<User> listUsers() {
        List<User> result = userRepository.findAll();
        result.stream().forEach(user -> user.setPassword(null));
        return result;
    }

}
