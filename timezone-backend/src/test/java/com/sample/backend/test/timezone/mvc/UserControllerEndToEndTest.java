package com.sample.backend.test.timezone.mvc;

import static com.sample.backend.test.timezone.helper.UnitTestingHelper.CHANGE_USER_PASSWORD_URL;
import static com.sample.backend.test.timezone.helper.UnitTestingHelper.DELETE_USER_URL;
import static com.sample.backend.test.timezone.helper.UnitTestingHelper.LIST_USERS_URL;
import static com.sample.backend.test.timezone.helper.UnitTestingHelper.LOGIN_URL;
import static com.sample.backend.test.timezone.helper.UnitTestingHelper.NAME;
import static com.sample.backend.test.timezone.helper.UnitTestingHelper.NAME_2;
import static com.sample.backend.test.timezone.helper.UnitTestingHelper.NAME_PARAM;
import static com.sample.backend.test.timezone.helper.UnitTestingHelper.NEW_PASSWORD;
import static com.sample.backend.test.timezone.helper.UnitTestingHelper.PASSWORD;
import static com.sample.backend.test.timezone.helper.UnitTestingHelper.PASSWORD_PARAM;
import static com.sample.backend.test.timezone.helper.UnitTestingHelper.REGISTER_USER_URL;
import static com.sample.backend.test.timezone.helper.UnitTestingHelper.SET_USER_ROLE_URL;
import static com.sample.backend.test.timezone.helper.UnitTestingHelper.SET_USER_STATUS_URL;
import static com.sample.backend.test.timezone.helper.UnitTestingHelper.UPDATE_USER_PROFILE_URL;
import static com.sample.backend.test.timezone.helper.UnitTestingHelper.USERNAME_1;
import static com.sample.backend.test.timezone.helper.UnitTestingHelper.USERNAME_2;
import static com.sample.backend.test.timezone.helper.UnitTestingHelper.USERNAME_3;
import static com.sample.backend.test.timezone.helper.UnitTestingHelper.USERNAME_PARAM;
import static com.sample.backend.test.timezone.helper.UnitTestingHelper.USER_NEW_PASSWORD_PARAM;
import static com.sample.backend.test.timezone.helper.UnitTestingHelper.USER_ROLE_PARAM;
import static com.sample.backend.test.timezone.helper.UnitTestingHelper.USER_STATUS_PARAM;
import static com.sample.backend.test.timezone.helper.UnitTestingHelper.createSaveUser;
import static com.sample.backend.test.timezone.helper.UnitTestingHelper.getAuthenticationHeader;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import javax.transaction.Transactional;

import org.apache.http.HttpHeaders;
import org.hamcrest.core.Is;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.sample.backend.config.SecurityConfiguration;
import com.sample.backend.test.config.DatabaseConfig;
import com.sample.backend.test.config.SpringConfig;
import com.sample.backend.timezones.def.RequestStatus;
import com.sample.backend.timezones.def.UserRole;
import com.sample.backend.timezones.domain.User;
import com.sample.backend.timezones.repository.UserRepository;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = { DatabaseConfig.class, SpringConfig.class, SecurityConfiguration.class })
@WebAppConfiguration
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Transactional
public class UserControllerEndToEndTest {

    @Autowired
    private WebApplicationContext _webApplicationContext;

    @Autowired
    private UserRepository userRepository;

    private MockMvc _mockMvc;

    @Before
    public void setUp() {
        createSaveUser(userRepository, USERNAME_1, UserRole.ADMIN);
        createSaveUser(userRepository, USERNAME_2, UserRole.USER);
        _mockMvc = MockMvcBuilders.webAppContextSetup(_webApplicationContext).apply(springSecurity()).build();
    }

    @Test
    public void testLogin() throws Exception {
        _mockMvc.perform(MockMvcRequestBuilders.post(LOGIN_URL)
                .header(HttpHeaders.AUTHORIZATION, getAuthenticationHeader(USERNAME_1, PASSWORD))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$status", Is.is(RequestStatus.SUCCESS.toString())))
                .andExpect(jsonPath("$user.username", Is.is(USERNAME_1)));
    }
    
    @Test
    public void testLogin_loginUnsuccessfull() throws Exception {
        _mockMvc.perform(MockMvcRequestBuilders.post(LOGIN_URL)
                .header(HttpHeaders.AUTHORIZATION, getAuthenticationHeader(USERNAME_3, PASSWORD))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }
    
    @Test
    public void testResgisterUser() throws Exception {
        _mockMvc.perform(MockMvcRequestBuilders.post(REGISTER_USER_URL)
                .param(NAME_PARAM, NAME)
                .param(USERNAME_PARAM, USERNAME_3)
                .param(PASSWORD_PARAM, PASSWORD)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$status", Is.is(RequestStatus.SUCCESS.toString())))
                .andExpect(jsonPath("$user.username", Is.is(USERNAME_3)));
        
        User user = userRepository.findByUsername(USERNAME_3);
        assertNotNull(user);
        assertEquals(NAME, user.getName());
        assertEquals(USERNAME_3, user.getUsername());
    }
    
    @Test
    public void testResgisterUser_userAlreadyExist() throws Exception {
        _mockMvc.perform(MockMvcRequestBuilders.post(REGISTER_USER_URL)
                .param(NAME_PARAM, NAME)
                .param(USERNAME_PARAM, USERNAME_1)
                .param(PASSWORD_PARAM, PASSWORD)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(equalTo("Username Already Exist")));
    }
    
    @Test
    public void testResetPassword() throws Exception {
        _mockMvc.perform(MockMvcRequestBuilders.post(CHANGE_USER_PASSWORD_URL)
                .param(USERNAME_PARAM, USERNAME_2)
                .param(USER_NEW_PASSWORD_PARAM, NEW_PASSWORD)
                .header(HttpHeaders.AUTHORIZATION, getAuthenticationHeader(USERNAME_1, PASSWORD))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$status", Is.is(RequestStatus.SUCCESS.toString())));
        
        User user = userRepository.findByUsername(USERNAME_2);
        assertNotNull(user);
        assertEquals(NEW_PASSWORD, user.getPassword());
    }
    
    @Test
    public void tsetResetPassword_notAuthorized() throws Exception {
        _mockMvc.perform(MockMvcRequestBuilders.post(CHANGE_USER_PASSWORD_URL)
                .param(USERNAME_PARAM, USERNAME_2)
                .param(USER_NEW_PASSWORD_PARAM, NEW_PASSWORD)
                .header(HttpHeaders.AUTHORIZATION, getAuthenticationHeader(USERNAME_2, PASSWORD))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }
    
    @Test
    public void testResetPassword_userNotFound() throws Exception {
        _mockMvc.perform(MockMvcRequestBuilders.post(CHANGE_USER_PASSWORD_URL)
                .param(USERNAME_PARAM, USERNAME_3)
                .param(USER_NEW_PASSWORD_PARAM, NEW_PASSWORD)
                .header(HttpHeaders.AUTHORIZATION, getAuthenticationHeader(USERNAME_1, PASSWORD))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string(equalTo("User Not Found Error")));
    }
    
    @Test
    public void testSetRole() throws Exception {
        _mockMvc.perform(MockMvcRequestBuilders.post(SET_USER_ROLE_URL)
                .param(USERNAME_PARAM, USERNAME_2)
                .param(USER_ROLE_PARAM, UserRole.ADMIN.toString())
                .header(HttpHeaders.AUTHORIZATION, getAuthenticationHeader(USERNAME_1, PASSWORD))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$status", Is.is(RequestStatus.SUCCESS.toString())));
        
        User user = userRepository.findByUsername(USERNAME_2);
        assertNotNull(user);
        assertEquals(UserRole.ADMIN, user.getUserRole());
    }
    
    @Test
    public void testSetRole_notAuthorized() throws Exception {
        _mockMvc.perform(MockMvcRequestBuilders.post(SET_USER_ROLE_URL)
                .param(USERNAME_PARAM, USERNAME_2)
                .param(USER_ROLE_PARAM, UserRole.ADMIN.toString())
                .header(HttpHeaders.AUTHORIZATION, getAuthenticationHeader(USERNAME_2, PASSWORD))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }
    
    @Test
    public void testSetRole_userNotFound() throws Exception {
        _mockMvc.perform(MockMvcRequestBuilders.post(SET_USER_ROLE_URL)
                .param(USERNAME_PARAM, USERNAME_3)
                .param(USER_ROLE_PARAM, UserRole.ADMIN.toString())
                .header(HttpHeaders.AUTHORIZATION, getAuthenticationHeader(USERNAME_1, PASSWORD))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string(equalTo("User Not Found Error")));
    }
    
    @Test
    public void testSetUserStatus() throws Exception {
        _mockMvc.perform(MockMvcRequestBuilders.post(SET_USER_STATUS_URL)
                .param(USERNAME_PARAM, USERNAME_2)
                .param(USER_STATUS_PARAM, String.valueOf(false))
                .header(HttpHeaders.AUTHORIZATION, getAuthenticationHeader(USERNAME_1, PASSWORD))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$status", Is.is(RequestStatus.SUCCESS.toString())));
        
        User user = userRepository.findByUsername(USERNAME_2);
        assertNotNull(user);
        assertEquals(false, user.getActive());
    }
    
    @Test
    public void testSetUserStatus_notAuthorized() throws Exception {
        _mockMvc.perform(MockMvcRequestBuilders.post(SET_USER_STATUS_URL)
                .param(USERNAME_PARAM, USERNAME_2)
                .param(USER_STATUS_PARAM, String.valueOf(false))
                .header(HttpHeaders.AUTHORIZATION, getAuthenticationHeader(USERNAME_2, PASSWORD))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }
    
    @Test
    public void testSetUserStatus_userNotFound() throws Exception {
        _mockMvc.perform(MockMvcRequestBuilders.post(SET_USER_STATUS_URL)
                .param(USERNAME_PARAM, USERNAME_3)
                .param(USER_STATUS_PARAM, String.valueOf(false))
                .header(HttpHeaders.AUTHORIZATION, getAuthenticationHeader(USERNAME_1, PASSWORD))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string(equalTo("User Not Found Error")));
    }
    
    @Test
    public void testUpdateProfile() throws Exception {
        _mockMvc.perform(MockMvcRequestBuilders.post(UPDATE_USER_PROFILE_URL)
                .param(NAME_PARAM, NAME_2)
                .param(USER_NEW_PASSWORD_PARAM, NEW_PASSWORD)
                .header(HttpHeaders.AUTHORIZATION, getAuthenticationHeader(USERNAME_1, PASSWORD))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$status", Is.is(RequestStatus.SUCCESS.toString())));
        
        User user = userRepository.findByUsername(USERNAME_1);
        assertNotNull(user);
        assertEquals(NAME_2, user.getName());
        assertEquals(NEW_PASSWORD, user.getPassword());
    }
    
    @Test
    public void testUpdateProfile_notAuthorized() throws Exception {
        _mockMvc.perform(MockMvcRequestBuilders.post(UPDATE_USER_PROFILE_URL)
                .param(NAME_PARAM, NAME_2)
                .param(USER_NEW_PASSWORD_PARAM, NEW_PASSWORD)
                .header(HttpHeaders.AUTHORIZATION, getAuthenticationHeader(USERNAME_3, PASSWORD))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }
    
    @Test
    public void testDeleteUser() throws Exception {
        _mockMvc.perform(MockMvcRequestBuilders.post(DELETE_USER_URL)
                .param(USERNAME_PARAM, USERNAME_2)
                .header(HttpHeaders.AUTHORIZATION, getAuthenticationHeader(USERNAME_1, PASSWORD))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$status", Is.is(RequestStatus.SUCCESS.toString())));
        
        User user = userRepository.findByUsername(USERNAME_2);
        assertNull(user);
    }
    
    @Test
    public void testDeleteUser_notAuthorized() throws Exception {
        _mockMvc.perform(MockMvcRequestBuilders.post(DELETE_USER_URL)
                .param(USERNAME_PARAM, USERNAME_1)
                .header(HttpHeaders.AUTHORIZATION, getAuthenticationHeader(USERNAME_2, PASSWORD))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }
    
    @Test
    public void testDeleteUser_userNotFound() throws Exception {
        _mockMvc.perform(MockMvcRequestBuilders.post(DELETE_USER_URL)
                .param(USERNAME_PARAM, USERNAME_3)
                .header(HttpHeaders.AUTHORIZATION, getAuthenticationHeader(USERNAME_1, PASSWORD))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string(equalTo("User Not Found Error")));
    }
    
    
    @Test
    public void testListAllUser() throws Exception {
        _mockMvc.perform(MockMvcRequestBuilders.post(LIST_USERS_URL)
                .header(HttpHeaders.AUTHORIZATION, getAuthenticationHeader(USERNAME_1, PASSWORD))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$status", Is.is(RequestStatus.SUCCESS.toString())))
                .andExpect(jsonPath("$users", hasSize(2)))
                .andExpect(jsonPath("$users[0].username", Is.is(USERNAME_1)))
                .andExpect(jsonPath("$users[1].username", Is.is(USERNAME_2)));
    }
    
    @Test
    public void testListAllUser_notAuthorized() throws Exception {
        _mockMvc.perform(MockMvcRequestBuilders.post(DELETE_USER_URL)
                .param(USERNAME_PARAM, USERNAME_1)
                .header(HttpHeaders.AUTHORIZATION, getAuthenticationHeader(USERNAME_2, PASSWORD))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

}
