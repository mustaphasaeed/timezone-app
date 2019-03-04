package com.sample.backend.test.timezone.helper;

import org.apache.http.client.config.AuthSchemes;
import org.apache.tomcat.util.codec.binary.Base64;

import com.sample.backend.timezones.def.UserRole;
import com.sample.backend.timezones.domain.Timezone;
import com.sample.backend.timezones.domain.User;
import com.sample.backend.timezones.repository.TimezoneRepository;
import com.sample.backend.timezones.repository.UserRepository;

public class UnitTestingHelper {

    public static final String NAME = "Name";
    
    public static final String NAME_2 = "Name2";

    public static final String USERNAME_1 = "Username1";

    public static final String USERNAME_2 = "Username2";
    
    public static final String USERNAME_3 = "Username3";

    public static final String PASSWORD = "pass";

    public static final String NEW_PASSWORD = "newPass";

    public static final String NEW_NAME = "newName";

    public static final String TZ_CITY = "City";

    public static final int TZ_OFFSET = 120;

    public static final String TZ_NAME_1 = "Timezone1";

    public static final String TZ_NAME_2 = "Timezone2";

    public static final String TZ_NAME_3 = "Timezone3";

    public static final long TZ_ID = 100;

    public static final long TZ_ID_2 = 101;

    public static final String LOGIN_URL = "/api/user/login";

    public static final String REGISTER_USER_URL = "/api/user/register";

    public static final String LIST_USERS_URL = "/api/user/listUsers";

    public static final String SET_USER_ROLE_URL = "/api/user/setRole";

    public static final String DELETE_USER_URL = "/api/user/deleteUser";

    public static final String CHANGE_USER_PASSWORD_URL = "/api/user/resetPassword";

    public static final String SET_USER_STATUS_URL = "/api/user/setUserStatus";

    public static final String UPDATE_USER_PROFILE_URL = "/api/user/updateProfile";

    public static final String GET_TIMEZONES_URL = "/api/timezone/listTimezones";

    public static final String DELETE_TIMEZONE_URL = "/api/timezone/deleteTimezone";

    public static final String ADD_NEW_TIME_URL = "/api/timezone/addTimezone";

    public static final String NAME_PARAM = "name";

    public static final String USERNAME_PARAM = "username";

    public static final String PASSWORD_PARAM = "password";

    public static final String USER_ROLE_PARAM = "role";

    public static final String USER_STATUS_PARAM = "status";

    public static final String USER_NEW_PASSWORD_PARAM = "newPassword";

    public static final String TIMEZONE_ID_PARAM = "timezoneId";

    public static final String TIMEZONE_CITY_PARAM = "city";

    public static final String TIMEZONE_NAME_PARAM = "name";

    public static final String TIMEZONE_OFFSET_PARAM = "offset";

    public static final String JSON_MESSAGE_KEY = "message";

    public static final String JSON_USER_KEY = "user";

    public static final String JSON_TIMEZONE_KEY = "timezone";

    public static final String JSON_TIMEZONES_LIST_KEY = "timezones";

    public static final String JSON_USERS_LIST_KEY = "users";

    public static User createSaveUser(UserRepository userRepository, String username, UserRole role) {
        return userRepository.save(new User(NAME, username, PASSWORD, role));
    }

    public static Timezone createSaveTimezone(TimezoneRepository timezoneRepository, String name, User user) {
        return timezoneRepository.save(new Timezone(name, TZ_CITY, TZ_OFFSET, user));
    }

    public static User createUser(String username, UserRole role) {
        return new User(NAME, username, PASSWORD, role);
    }

    public static Timezone createTimezone(String name, User user) {
        return new Timezone(name, TZ_CITY, TZ_OFFSET, user);
    }
    
    public static String getAuthenticationHeader(String username, String password) {
        String encodedAuth = Base64.encodeBase64String(
                new StringBuilder().append(username).append(":").append(password).toString().getBytes());
        return AuthSchemes.BASIC + " " + encodedAuth;
    }

}
