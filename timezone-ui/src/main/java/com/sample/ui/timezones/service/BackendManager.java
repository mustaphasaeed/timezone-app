package com.sample.ui.timezones.service;

import static com.sample.ui.timezones.util.SharedConstants.BACKEND_COMMUNICATION_ERROR;
import static com.sample.ui.timezones.util.SharedConstants.TIMEZONES_RETRIEVED_SUCCESSFULLY;
import static com.sample.ui.timezones.util.SharedConstants.TIMEZONE_ADDED_SUCCESSFULLY;
import static com.sample.ui.timezones.util.SharedConstants.TIMEZONE_DELETED_SUCCESSFULLY;
import static com.sample.ui.timezones.util.SharedConstants.USERS_RETRIEVED_SUCCESSFULLY;
import static com.sample.ui.timezones.util.SharedConstants.USER_AUTHENTICTED_SUCCESSFULLY;
import static com.sample.ui.timezones.util.SharedConstants.USER_DELETED_SUCCESSFULLY;
import static com.sample.ui.timezones.util.SharedConstants.USER_PASSWORD_UPDATED_SUCCESSFULLY;
import static com.sample.ui.timezones.util.SharedConstants.USER_PROFILE_UPDATED_SUCCESSFULLY;
import static com.sample.ui.timezones.util.SharedConstants.USER_REGISTERED_SUCCESSFULLY;
import static com.sample.ui.timezones.util.SharedConstants.USER_ROLE_UPDATED_SUCCESSFULLY;
import static com.sample.ui.timezones.util.SharedConstants.USER_STATUS_UPDATED_SUCCESSFULLY;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import com.sample.ui.timezones.def.ApiResponseStatus;
import com.sample.ui.timezones.def.UserRole;
import com.sample.ui.timezones.domain.TimezoneDto;
import com.sample.ui.timezones.domain.UserDto;
import com.sample.ui.timezones.service.model.AddTimezoneResponse;
import com.sample.ui.timezones.service.model.ApiResults;
import com.sample.ui.timezones.service.model.AuthenticationResponse;
import com.sample.ui.timezones.service.model.GetTimezonesResponse;
import com.sample.ui.timezones.service.model.GetUsersResponse;
import com.sample.ui.timezones.service.model.HttpApiResponse;
import com.sample.ui.timezones.util.JsonUtils;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;

@SpringComponent
@UIScope
public class BackendManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(BackendManager.class.getName());

    private static final String LOGIN_URL = "api/user/login";

    private static final String REGISTER_USER_URL = "api/user/register";

    private static final String LIST_USERS_URL = "api/user/listUsers";

    private static final String SET_USER_ROLE_URL = "api/user/setRole";

    private static final String DELETE_USER_URL = "api/user/deleteUser";

    private static final String CHANGE_USER_PASSWORD_URL = "api/user/resetPassword";

    private static final String SET_USER_STATUS_URL = "api/user/setUserStatus";

    private static final String UPDATE_USER_PROFILE_URL = "api/user/updateProfile";

    private static final String GET_TIMEZONES_URL = "api/timezone/listTimezones";

    private static final String DELETE_TIMEZONE_URL = "api/timezone/deleteTimezone";

    private static final String ADD_NEW_TIME_URL = "api/timezone/addTimezone";

    private static final String NAME_PARAM = "name";

    private static final String USERNAME_PARAM = "username";

    private static final String PASSWORD_PARAM = "password";

    private static final String USER_ROLE_PARAM = "role";

    private static final String USER_STATUS_PARAM = "status";

    private static final String USER_NEW_PASSWORD_PARAM = "newPassword";

    private static final String TIMEZONE_ID_PARAM = "timezoneId";

    private static final String TIMEZONE_CITY_PARAM = "city";

    private static final String TIMEZONE_NAME_PARAM = "name";

    private static final String TIMEZONE_OFFSET_PARAM = "offset";

    private static final String JSON_MESSAGE_KEY = "message";

    private static final String JSON_USER_KEY = "user";

    private static final String JSON_TIMEZONE_KEY = "timezone";

    private static final String JSON_TIMEZONES_LIST_KEY = "timezones";

    private static final String JSON_USERS_LIST_KEY = "users";

    private final HttpClientReader httpClientReader;

    @Autowired
    public BackendManager(HttpClientReader httpClientReader) {
        this.httpClientReader = httpClientReader;
    }

    public AuthenticationResponse loginUser(String username, String password) {
        try {
            HttpApiResponse httpApiResponse = httpClientReader.invokeApi(LOGIN_URL, null, username, password);
            if (httpApiResponse.getStatusCode() != HttpStatus.OK.value()) {
                return new AuthenticationResponse(ApiResponseStatus.FAIL,
                        extractResponseMessage(httpApiResponse.getContents()), null);
            } else {
                return new AuthenticationResponse(ApiResponseStatus.SUCCESS, USER_AUTHENTICTED_SUCCESSFULLY,
                        extractUserDetails(httpApiResponse.getContents()));
            }
        } catch (URISyntaxException | IOException e) {
            LOGGER.error(BACKEND_COMMUNICATION_ERROR, e);
            return new AuthenticationResponse(ApiResponseStatus.FAIL, e.getMessage(), null);
        }

    }

    public AuthenticationResponse registerUser(String name, String username, String password) {
        try {
            HashMap<String, String> params = new HashMap<>();
            params.put(NAME_PARAM, name);
            params.put(USERNAME_PARAM, username);
            params.put(PASSWORD_PARAM, password);
            HttpApiResponse httpApiResponse = httpClientReader.invokeApi(REGISTER_USER_URL, params, null, null);
            if (httpApiResponse.getStatusCode() != HttpStatus.OK.value()) {
                return new AuthenticationResponse(ApiResponseStatus.FAIL, httpApiResponse.getContents(), null);
            } else {
                return new AuthenticationResponse(ApiResponseStatus.SUCCESS, USER_REGISTERED_SUCCESSFULLY,
                        extractUserDetails(httpApiResponse.getContents()));
            }
        } catch (URISyntaxException | IOException e) {
            LOGGER.error(BACKEND_COMMUNICATION_ERROR, e);
            return new AuthenticationResponse(ApiResponseStatus.FAIL, e.getMessage(), null);
        }
    }

    public GetTimezonesResponse getUserTimezones(String timeZoneUser, String username, String password) {
        try {
            HashMap<String, String> params = new HashMap<>();
            params.put(USERNAME_PARAM, timeZoneUser);
            HttpApiResponse httpApiResponse = httpClientReader.invokeApi(GET_TIMEZONES_URL, params, username, password);
            if (httpApiResponse.getStatusCode() != HttpStatus.OK.value()) {
                return new GetTimezonesResponse(ApiResponseStatus.FAIL, httpApiResponse.getContents(), null);
            } else {
                return new GetTimezonesResponse(ApiResponseStatus.SUCCESS, TIMEZONES_RETRIEVED_SUCCESSFULLY,
                        extractTimeZones(httpApiResponse.getContents()));
            }
        } catch (URISyntaxException | IOException e) {
            LOGGER.error(BACKEND_COMMUNICATION_ERROR, e);
            return new GetTimezonesResponse(ApiResponseStatus.FAIL, e.getMessage(), null);
        }
    }

    public GetUsersResponse getAllUsers(String username, String password) {
        try {
            HttpApiResponse httpApiResponse = httpClientReader.invokeApi(LIST_USERS_URL, null, username, password);
            if (httpApiResponse.getStatusCode() != HttpStatus.OK.value()) {
                return new GetUsersResponse(ApiResponseStatus.FAIL, httpApiResponse.getContents(), null);
            } else {
                return new GetUsersResponse(ApiResponseStatus.SUCCESS, USERS_RETRIEVED_SUCCESSFULLY,
                        extractUsers(httpApiResponse.getContents()));
            }
        } catch (URISyntaxException | IOException e) {
            LOGGER.error(BACKEND_COMMUNICATION_ERROR, e);
            return new GetUsersResponse(ApiResponseStatus.FAIL, e.getMessage(), null);
        }
    }

    public AddTimezoneResponse addTimeZone(String city, String name, int offset, String timezoneUser, String username,
            String password) {
        try {
            HashMap<String, String> params = new HashMap<>();
            params.put(USERNAME_PARAM, timezoneUser);
            params.put(TIMEZONE_CITY_PARAM, city);
            params.put(TIMEZONE_NAME_PARAM, name);
            params.put(TIMEZONE_OFFSET_PARAM, String.valueOf(offset));
            HttpApiResponse httpApiResponse = httpClientReader.invokeApi(ADD_NEW_TIME_URL, params, username, password);
            if (httpApiResponse.getStatusCode() != HttpStatus.OK.value()) {
                return new AddTimezoneResponse(ApiResponseStatus.FAIL, httpApiResponse.getContents(), null);
            } else {
                return new AddTimezoneResponse(ApiResponseStatus.SUCCESS, TIMEZONE_ADDED_SUCCESSFULLY,
                        extractTimeZone(httpApiResponse.getContents()));
            }
        } catch (URISyntaxException | IOException e) {
            LOGGER.error(BACKEND_COMMUNICATION_ERROR, e);
            return new AddTimezoneResponse(ApiResponseStatus.FAIL, e.getMessage(), null);
        }
    }

    public ApiResults updateProfile(String name, String newPassword, String username, String password) {
        HashMap<String, String> params = new HashMap<>();
        params.put(NAME_PARAM, name);
        params.put(USER_NEW_PASSWORD_PARAM, newPassword);
        return callApiWithNoResp(UPDATE_USER_PROFILE_URL, params, username, password,
                USER_PROFILE_UPDATED_SUCCESSFULLY);
    }

    public ApiResults deleteTimezone(long timezoneId, String username, String password) {
        HashMap<String, String> params = new HashMap<>();
        params.put(TIMEZONE_ID_PARAM, String.valueOf(timezoneId));
        return callApiWithNoResp(DELETE_TIMEZONE_URL, params, username, password, TIMEZONE_DELETED_SUCCESSFULLY);
    }

    public ApiResults changeUserRole(String impactedUsername, UserRole newRole, String username, String password) {
        HashMap<String, String> params = new HashMap<>();
        params.put(USERNAME_PARAM, impactedUsername);
        params.put(USER_ROLE_PARAM, String.valueOf(newRole));
        return callApiWithNoResp(SET_USER_ROLE_URL, params, username, password, USER_ROLE_UPDATED_SUCCESSFULLY);
    }

    public ApiResults deleteUser(String impactedUsername, String username, String password) {
        HashMap<String, String> params = new HashMap<>();
        params.put(USERNAME_PARAM, String.valueOf(impactedUsername));
        return callApiWithNoResp(DELETE_USER_URL, params, username, password, USER_DELETED_SUCCESSFULLY);
    }

    public ApiResults setUserStatus(String impactedUsername, boolean activated, String username, String password) {
        HashMap<String, String> params = new HashMap<>();
        params.put(USERNAME_PARAM, impactedUsername);
        params.put(USER_STATUS_PARAM, String.valueOf(activated));
        return callApiWithNoResp(SET_USER_STATUS_URL, params, username, password, USER_STATUS_UPDATED_SUCCESSFULLY);
    }

    public ApiResults resetPassword(String impactedUser, String newPassword, String username, String password) {
        HashMap<String, String> params = new HashMap<>();
        params.put(USERNAME_PARAM, impactedUser);
        params.put(USER_NEW_PASSWORD_PARAM, newPassword);
        return callApiWithNoResp(CHANGE_USER_PASSWORD_URL, params, username, password,
                USER_PASSWORD_UPDATED_SUCCESSFULLY);
    }

    private ApiResults callApiWithNoResp(String url, HashMap<String, String> params, String username, String password,
            String sucessMessage) {
        try {
            HttpApiResponse httpApiResponse = httpClientReader.invokeApi(url, params, username, password);
            if (httpApiResponse.getStatusCode() != HttpStatus.OK.value()) {
                return new ApiResults(ApiResponseStatus.FAIL, httpApiResponse.getContents());
            } else {
                return new ApiResults(ApiResponseStatus.SUCCESS, sucessMessage);
            }
        } catch (URISyntaxException | IOException e) {
            LOGGER.error(BACKEND_COMMUNICATION_ERROR, e);
            return new ApiResults(ApiResponseStatus.FAIL, e.getMessage());
        }
    }

    private String extractResponseMessage(String contents) {
        Map<String, String> map = JsonUtils.jsonToMap(contents);
        return map.get(JSON_MESSAGE_KEY);
    }

    private TimezoneDto extractTimeZone(String contents) {
        Map<String, String> map = JsonUtils.jsonToMap(contents);
        return JsonUtils.jsonToObject(map.get(JSON_TIMEZONE_KEY), TimezoneDto.class);
    }

    private UserDto extractUserDetails(String contents) {
        Map<String, String> map = JsonUtils.jsonToMap(contents);
        return JsonUtils.jsonToObject(map.get(JSON_USER_KEY), UserDto.class);
    }

    private List<TimezoneDto> extractTimeZones(String contents) {
        Map<String, String> map = JsonUtils.jsonToMap(contents);
        return JsonUtils.jsonToList(map.get(JSON_TIMEZONES_LIST_KEY), TimezoneDto.class);
    }

    private List<UserDto> extractUsers(String contents) {
        Map<String, String> map = JsonUtils.jsonToMap(contents);
        return JsonUtils.jsonToList(map.get(JSON_USERS_LIST_KEY), UserDto.class);
    }

}
