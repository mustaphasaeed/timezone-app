package com.sample.backend.test.timezone.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

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
import com.sample.backend.timezones.domain.User;
import com.sample.backend.timezones.repository.UserRepository;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = { DatabaseConfig.class, SpringConfig.class })
@WebAppConfiguration
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Transactional
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Before
    public void setup() {
        UnitTestingHelper.createSaveUser(userRepository, UnitTestingHelper.USERNAME_1, UserRole.USER);
    }

    @Test
    public void testFindByUsername() {
        User user = userRepository.findByUsername(UnitTestingHelper.USERNAME_1);

        assertNotNull(user);
        assertEquals(UnitTestingHelper.NAME, user.getName());
        assertEquals(UnitTestingHelper.USERNAME_1, user.getUsername());
        assertEquals(UnitTestingHelper.PASSWORD, user.getPassword());
        assertEquals(UserRole.USER, user.getUserRole());
    }

    @Test
    public void testFindByUsername_userNotFound() {
        User user = userRepository.findByUsername(UnitTestingHelper.USERNAME_2);
        assertNull(user);
    }

}
