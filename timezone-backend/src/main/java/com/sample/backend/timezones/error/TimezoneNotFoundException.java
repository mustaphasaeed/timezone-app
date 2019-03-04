package com.sample.backend.timezones.error;

public class TimezoneNotFoundException extends Exception {

    private static final long serialVersionUID = -5676951399274104917L;

    private static final String TIMEZONE_NOT_FOUND_ERROR = "Timezone Not Found Error";

    public TimezoneNotFoundException() {
        super(TIMEZONE_NOT_FOUND_ERROR);
    }

}
