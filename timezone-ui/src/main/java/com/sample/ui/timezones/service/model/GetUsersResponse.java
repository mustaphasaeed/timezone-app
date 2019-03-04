package com.sample.ui.timezones.service.model;

import java.util.List;

import com.sample.ui.timezones.def.ApiResponseStatus;
import com.sample.ui.timezones.domain.UserDto;

public class GetUsersResponse extends ApiResults {

    private final List<UserDto> users;

    public GetUsersResponse(ApiResponseStatus result, String message, List<UserDto> users) {
        super(result, message);
        this.users = users;
    }

    public List<UserDto> getUsers() {
        return users;
    }

}
