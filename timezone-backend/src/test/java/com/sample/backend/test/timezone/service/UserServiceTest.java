package com.sample.backend.test.timezone.service;

import static com.sample.backend.test.timezone.helper.UnitTestingHelper.NAME;
import static com.sample.backend.test.timezone.helper.UnitTestingHelper.NEW_NAME;
import static com.sample.backend.test.timezone.helper.UnitTestingHelper.NEW_PASSWORD;
import static com.sample.backend.test.timezone.helper.UnitTestingHelper.PASSWORD;
import static com.sample.backend.test.timezone.helper.UnitTestingHelper.USERNAME_1;
import static com.sample.backend.test.timezone.helper.UnitTestingHelper.USERNAME_2;
import static com.sample.backend.test.timezone.helper.UnitTestingHelper.createUser;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import com.sample.backend.timezones.def.UserRole;
import com.sample.backend.timezones.domain.User;
import com.sample.backend.timezones.error.UserAlreadyExisitException;
import com.sample.backend.timezones.error.UserNotFoundException;
import com.sample.backend.timezones.repository.TimezoneRepository;
import com.sample.backend.timezones.repository.UserRepository;
import com.sample.backend.timezones.service.UserService;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private TimezoneRepository timezoneRepository;

    @InjectMocks
    private UserService userService;

    @Before
    public void setup() {
        when(userRepository.save(any(User.class))).then(new Answer<User>() {
            @Override
            public User answer(InvocationOnMock invocation) throws Throwable {
                return (User) invocation.getArguments()[0];
            }
        });

        when(userRepository.findByUsername(USERNAME_1)).thenReturn(createUser(USERNAME_1, UserRole.USER));

    }

    @Test
    public void testRegisterUser() throws UserAlreadyExisitException {
        userService.registerUser(NAME, USERNAME_2, PASSWORD);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository, times(1)).save(captor.capture());
        assertEquals(NAME, captor.getValue().getName());
        assertEquals(USERNAME_2, captor.getValue().getUsername());
        assertEquals(UserRole.USER, captor.getValue().getUserRole());
    }

    @Test
    public void testChangePassword() throws UserNotFoundException {
        userService.resetPassword(USERNAME_1, NEW_PASSWORD);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository, times(1)).save(captor.capture());
        assertEquals(USERNAME_1, captor.getValue().getUsername());
        assertEquals(NEW_PASSWORD, captor.getValue().getPassword());
    }

    @Test(expected = UserNotFoundException.class)
    public void testChangePassword_userNotFound() throws UserNotFoundException {
        userService.resetPassword(USERNAME_2, NEW_PASSWORD);
    }

    @Test
    public void testUpdateProfile() {
        User user = userRepository.findByUsername(USERNAME_1);
        userService.updateProfile(user, NEW_NAME, NEW_PASSWORD);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository, times(1)).save(captor.capture());
        assertEquals(NEW_NAME, captor.getValue().getName());
        assertEquals(USERNAME_1, captor.getValue().getUsername());
        assertEquals(NEW_PASSWORD, captor.getValue().getPassword());
    }

    @Test
    public void testSetUserStatus() throws UserNotFoundException {
        userService.setUserStatus(USERNAME_1, false);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository, times(1)).save(captor.capture());
        assertEquals(USERNAME_1, captor.getValue().getUsername());
        assertEquals(false, captor.getValue().getActive());
    }

    @Test(expected = UserNotFoundException.class)
    public void testSetUserStatus_userNotFound() throws UserNotFoundException {
        userService.setUserStatus(USERNAME_2, false);
    }

    @Test
    public void testSetRole() throws UserNotFoundException {
        userService.setRole(USERNAME_1, UserRole.ADMIN);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository, times(1)).save(captor.capture());
        assertEquals(USERNAME_1, captor.getValue().getUsername());
        assertEquals(UserRole.ADMIN, captor.getValue().getUserRole());
    }

    @Test(expected = UserNotFoundException.class)
    public void testSetRole_userNotFound() throws UserNotFoundException {
        userService.setRole(USERNAME_2, UserRole.ADMIN);
    }

    @Test
    public void testDeleteUser() throws UserNotFoundException {
        User user = userRepository.findByUsername(USERNAME_1);

        userService.deleteUser(USERNAME_1);
        verify(userRepository, times(1)).delete(user);
        verify(timezoneRepository, times(1)).deleteByUser(user);
    }

    @Test(expected = UserNotFoundException.class)
    public void testDeleteUser_userNotFound() throws UserNotFoundException {
        userService.deleteUser(USERNAME_2);
    }

    @Test
    public void testListUsers() {
        when(userRepository.findAll()).thenReturn(
                Arrays.asList(createUser(USERNAME_1, UserRole.ADMIN), createUser(USERNAME_2, UserRole.USER)));

        List<User> users = userService.listUsers();
        assertEquals(2, users.size());
        assertEquals(USERNAME_1, users.get(0).getUsername());
        assertNull(users.get(0).getPassword());
        assertEquals(UserRole.ADMIN, users.get(0).getUserRole());
        assertEquals(USERNAME_2, users.get(1).getUsername());
        assertEquals(UserRole.USER, users.get(1).getUserRole());
        assertNull(users.get(1).getPassword());
    }

}
