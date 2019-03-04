package com.sample.ui.timezones.service.model;

import java.util.List;

import com.sample.ui.timezones.def.ApiResponseStatus;
import com.sample.ui.timezones.domain.TimezoneDto;

public class GetTimezonesResponse extends ApiResults {

    private final List<TimezoneDto> timezones;

    public GetTimezonesResponse(ApiResponseStatus result, String message, List<TimezoneDto> timezones) {
        super(result, message);
        this.timezones = timezones;
    }

    public List<TimezoneDto> getTimezones() {
        return timezones;
    }

}
