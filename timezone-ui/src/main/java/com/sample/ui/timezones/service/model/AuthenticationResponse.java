package com.sample.ui.timezones.service.model;

import com.sample.ui.timezones.def.ApiResponseStatus;
import com.sample.ui.timezones.domain.UserDto;

public class AuthenticationResponse extends ApiResults {

    private final UserDto user;

    public AuthenticationResponse(ApiResponseStatus result, String message, UserDto user) {
        super(result, message);
        this.user = user;
    }

    public UserDto getUser() {
        return user;
    }

}
