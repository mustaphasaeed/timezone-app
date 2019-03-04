package com.sample.ui.timezones.web.dialouges;

import org.springframework.util.StringUtils;

import com.sample.ui.timezones.def.ApiResponseStatus;
import com.sample.ui.timezones.def.UserRole;
import com.sample.ui.timezones.domain.OffsetDto;
import com.sample.ui.timezones.domain.UserDto;
import com.sample.ui.timezones.service.BackendManager;
import com.sample.ui.timezones.service.model.GetUsersResponse;
import com.sample.ui.timezones.util.VaadinUtils;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

@SuppressWarnings("serial")
public abstract class CreateNewTimezoneDialog extends Window {

    private final TextField city;

    private final TextField name;

    private final ComboBox offsetComboBox;

    private ComboBox userComboBox;

    public CreateNewTimezoneDialog(BackendManager backendManager) {
        super("New Timezone");
        setSizeUndefined();
        setModal(true);
        setResizable(false);

        FormLayout formLayout = new FormLayout();
        formLayout.setSizeUndefined();
        formLayout.setMargin(true);
        setContent(formLayout);

        city = new TextField("City");
        city.setRequired(true);
        city.addStyleName(ValoTheme.TEXTFIELD_SMALL);
        formLayout.addComponent(city);

        name = new TextField("Name");
        name.setRequired(true);
        name.addStyleName(ValoTheme.TEXTFIELD_SMALL);
        formLayout.addComponent(name);

        offsetComboBox = new ComboBox("Offset");
        offsetComboBox.setRequired(true);
        offsetComboBox.setImmediate(true);
        for (OffsetDto offsetDto : OffsetDto.getOffsetList()) {
            offsetComboBox.addItem(offsetDto.getOffset());
            offsetComboBox.setItemCaption(offsetDto.getOffset(), offsetDto.getDescription());
        }
        formLayout.addComponent(offsetComboBox);

        UserDto loggedInUser = VaadinUtils.getLoggedInUser();
        if (UserRole.ADMIN.equals(loggedInUser.getUserRole())) {
            GetUsersResponse response = backendManager.getAllUsers(loggedInUser.getUsername(),
                    VaadinUtils.getLoggedInUserPassword());
            if (ApiResponseStatus.FAIL.equals(response.getStatus())) {
                VaadinUtils.showNotification("Couldn't load users", response.getMessage(), Type.ERROR_MESSAGE);
                close();
                return;
            }
            userComboBox = new ComboBox("User");
            userComboBox.setImmediate(true);
            userComboBox.setRequired(true);
            for (UserDto user : response.getUsers()) {
                userComboBox.addItem(user.getUsername());
                userComboBox.setItemCaption(user.getUsername(), user.getName());
            }
            userComboBox.setValue(loggedInUser.getUsername());
            formLayout.addComponent(userComboBox);
        }

        HorizontalLayout buttonsPanel = new HorizontalLayout();
        buttonsPanel.setSizeUndefined();
        buttonsPanel.setWidth(100, Unit.PERCENTAGE);
        buttonsPanel.setSpacing(true);
        formLayout.addComponent(buttonsPanel);

        Button okButtone = new Button("Ok");
        okButtone.addStyleName(ValoTheme.BUTTON_SMALL);
        okButtone.addClickListener(new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {

                if (StringUtils.isEmpty(city.getValue()) || StringUtils.isEmpty(name.getValue())
                        || offsetComboBox.getValue() == null
                        || (userComboBox != null && userComboBox.getValue() == null)) {
                    VaadinUtils.showNotification("All fields are mandatory", "", Type.WARNING_MESSAGE);
                    return;
                }

                saveNewTimeZone(city.getValue(), name.getValue(), Integer.valueOf(offsetComboBox.getValue().toString()),
                        userComboBox == null ? null : String.valueOf(userComboBox.getValue()));
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

    protected abstract void saveNewTimeZone(String city, String name, int offset, String timezoneUsername);

}