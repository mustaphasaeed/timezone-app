package com.sample.backend.timezones.web.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.sample.backend.timezones.def.RequestStatus;
import com.sample.backend.timezones.def.UserRole;
import com.sample.backend.timezones.domain.User;
import com.sample.backend.timezones.error.UserAlreadyExisitException;
import com.sample.backend.timezones.error.UserNotFoundException;
import com.sample.backend.timezones.service.UserService;
import com.sample.backend.timezones.web.model.ApiResponse;
import com.sample.backend.timezones.web.model.ListUsersResponse;
import com.sample.backend.timezones.web.model.UserLoginResponse;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private static final String USER_REGISTRAION_SUCCESS = "User Registered Successfully";

    private static final String USER_LOGIN_SUCCESS = "User Logged in Successfully";

    private static final String PASSWORD_RESET_SUCCESSFULLY = "Password Reseted Successfully";

    private static final String PROFILE_UPDATED_SUCCESSFULLY = "User Profile Updated Successfully";

    private static final String USER_NOT_AUHTORIZED = "User is not authorized";

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    @ResponseBody
    public UserLoginResponse registerUser(@RequestParam(value = "name", required = true) String name,
            @RequestParam(value = "username", required = true) String username,
            @RequestParam(value = "password", required = true) String password) throws UserAlreadyExisitException {
        User user = userService.registerUser(name, username, password);
        return new UserLoginResponse(RequestStatus.SUCCESS, USER_REGISTRAION_SUCCESS, user);
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    @ResponseBody
    public UserLoginResponse loginUser() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        user.setPassword(null);
        return new UserLoginResponse(RequestStatus.SUCCESS, USER_LOGIN_SUCCESS, user);
    }

    @RequestMapping(value = "/resetPassword", method = RequestMethod.POST)
    @ResponseBody
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse resetPassword(@RequestParam(value = "username", required = true) String username,
            @RequestParam(value = "newPassword", required = true) String newPassword)
                    throws AccessDeniedException, UserNotFoundException {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (UserRole.USER.equals(user.getUserRole()) && !user.getUsername().equals(username))
            throw new AccessDeniedException(USER_NOT_AUHTORIZED);
        userService.resetPassword(username, newPassword);
        return new ApiResponse(RequestStatus.SUCCESS, PASSWORD_RESET_SUCCESSFULLY);
    }

    @RequestMapping(value = "/updateProfile", method = RequestMethod.POST)
    @ResponseBody
    public ApiResponse updateProfile(@RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "newPassword", required = false) String password) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        userService.updateProfile(user, name, password);
        return new ApiResponse(RequestStatus.SUCCESS, PROFILE_UPDATED_SUCCESSFULLY);
    }

    @RequestMapping(value = "/setUserStatus", method = RequestMethod.POST)
    @ResponseBody
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse setUserStatus(@RequestParam(value = "username", required = true) String username,
            @RequestParam(value = "status", required = true) boolean status) throws UserNotFoundException {
        userService.setUserStatus(username, status);
        return new ApiResponse(RequestStatus.SUCCESS, PROFILE_UPDATED_SUCCESSFULLY);
    }

    @RequestMapping(value = "/setRole", method = RequestMethod.POST)
    @ResponseBody
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse setRole(@RequestParam(value = "username", required = true) String username,
            @RequestParam(value = "role", required = true) UserRole role) throws UserNotFoundException {
        userService.setRole(username, role);
        return new ApiResponse(RequestStatus.SUCCESS, PROFILE_UPDATED_SUCCESSFULLY);
    }

    @RequestMapping(value = "/deleteUser", method = RequestMethod.POST)
    @ResponseBody
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse deleteUser(@RequestParam(value = "username", required = true) String username)
            throws UserNotFoundException {
        userService.deleteUser(username);
        return new ApiResponse(RequestStatus.SUCCESS, PROFILE_UPDATED_SUCCESSFULLY);
    }

    @RequestMapping(value = "/listUsers", method = RequestMethod.POST)
    @ResponseBody
    @PreAuthorize("hasRole('ADMIN')")
    public ListUsersResponse listUsers() {
        List<User> users = userService.listUsers();
        return new ListUsersResponse(RequestStatus.SUCCESS, PROFILE_UPDATED_SUCCESSFULLY, users);
    }

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleAppException(UserNotFoundException ex) {
        return ex.getMessage();
    }
    
    @ExceptionHandler(UserAlreadyExisitException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleAppException(UserAlreadyExisitException ex) {
        return ex.getMessage();
    }
    
    
}
