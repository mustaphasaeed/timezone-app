package com.sample.ui.timezones.web.dialouges;

import static com.sample.ui.timezones.util.SharedConstants.INVALID_PASSWORD;
import static com.sample.ui.timezones.util.SharedConstants.NAME_IS_REQUIRED;
import static com.sample.ui.timezones.util.SharedConstants.PASSWORD_DOESNOT_MATCH;
import static com.sample.ui.timezones.util.SharedConstants.PASSWORD_REQUIREMENT;

import org.springframework.util.StringUtils;

import com.sample.ui.timezones.util.PasswordUtils;
import com.sample.ui.timezones.util.VaadinUtils;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

@SuppressWarnings("serial")
public abstract class EditProfileDialog extends Window {

    private final TextField nameField;

    private final PasswordField passwordField;

    private final PasswordField confirmPasswordField;

    public EditProfileDialog() {
        super("Edit Profile");
        setSizeUndefined();
        setModal(true);
        setResizable(false);

        FormLayout formLayout = new FormLayout();
        formLayout.setSizeUndefined();
        formLayout.setMargin(true);
        setContent(formLayout);

        nameField = new TextField("Name");
        nameField.addStyleName(ValoTheme.TEXTFIELD_SMALL);
        nameField.setValue(VaadinUtils.getLoggedInUser().getName());
        formLayout.addComponent(nameField);

        passwordField = new PasswordField("New Password");
        passwordField.addStyleName(ValoTheme.TEXTFIELD_SMALL);
        formLayout.addComponent(passwordField);

        confirmPasswordField = new PasswordField("Confirm Password");
        confirmPasswordField.addStyleName(ValoTheme.TEXTFIELD_SMALL);
        formLayout.addComponent(confirmPasswordField);

        HorizontalLayout buttonsPanel = new HorizontalLayout();
        buttonsPanel.setSizeUndefined();
        buttonsPanel.setWidth(100, Unit.PERCENTAGE);
        buttonsPanel.setSpacing(true);
        formLayout.addComponent(buttonsPanel);

        Button okButtone = new Button("Update");
        okButtone.addStyleName(ValoTheme.BUTTON_SMALL);
        okButtone.addClickListener(new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {

                if (!StringUtils.isEmpty(passwordField.getValue())
                        && !passwordField.getValue().equals(confirmPasswordField.getValue())) {
                    VaadinUtils.showNotification(PASSWORD_DOESNOT_MATCH, null, Type.WARNING_MESSAGE);
                    return;
                }
                if (!StringUtils.isEmpty(passwordField.getValue())
                        && !PasswordUtils.validatePassword(passwordField.getValue())) {
                    VaadinUtils.showNotification(INVALID_PASSWORD, PASSWORD_REQUIREMENT,
                            Notification.Type.WARNING_MESSAGE);
                    return;
                }

                if (StringUtils.isEmpty(nameField.getValue())) {
                    VaadinUtils.showNotification(NAME_IS_REQUIRED, null,
                            Notification.Type.WARNING_MESSAGE);
                    return;
                }

                updateUserProfile(nameField.getValue(), passwordField.getValue());

                close();
            }
        });

        Button cancelButton = new Button("Cancel");
        cancelButton.addStyleName(ValoTheme.BUTTON_SMALL);
        cancelButton.addClickListener(new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                close();
            }
        });
        buttonsPanel.addComponent(cancelButton);
        buttonsPanel.setExpandRatio(cancelButton, 1);
        buttonsPanel.setComponentAlignment(cancelButton, Alignment.MIDDLE_RIGHT);
        buttonsPanel.addComponent(okButtone);
        buttonsPanel.setExpandRatio(okButtone, 0);
        buttonsPanel.setComponentAlignment(okButtone, Alignment.MIDDLE_RIGHT);
    }

    protected abstract void updateUserProfile(String name, String newPassword);

}