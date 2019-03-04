package com.sample.backend.timezones.web.model;

import java.util.List;

import com.sample.backend.timezones.def.RequestStatus;
import com.sample.backend.timezones.domain.Timezone;

public class ListTimezonesReponse extends ApiResponse {

    private final List<Timezone> timezones;

    public ListTimezonesReponse(RequestStatus status, String message, List<Timezone> timezones) {
        super(status, message);
        this.timezones = timezones;
    }

    public List<Timezone> getTimezones() {
        return timezones;
    }

}
