package com.sample.ui.timezones.service.model;

import com.sample.ui.timezones.def.ApiResponseStatus;

public class ApiResults {

    private final ApiResponseStatus status;

    private final String message;

    public ApiResults(ApiResponseStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    public ApiResponseStatus getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

}
