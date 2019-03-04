package com.sample.backend.timezones.web.model;

import com.sample.backend.timezones.def.RequestStatus;
import com.sample.backend.timezones.domain.Timezone;

public class AddTimezoneResponse extends ApiResponse {

    private final Timezone timezone;

    public AddTimezoneResponse(RequestStatus status, String message, Timezone timezone) {
        super(status, message);
        this.timezone = timezone;
    }

    public Timezone getTimezone() {
        return timezone;
    }

}
