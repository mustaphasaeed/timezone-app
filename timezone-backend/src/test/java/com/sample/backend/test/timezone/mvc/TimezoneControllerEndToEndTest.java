package com.sample.backend.test.timezone.mvc;

import static com.sample.backend.test.timezone.helper.UnitTestingHelper.ADD_NEW_TIME_URL;
import static com.sample.backend.test.timezone.helper.UnitTestingHelper.DELETE_TIMEZONE_URL;
import static com.sample.backend.test.timezone.helper.UnitTestingHelper.GET_TIMEZONES_URL;
import static com.sample.backend.test.timezone.helper.UnitTestingHelper.PASSWORD;
import static com.sample.backend.test.timezone.helper.UnitTestingHelper.TIMEZONE_CITY_PARAM;
import static com.sample.backend.test.timezone.helper.UnitTestingHelper.TIMEZONE_ID_PARAM;
import static com.sample.backend.test.timezone.helper.UnitTestingHelper.TIMEZONE_NAME_PARAM;
import static com.sample.backend.test.timezone.helper.UnitTestingHelper.TIMEZONE_OFFSET_PARAM;
import static com.sample.backend.test.timezone.helper.UnitTestingHelper.TZ_CITY;
import static com.sample.backend.test.timezone.helper.UnitTestingHelper.TZ_ID;
import static com.sample.backend.test.timezone.helper.UnitTestingHelper.TZ_NAME_1;
import static com.sample.backend.test.timezone.helper.UnitTestingHelper.TZ_NAME_2;
import static com.sample.backend.test.timezone.helper.UnitTestingHelper.TZ_OFFSET;
import static com.sample.backend.test.timezone.helper.UnitTestingHelper.USERNAME_1;
import static com.sample.backend.test.timezone.helper.UnitTestingHelper.USERNAME_2;
import static com.sample.backend.test.timezone.helper.UnitTestingHelper.USERNAME_3;
import static com.sample.backend.test.timezone.helper.UnitTestingHelper.USERNAME_PARAM;
import static com.sample.backend.test.timezone.helper.UnitTestingHelper.createSaveUser;
import static com.sample.backend.test.timezone.helper.UnitTestingHelper.getAuthenticationHeader;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
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
import com.sample.backend.timezones.domain.Timezone;
import com.sample.backend.timezones.repository.TimezoneRepository;
import com.sample.backend.timezones.repository.UserRepository;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = { DatabaseConfig.class, SpringConfig.class, SecurityConfiguration.class })
@WebAppConfiguration
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Transactional
public class TimezoneControllerEndToEndTest {

    @Autowired
    private WebApplicationContext _webApplicationContext;

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private TimezoneRepository timezoneRepository;

    private MockMvc _mockMvc;

    @Before
    public void setUp() {
        createSaveUser(userRepository, USERNAME_1, UserRole.ADMIN);
        createSaveUser(userRepository, USERNAME_2, UserRole.USER);
        _mockMvc = MockMvcBuilders.webAppContextSetup(_webApplicationContext).apply(springSecurity()).build();
    }
    
    @Test
    public void testAddTimeZone() throws Exception{
        _mockMvc.perform(MockMvcRequestBuilders.post(ADD_NEW_TIME_URL)
                .param(USERNAME_PARAM, USERNAME_2)
                .param(TIMEZONE_CITY_PARAM, TZ_CITY)
                .param(TIMEZONE_NAME_PARAM, TZ_NAME_1)
                .param(TIMEZONE_OFFSET_PARAM, String.valueOf(TZ_OFFSET))
                .header(HttpHeaders.AUTHORIZATION, getAuthenticationHeader(USERNAME_1, PASSWORD))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$status", Is.is(RequestStatus.SUCCESS.toString())))
                .andExpect(jsonPath("$timezone.name", Is.is(TZ_NAME_1)))
                .andExpect(jsonPath("$timezone.city", Is.is(TZ_CITY)))
                .andExpect(jsonPath("$timezone.offset", Is.is(TZ_OFFSET)));
        
        Timezone timezone = timezoneRepository.findById(1);
        assertNotNull(timezone);
        assertEquals(TZ_NAME_1, timezone.getName());
        assertEquals(TZ_CITY, timezone.getCity());
        assertEquals(TZ_OFFSET, timezone.getOffset());
        assertEquals(USERNAME_2, timezone.getUser().getUsername());
    }
    
    @Test
    public void testAddTimeZone_nonAdmin() throws Exception{
        _mockMvc.perform(MockMvcRequestBuilders.post(ADD_NEW_TIME_URL)
                .param(USERNAME_PARAM, USERNAME_2)
                .param(TIMEZONE_CITY_PARAM, TZ_CITY)
                .param(TIMEZONE_NAME_PARAM, TZ_NAME_1)
                .param(TIMEZONE_OFFSET_PARAM, String.valueOf(TZ_OFFSET))
                .header(HttpHeaders.AUTHORIZATION, getAuthenticationHeader(USERNAME_2, PASSWORD))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$status", Is.is(RequestStatus.SUCCESS.toString())))
                .andExpect(jsonPath("$timezone.name", Is.is(TZ_NAME_1)))
                .andExpect(jsonPath("$timezone.city", Is.is(TZ_CITY)))
                .andExpect(jsonPath("$timezone.offset", Is.is(TZ_OFFSET)));
        
        Timezone timezone = timezoneRepository.findById(1);
        assertNotNull(timezone);
        assertEquals(TZ_NAME_1, timezone.getName());
        assertEquals(TZ_CITY, timezone.getCity());
        assertEquals(TZ_OFFSET, timezone.getOffset());
        assertEquals(USERNAME_2, timezone.getUser().getUsername());
    }
    
    @Test
    public void testAddTimeZone_notAllowed() throws Exception{
        _mockMvc.perform(MockMvcRequestBuilders.post(ADD_NEW_TIME_URL)
                .param(USERNAME_PARAM, USERNAME_1)
                .param(TIMEZONE_CITY_PARAM, TZ_CITY)
                .param(TIMEZONE_NAME_PARAM, TZ_NAME_1)
                .param(TIMEZONE_OFFSET_PARAM, String.valueOf(TZ_OFFSET))
                .header(HttpHeaders.AUTHORIZATION, getAuthenticationHeader(USERNAME_2, PASSWORD))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }
    
    @Test
    public void testAddTimeZone_notAuthorized() throws Exception{
        _mockMvc.perform(MockMvcRequestBuilders.post(ADD_NEW_TIME_URL)
                .param(USERNAME_PARAM, USERNAME_1)
                .param(TIMEZONE_CITY_PARAM, TZ_CITY)
                .param(TIMEZONE_NAME_PARAM, TZ_NAME_1)
                .param(TIMEZONE_OFFSET_PARAM, String.valueOf(TZ_OFFSET))
                .header(HttpHeaders.AUTHORIZATION, getAuthenticationHeader(USERNAME_3, PASSWORD))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }
    
    @Test
    public void testDeleteTimezone() throws Exception{
        Timezone timezone = timezoneRepository
                .save(new Timezone(TZ_NAME_1, TZ_CITY, TZ_OFFSET, userRepository.findByUsername(USERNAME_2)));
        
        _mockMvc.perform(MockMvcRequestBuilders.post(DELETE_TIMEZONE_URL)
                .param(TIMEZONE_ID_PARAM, String.valueOf(timezone.getId()))
                .header(HttpHeaders.AUTHORIZATION, getAuthenticationHeader(USERNAME_1, PASSWORD))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$status", Is.is(RequestStatus.SUCCESS.toString())));
        
        timezone = timezoneRepository.findById(timezone.getId());
        assertNull(timezone);
    }
    
    @Test
    public void testDeleteTimezone_nonAdmin() throws Exception{
        Timezone timezone = timezoneRepository
                .save(new Timezone(TZ_NAME_1, TZ_CITY, TZ_OFFSET, userRepository.findByUsername(USERNAME_2)));
        
        _mockMvc.perform(MockMvcRequestBuilders.post(DELETE_TIMEZONE_URL)
                .param(TIMEZONE_ID_PARAM, String.valueOf(timezone.getId()))
                .header(HttpHeaders.AUTHORIZATION, getAuthenticationHeader(USERNAME_2, PASSWORD))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$status", Is.is(RequestStatus.SUCCESS.toString())));
        
        timezone = timezoneRepository.findById(timezone.getId());
        assertNull(timezone);
    }
    
    @Test
    public void testDeleteTimezone_notAllowed() throws Exception{
        Timezone timezone = timezoneRepository
                .save(new Timezone(TZ_NAME_1, TZ_CITY, TZ_OFFSET, userRepository.findByUsername(USERNAME_1)));
        
        _mockMvc.perform(MockMvcRequestBuilders.post(DELETE_TIMEZONE_URL)
                .param(TIMEZONE_ID_PARAM, String.valueOf(timezone.getId()))
                .header(HttpHeaders.AUTHORIZATION, getAuthenticationHeader(USERNAME_2, PASSWORD))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }
    
    @Test
    public void testDeleteTimezone_notFound() throws Exception{
        _mockMvc.perform(MockMvcRequestBuilders.post(DELETE_TIMEZONE_URL)
                .param(TIMEZONE_ID_PARAM, String.valueOf(TZ_ID))
                .header(HttpHeaders.AUTHORIZATION, getAuthenticationHeader(USERNAME_1, PASSWORD))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
    
    @Test
    public void testDeleteTimezone_notAuthorized() throws Exception{
        _mockMvc.perform(MockMvcRequestBuilders.post(DELETE_TIMEZONE_URL)
                .param(TIMEZONE_ID_PARAM, String.valueOf(TZ_ID))
                .header(HttpHeaders.AUTHORIZATION, getAuthenticationHeader(USERNAME_3, PASSWORD))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }
    
    @Test
    public void testListTimezone() throws Exception{
        timezoneRepository.save(new Timezone(TZ_NAME_1, TZ_CITY, TZ_OFFSET, userRepository.findByUsername(USERNAME_1)));
        timezoneRepository.save(new Timezone(TZ_NAME_2, TZ_CITY, TZ_OFFSET, userRepository.findByUsername(USERNAME_2)));
        
        _mockMvc.perform(MockMvcRequestBuilders.post(GET_TIMEZONES_URL)
                .param(USERNAME_PARAM, USERNAME_2)
                .header(HttpHeaders.AUTHORIZATION, getAuthenticationHeader(USERNAME_2, PASSWORD))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$status", Is.is(RequestStatus.SUCCESS.toString())))
                .andExpect(jsonPath("$timezones", hasSize(1)))
                .andExpect(jsonPath("$timezones[0].name", Is.is(TZ_NAME_2)));
    }
    
    @Test
    public void testListTimezone_byAdmin() throws Exception{
        timezoneRepository.save(new Timezone(TZ_NAME_1, TZ_CITY, TZ_OFFSET, userRepository.findByUsername(USERNAME_1)));
        timezoneRepository.save(new Timezone(TZ_NAME_2, TZ_CITY, TZ_OFFSET, userRepository.findByUsername(USERNAME_2)));
        
        _mockMvc.perform(MockMvcRequestBuilders.post(GET_TIMEZONES_URL)
                .header(HttpHeaders.AUTHORIZATION, getAuthenticationHeader(USERNAME_1, PASSWORD))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$status", Is.is(RequestStatus.SUCCESS.toString())))
                .andExpect(jsonPath("$timezones", hasSize(2)))
                .andExpect(jsonPath("$timezones[0].name", Is.is(TZ_NAME_1)))
                .andExpect(jsonPath("$timezones[1].name", Is.is(TZ_NAME_2)));
    }
    
    @Test
    public void testListTimezone_notAllowedAllTimezones() throws Exception{
        timezoneRepository.save(new Timezone(TZ_NAME_1, TZ_CITY, TZ_OFFSET, userRepository.findByUsername(USERNAME_1)));
        timezoneRepository.save(new Timezone(TZ_NAME_2, TZ_CITY, TZ_OFFSET, userRepository.findByUsername(USERNAME_2)));
        
        _mockMvc.perform(MockMvcRequestBuilders.post(GET_TIMEZONES_URL)
                .header(HttpHeaders.AUTHORIZATION, getAuthenticationHeader(USERNAME_2, PASSWORD))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }
    
    @Test
    public void testListTimezone_notAllowed() throws Exception{
        timezoneRepository.save(new Timezone(TZ_NAME_1, TZ_CITY, TZ_OFFSET, userRepository.findByUsername(USERNAME_1)));
        timezoneRepository.save(new Timezone(TZ_NAME_2, TZ_CITY, TZ_OFFSET, userRepository.findByUsername(USERNAME_2)));
        
        _mockMvc.perform(MockMvcRequestBuilders.post(GET_TIMEZONES_URL)
                .param(USERNAME_PARAM, USERNAME_1)
                .header(HttpHeaders.AUTHORIZATION, getAuthenticationHeader(USERNAME_2, PASSWORD))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }
    
    @Test
    public void testListTimezone_notAuthorized() throws Exception{
        timezoneRepository.save(new Timezone(TZ_NAME_1, TZ_CITY, TZ_OFFSET, userRepository.findByUsername(USERNAME_1)));
        timezoneRepository.save(new Timezone(TZ_NAME_2, TZ_CITY, TZ_OFFSET, userRepository.findByUsername(USERNAME_2)));
        
        _mockMvc.perform(MockMvcRequestBuilders.post(GET_TIMEZONES_URL)
                .param(USERNAME_PARAM, USERNAME_1)
                .header(HttpHeaders.AUTHORIZATION, getAuthenticationHeader(USERNAME_3, PASSWORD))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }
    
}
