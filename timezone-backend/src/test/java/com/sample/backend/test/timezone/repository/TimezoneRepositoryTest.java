package com.sample.backend.test.timezone.repository;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;

import java.util.List;

import javax.transaction.Transactional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.sample.backend.test.config.DatabaseConfig;
import com.sample.backend.test.config.SpringConfig;
import com.sample.backend.test.timezone.helper.UnitTestingHelper;
import com.sample.backend.timezones.def.UserRole;
import com.sample.backend.timezones.domain.Timezone;
import com.sample.backend.timezones.domain.User;
import com.sample.backend.timezones.repository.TimezoneRepository;
import com.sample.backend.timezones.repository.UserRepository;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = { DatabaseConfig.class, SpringConfig.class })
@WebAppConfiguration
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Transactional
public class TimezoneRepositoryTest {

    @Autowired
    private TimezoneRepository timezoneRepository;

    @Autowired
    private UserRepository userRepository;

    private User user;

    @Before
    public void setup() {
        user = UnitTestingHelper.createSaveUser(userRepository, UnitTestingHelper.USERNAME_1, UserRole.USER);
        UnitTestingHelper.createSaveTimezone(timezoneRepository, UnitTestingHelper.TZ_NAME_1, user);
        UnitTestingHelper.createSaveTimezone(timezoneRepository, UnitTestingHelper.TZ_NAME_2, user);

        User user2 = UnitTestingHelper.createSaveUser(userRepository, UnitTestingHelper.USERNAME_2, UserRole.USER);
        UnitTestingHelper.createSaveTimezone(timezoneRepository, UnitTestingHelper.TZ_NAME_3, user2);
    }

    @Test
    public void testFindAllByUser() {
        List<Timezone> timezoneByUser = timezoneRepository.findAllByUser(user);
        assertEquals(2, timezoneByUser.size());
        assertEquals(UnitTestingHelper.TZ_NAME_1, timezoneByUser.get(0).getName());
        assertEquals(user.getName(), timezoneByUser.get(0).getOwner());
        assertEquals(UnitTestingHelper.TZ_NAME_2, timezoneByUser.get(1).getName());
        assertEquals(user.getName(), timezoneByUser.get(1).getOwner());
    }

    @Test
    public void testFindById() {
        Timezone timezone = timezoneRepository.findById(1);
        assertNotNull(timezone);
        assertEquals(UnitTestingHelper.TZ_NAME_1, timezone.getName());
        assertEquals(user.getName(), timezone.getOwner());
    }

    @Test
    public void testFindById_timeZoneNotFound() {
        Timezone timezone = timezoneRepository.findById(10);
        assertNull(timezone);
    }

    @Test
    public void deleteByUser() {
        timezoneRepository.deleteByUser(user);
        List<Timezone> timezones = timezoneRepository.findAllByUser(user);
        assertEquals(0, timezones.size());
    }
}
