package com.sample.backend.timezones.error;

public class UserNotFoundException extends Exception {

    private static final long serialVersionUID = -5832747531900285474L;

    private static final String USER_NOT_FOUND_ERROR = "User Not Found Error";

    public UserNotFoundException() {
        super(USER_NOT_FOUND_ERROR);
    }

}
