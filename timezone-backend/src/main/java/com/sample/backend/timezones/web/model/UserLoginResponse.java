package com.sample.backend.timezones.web.model;

import com.sample.backend.timezones.def.RequestStatus;
import com.sample.backend.timezones.domain.User;

public class UserLoginResponse extends ApiResponse {

    private final User user;

    public UserLoginResponse(RequestStatus status, String message, User user) {
        super(status, message);
        this.user = user;
    }

    public User getUser() {
        return user;
    }

}
