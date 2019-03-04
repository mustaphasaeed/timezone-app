package com.sample.backend.timezones.web.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.sample.backend.timezones.def.RequestStatus;
import com.sample.backend.timezones.def.UserRole;
import com.sample.backend.timezones.domain.Timezone;
import com.sample.backend.timezones.domain.User;
import com.sample.backend.timezones.error.TimezoneNotFoundException;
import com.sample.backend.timezones.error.UserNotFoundException;
import com.sample.backend.timezones.service.TimezoneService;
import com.sample.backend.timezones.web.model.AddTimezoneResponse;
import com.sample.backend.timezones.web.model.ApiResponse;
import com.sample.backend.timezones.web.model.ListTimezonesReponse;

@RestController
@RequestMapping("/api/timezone")
public class TimezoneController {

    private static final String USER_NOT_AUHTORIZED = "User is not authorized";

    private static final String TIMEZONE_ADDED_SUCCESSFULLY = "Timezone Added Successfully";

    private static final String TIMEZONE_DELETED_SUCCESSFULLY = "Timezone Deleted Successfully";

    private static final String TIMEZONES_RETRIEVED_SUCCESSFULLY = "Timezones Retrieved Successfully";

    private final TimezoneService timezoneService;

    @Autowired
    public TimezoneController(TimezoneService timezoneService) {
        this.timezoneService = timezoneService;
    }

    @RequestMapping(value = "/addTimezone", method = RequestMethod.POST)
    @ResponseBody
    public AddTimezoneResponse addTimezone(@RequestParam(value = "username", required = true) String username,
            @RequestParam(value = "name", required = true) String name,
            @RequestParam(value = "city", required = true) String city,
            @RequestParam(value = "offset", required = true) int offset)
                    throws AccessDeniedException, UserNotFoundException {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (UserRole.USER.equals(user.getUserRole()) && !user.getUsername().equals(username))
            throw new AccessDeniedException(USER_NOT_AUHTORIZED);
        Timezone timezone = timezoneService.addTimeZone(username, name, city, offset);
        return new AddTimezoneResponse(RequestStatus.SUCCESS, TIMEZONE_ADDED_SUCCESSFULLY, timezone);
    }

    @RequestMapping(value = "/deleteTimezone", method = RequestMethod.POST)
    @ResponseBody
    public ApiResponse deleteTimezone(@RequestParam(value = "timezoneId", required = true) long timezoneId)
            throws AccessDeniedException, TimezoneNotFoundException {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User owner = timezoneService.getTimezoneOwner(timezoneId);
        if (UserRole.USER.equals(user.getUserRole()) && !user.getUsername().equals(owner.getUsername()))
            throw new AccessDeniedException(USER_NOT_AUHTORIZED);
        timezoneService.deleteTimeZone(timezoneId);
        return new ApiResponse(RequestStatus.SUCCESS, TIMEZONE_DELETED_SUCCESSFULLY);
    }

    @RequestMapping(value = "/listTimezones", method = RequestMethod.POST)
    @ResponseBody
    public ListTimezonesReponse listTimezones(@RequestParam(value = "username", required = false) String username)
            throws AccessDeniedException, UserNotFoundException {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (UserRole.USER.equals(user.getUserRole()) && StringUtils.isEmpty(username))
            throw new AccessDeniedException(USER_NOT_AUHTORIZED);
        if (UserRole.USER.equals(user.getUserRole()) && !user.getUsername().equals(username))
            throw new AccessDeniedException(USER_NOT_AUHTORIZED);
        List<Timezone> timezones = null;
        if (StringUtils.isEmpty(username))
            timezones = timezoneService.getAllTimeZones();
        else
            timezones = timezoneService.getUserTimezones(username);
        return new ListTimezonesReponse(RequestStatus.SUCCESS, TIMEZONES_RETRIEVED_SUCCESSFULLY, timezones);
    }

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleAppException(UserNotFoundException ex) {
        return ex.getMessage();
    }

    @ExceptionHandler(TimezoneNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleAppException(TimezoneNotFoundException ex) {
        return ex.getMessage();
    }

}
