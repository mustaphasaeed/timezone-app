package com.sample.backend.test.timezone.service;

import static com.sample.backend.test.timezone.helper.UnitTestingHelper.TZ_CITY;
import static com.sample.backend.test.timezone.helper.UnitTestingHelper.TZ_ID;
import static com.sample.backend.test.timezone.helper.UnitTestingHelper.TZ_ID_2;
import static com.sample.backend.test.timezone.helper.UnitTestingHelper.TZ_NAME_1;
import static com.sample.backend.test.timezone.helper.UnitTestingHelper.TZ_NAME_2;
import static com.sample.backend.test.timezone.helper.UnitTestingHelper.TZ_OFFSET;
import static com.sample.backend.test.timezone.helper.UnitTestingHelper.USERNAME_1;
import static com.sample.backend.test.timezone.helper.UnitTestingHelper.USERNAME_2;
import static com.sample.backend.test.timezone.helper.UnitTestingHelper.createTimezone;
import static com.sample.backend.test.timezone.helper.UnitTestingHelper.createUser;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
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
import com.sample.backend.timezones.domain.Timezone;
import com.sample.backend.timezones.domain.User;
import com.sample.backend.timezones.error.TimezoneNotFoundException;
import com.sample.backend.timezones.error.UserNotFoundException;
import com.sample.backend.timezones.repository.TimezoneRepository;
import com.sample.backend.timezones.repository.UserRepository;
import com.sample.backend.timezones.service.TimezoneService;

@RunWith(MockitoJUnitRunner.class)
public class TimezoneServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private TimezoneRepository timezoneRepository;

    @InjectMocks
    private TimezoneService timezoneService;

    @Before
    public void setup() {
        when(timezoneRepository.save(any(Timezone.class))).then(new Answer<Timezone>() {
            @Override
            public Timezone answer(InvocationOnMock invocation) throws Throwable {
                return (Timezone) invocation.getArguments()[0];
            }
        });

        when(userRepository.findByUsername(USERNAME_1)).thenReturn(createUser(USERNAME_1, UserRole.USER));
    }

    @Test
    public void testAddTimeZone() throws UserNotFoundException {
        timezoneService.addTimeZone(USERNAME_1, TZ_NAME_1, TZ_CITY, TZ_OFFSET);

        ArgumentCaptor<Timezone> captor = ArgumentCaptor.forClass(Timezone.class);
        verify(timezoneRepository, times(1)).save(captor.capture());
        assertEquals(TZ_NAME_1, captor.getValue().getName());
        assertEquals(TZ_CITY, captor.getValue().getCity());
        assertEquals(TZ_OFFSET, captor.getValue().getOffset());
    }

    @Test(expected = UserNotFoundException.class)
    public void testAddTimeZone_usetNotFound() throws UserNotFoundException {
        timezoneService.addTimeZone(USERNAME_2, TZ_NAME_1, TZ_CITY, TZ_OFFSET);
    }

    @Test
    public void testGetTimezoneOwner() throws TimezoneNotFoundException {
        when(timezoneRepository.findById(TZ_ID))
                .thenReturn(createTimezone(TZ_NAME_1, createUser(USERNAME_1, UserRole.USER)));

        User owner = timezoneService.getTimezoneOwner(TZ_ID);
        assertNotNull(owner);
        assertEquals(USERNAME_1, owner.getUsername());
    }

    @Test(expected = TimezoneNotFoundException.class)
    public void testGetTimezoneOwner_timezoneNotFound() throws TimezoneNotFoundException {
        timezoneService.getTimezoneOwner(TZ_ID_2);
    }

    @Test
    public void testDeleteTimeZone() throws TimezoneNotFoundException {
        when(timezoneRepository.findById(TZ_ID))
                .thenReturn(createTimezone(TZ_NAME_1, createUser(USERNAME_1, UserRole.USER)));

        timezoneService.deleteTimeZone(TZ_ID);

        ArgumentCaptor<Timezone> captor = ArgumentCaptor.forClass(Timezone.class);
        verify(timezoneRepository, times(1)).delete(captor.capture());
        assertEquals(TZ_NAME_1, captor.getValue().getName());
    }

    @Test(expected = TimezoneNotFoundException.class)
    public void testDeleteTimeZone_timezoneNotFound() throws TimezoneNotFoundException {
        timezoneService.deleteTimeZone(TZ_ID_2);
    }

    @Test
    public void testGetUserTimezones() throws UserNotFoundException {
        User user = userRepository.findByUsername(USERNAME_1);
        when(timezoneRepository.findAllByUser(user))
                .thenReturn(Arrays.asList(createTimezone(TZ_NAME_1, user), createTimezone(TZ_NAME_2, user)));

        List<Timezone> timezones = timezoneService.getUserTimezones(USERNAME_1);
        assertEquals(2, timezones.size());
        assertEquals(TZ_NAME_1, timezones.get(0).getName());
        assertEquals(TZ_NAME_2, timezones.get(1).getName());
    }

    @Test(expected = UserNotFoundException.class)
    public void testGetUserTimezones_userNotFound() throws UserNotFoundException {
        timezoneService.getUserTimezones(USERNAME_2);
    }

    @Test
    public void testGetAllTimeZones() {
        User user = userRepository.findByUsername(USERNAME_1);
        when(timezoneRepository.findAll())
                .thenReturn(Arrays.asList(createTimezone(TZ_NAME_1, user), createTimezone(TZ_NAME_2, user)));

        List<Timezone> timezones = timezoneService.getAllTimeZones();
        assertEquals(2, timezones.size());
        assertEquals(TZ_NAME_1, timezones.get(0).getName());
        assertEquals(TZ_NAME_2, timezones.get(1).getName());
    }

}
