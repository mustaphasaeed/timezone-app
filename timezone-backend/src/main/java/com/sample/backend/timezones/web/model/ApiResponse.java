package com.sample.backend.timezones.web.model;

import com.sample.backend.timezones.def.RequestStatus;

public class ApiResponse {

    private RequestStatus status;

    private String message;

    public ApiResponse(RequestStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    public RequestStatus getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

}
