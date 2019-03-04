package com.sample.ui.timezones.web.login;

import static com.sample.ui.timezones.util.SharedConstants.INVALID_PASSWORD;
import static com.sample.ui.timezones.util.SharedConstants.INVALID_USERNAME;
import static com.sample.ui.timezones.util.SharedConstants.LOGIN_FAILED;
import static com.sample.ui.timezones.util.SharedConstants.PASSWORD_LABEL;
import static com.sample.ui.timezones.util.SharedConstants.SESSION_PASSWORD_KEY;
import static com.sample.ui.timezones.util.SharedConstants.SESSION_USER_KEY;
import static com.sample.ui.timezones.util.SharedConstants.STANDARD_INPUT_SIZE;
import static com.sample.ui.timezones.util.SharedConstants.USERNAME_MUST_BE_AN_EMAIL;
import static com.sample.ui.timezones.util.SharedConstants.USERNAME_PROMPT;
import static com.sample.ui.timezones.util.SharedConstants.USERNMAE_LABEL;

import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.util.StringUtils;

import com.sample.ui.timezones.def.ApiResponseStatus;
import com.sample.ui.timezones.service.BackendManager;
import com.sample.ui.timezones.service.model.AuthenticationResponse;
import com.sample.ui.timezones.util.PasswordUtils;
import com.sample.ui.timezones.util.VaadinUtils;
import com.sample.ui.timezones.web.MainView;
import com.vaadin.server.VaadinSession;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Notification;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

public class LoginLayout extends VerticalLayout implements Button.ClickListener {

    private static final long serialVersionUID = -358083431364464012L;

    private final TextField username;

    private final PasswordField password;

    private final Button loginButton;

    BackendManager backendManager;

    public LoginLayout(BackendManager backendManager) {
        this.backendManager = backendManager;

        username = new TextField(USERNMAE_LABEL);
        username.setWidth(STANDARD_INPUT_SIZE);
        username.setRequired(true);
        username.setInputPrompt(USERNAME_PROMPT);
        username.setInvalidAllowed(false);

        password = new PasswordField(PASSWORD_LABEL);
        password.setWidth(STANDARD_INPUT_SIZE);
        password.setRequired(true);
        password.setValue("");
        password.setNullRepresentation("");

        loginButton = new Button("Login", this);

        addComponent(username);
        addComponent(password);
        addComponent(loginButton);
        setSpacing(true);
        setMargin(new MarginInfo(true, true, true, false));
        setSizeUndefined();
    }

    @Override
    public void buttonClick(ClickEvent event) {
        if (!EmailValidator.getInstance().isValid(username.getValue())) {
            VaadinUtils.showNotification(INVALID_USERNAME, USERNAME_MUST_BE_AN_EMAIL,
                    Notification.Type.WARNING_MESSAGE);
            return;
        }
        if (StringUtils.isEmpty(password.getValue())) {
            VaadinUtils.showNotification(INVALID_PASSWORD, null, Notification.Type.WARNING_MESSAGE);
            return;
        }

        String encPassword = PasswordUtils.hashPassword(password.getValue());
        AuthenticationResponse response = backendManager.loginUser(username.getValue(), encPassword);

        if (ApiResponseStatus.SUCCESS.equals(response.getStatus())) {
            VaadinSession.getCurrent().getSession().setAttribute(SESSION_USER_KEY, response.getUser());
            VaadinSession.getCurrent().getSession().setAttribute(SESSION_PASSWORD_KEY, encPassword);
            getUI().getNavigator().navigateTo(MainView.VIEW_NAME);
            VaadinUtils.showNotification(response.getMessage(), null, Notification.Type.TRAY_NOTIFICATION);
        } else {
            this.password.setValue(null);
            this.password.focus();
            VaadinUtils.showNotification(LOGIN_FAILED, response.getMessage(),
                    Notification.Type.WARNING_MESSAGE);
        }
    }

}
