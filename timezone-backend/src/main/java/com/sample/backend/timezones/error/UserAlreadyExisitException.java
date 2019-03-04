package com.sample.backend.timezones.error;

public class UserAlreadyExisitException extends Exception {

    private static final long serialVersionUID = -5832747531900285474L;

    private static final String USER_EXIST_ERROR = "Username Already Exist";

    public UserAlreadyExisitException() {
        super(USER_EXIST_ERROR);
    }

}
