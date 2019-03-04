package com.sample.backend.timezones.web.model;

import java.util.List;

import com.sample.backend.timezones.def.RequestStatus;
import com.sample.backend.timezones.domain.User;

public class ListUsersResponse extends ApiResponse {

    private final List<User> users;

    public ListUsersResponse(RequestStatus status, String message, List<User> users) {
        super(status, message);
        this.users = users;
    }

    public List<User> getUsers() {
        return users;
    }
}
