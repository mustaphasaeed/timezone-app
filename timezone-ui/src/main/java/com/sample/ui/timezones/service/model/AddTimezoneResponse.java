package com.sample.ui.timezones.service.model;

import com.sample.ui.timezones.def.ApiResponseStatus;
import com.sample.ui.timezones.domain.TimezoneDto;

public class AddTimezoneResponse extends ApiResults {

    private final TimezoneDto timezone;

    public AddTimezoneResponse(ApiResponseStatus result, String message, TimezoneDto timezoneDto) {
        super(result, message);
        this.timezone = timezoneDto;
    }

    public TimezoneDto getTimezone() {
        return timezone;
    }

}
