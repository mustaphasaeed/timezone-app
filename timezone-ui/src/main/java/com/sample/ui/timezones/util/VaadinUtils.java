package com.sample.ui.timezones.util;

import static com.sample.ui.timezones.util.SharedConstants.SESSION_PASSWORD_KEY;
import static com.sample.ui.timezones.util.SharedConstants.SESSION_USER_KEY;

import com.sample.ui.timezones.domain.UserDto;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.UI;

public class VaadinUtils {

    public static void showNotification(String caption, String description, Type type) {
        Notification error = new Notification(caption, description, type);
        error.show(UI.getCurrent().getPage());
    }

    public static UserDto getLoggedInUser() {
        return (UserDto) VaadinSession.getCurrent().getSession().getAttribute(SESSION_USER_KEY);
    }

    public static String getLoggedInUserPassword() {
        return (String) VaadinSession.getCurrent().getSession().getAttribute(SESSION_PASSWORD_KEY);
    }

}
