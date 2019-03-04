package com.sample.ui.timezones.web;

import static com.sample.ui.timezones.util.SharedConstants.ERROR;
import static com.sample.ui.timezones.util.SharedConstants.SESSION_PASSWORD_KEY;
import static com.sample.ui.timezones.util.SharedConstants.SESSION_USER_KEY;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import com.sample.ui.timezones.def.ApiResponseStatus;
import com.sample.ui.timezones.def.UserRole;
import com.sample.ui.timezones.domain.UserDto;
import com.sample.ui.timezones.service.BackendManager;
import com.sample.ui.timezones.service.model.ApiResults;
import com.sample.ui.timezones.util.PasswordUtils;
import com.sample.ui.timezones.util.VaadinUtils;
import com.sample.ui.timezones.web.dialouges.EditProfileDialog;
import com.sample.ui.timezones.web.timezone.TimezonesView;
import com.sample.ui.timezones.web.user.UsersView;
import com.vaadin.annotations.JavaScript;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.VaadinSession;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.MenuItem;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

@SpringView(name = MainView.VIEW_NAME)
@JavaScript({ "vaadin://scripts/timezone5.js" })
public class MainView extends CustomComponent implements View {

    private static final long serialVersionUID = 1L;

    private static final String LOGOUT_MENU_LABEL = "Logout";

    private static final String EDIT_PROFILE_MENU_LABEL = "Edit Profile";

    private static String TIMEZONE_TAB_NAME = "Timezones";

    private static String USERS_TAB_NAME = "Users";

    public static final String VIEW_NAME = "";

    @Autowired
    private TimezonesView timezonesView;

    @Autowired
    private UsersView usersView;

    @Autowired
    private BackendManager backendManager;

    @Override
    public void enter(ViewChangeEvent event) {
        UserDto user = VaadinUtils.getLoggedInUser();
        setSizeFull();

        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();
        layout.setMargin(true);
        layout.setSpacing(true);

        HorizontalLayout header = new HorizontalLayout();
        header.setWidth(100, Unit.PERCENTAGE);
        layout.addComponent(header);

        Label appLabel = new Label("Timezone Viewer");
        appLabel.setStyleName("app-label");
        appLabel.setSizeFull();
        header.addComponent(appLabel);
        header.setComponentAlignment(appLabel, Alignment.TOP_LEFT);
        header.setExpandRatio(appLabel, 1);

        MenuBar menu = new MenuBar();
        header.addComponent(menu);
        header.setComponentAlignment(menu, Alignment.TOP_RIGHT);
        header.setExpandRatio(menu, 0);

        String firstName = user.getName().split(" ")[0];
        MenuItem userMenu = menu.addItem("Hello, " + firstName, null, null);
        MenuBar.Command editProfileCommad = new MenuBar.Command() {

            private static final long serialVersionUID = -148451157149584666L;

            @Override
            public void menuSelected(MenuItem selectedItem) {
                UI.getCurrent().addWindow(new EditProfileDialog() {

                    private static final long serialVersionUID = -4026137770820452239L;

                    @Override
                    protected void updateUserProfile(String name, String newPassword) {
                        UserDto user = VaadinUtils.getLoggedInUser();
                        ApiResults results = backendManager.updateProfile(name,
                                StringUtils.isEmpty(newPassword) ? null : PasswordUtils.hashPassword(newPassword),
                                user.getUsername(), VaadinUtils.getLoggedInUserPassword());
                        if (results.getStatus().equals(ApiResponseStatus.SUCCESS)) {
                            user.setName(name);
                            String firstName = user.getName().split(" ")[0];
                            userMenu.setText("Hello, " + firstName);
                            VaadinUtils.showNotification(results.getMessage(), null,
                                    Notification.Type.TRAY_NOTIFICATION);
                        } else {
                            VaadinUtils.showNotification(ERROR, results.getMessage(),
                                    Notification.Type.ERROR_MESSAGE);
                        }
                    }
                });
            }
        };
        userMenu.addItem(EDIT_PROFILE_MENU_LABEL, editProfileCommad);

        MenuBar.Command logoutCommand = new MenuBar.Command() {

            private static final long serialVersionUID = 2823653290844218662L;

            public void menuSelected(MenuItem selectedItem) {
                VaadinSession.getCurrent().getSession().setAttribute(SESSION_USER_KEY, null);
                VaadinSession.getCurrent().getSession().setAttribute(SESSION_PASSWORD_KEY, null);
                getUI().getNavigator().navigateTo(VIEW_NAME);
            }
        };
        userMenu.addItem(LOGOUT_MENU_LABEL, logoutCommand);

        TabSheet tabSheet = new TabSheet();
        tabSheet.setSizeFull();
        layout.addComponent(tabSheet);
        layout.setExpandRatio(tabSheet, 1f);

        tabSheet.addTab(timezonesView, TIMEZONE_TAB_NAME);
        if (UserRole.ADMIN.equals(user.getUserRole())) {
            tabSheet.addTab(usersView, USERS_TAB_NAME);
        }
        tabSheet.setSizeFull();

        setCompositionRoot(layout);
    }

    public void refresh() {
        timezonesView.refresh();
        if (UserRole.ADMIN.equals(VaadinUtils.getLoggedInUser().getUserRole()))
            usersView.refresh();
    }
}