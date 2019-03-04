package com.sample.ui.timezones.web.user;

import static com.sample.ui.timezones.util.SharedConstants.ERROR;

import java.util.ArrayList;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.dialogs.ConfirmDialog;

import com.sample.ui.timezones.def.ApiResponseStatus;
import com.sample.ui.timezones.def.UserRole;
import com.sample.ui.timezones.domain.UserDto;
import com.sample.ui.timezones.service.BackendManager;
import com.sample.ui.timezones.service.model.ApiResults;
import com.sample.ui.timezones.service.model.GetUsersResponse;
import com.sample.ui.timezones.util.PasswordUtils;
import com.sample.ui.timezones.util.VaadinUtils;
import com.sample.ui.timezones.web.common.GridView;
import com.sample.ui.timezones.web.dialouges.ChangePasswordDialog;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.GeneratedPropertyContainer;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import com.vaadin.ui.themes.ValoTheme;

@SpringView(name = UsersView.VIEW_NAME)
public class UsersView extends GridView {

    private static final long serialVersionUID = -2964915905643268966L;

    public static final String VIEW_NAME = "Users View";

    private final BackendManager backendManager;

    private BeanItemContainer<UserDto> container;

    @Autowired
    public UsersView(BackendManager backendManager) {
        this.backendManager = backendManager;
    }

    @PostConstruct
    private void initView() {
        super.init();
    }

    @Override
    public void refresh() {
        UserDto user = VaadinUtils.getLoggedInUser();

        GetUsersResponse response = backendManager.getAllUsers(user.getUsername(),
                VaadinUtils.getLoggedInUserPassword());

        if (response.getStatus().equals(ApiResponseStatus.SUCCESS)) {
            container.removeAllItems();
            container.addAll(response.getUsers());
        } else {
            VaadinUtils.showNotification(ERROR, response.getMessage(), Notification.Type.ERROR_MESSAGE);
        }
    }

    @Override
    protected void initGrid() {
        container = new BeanItemContainer<UserDto>(UserDto.class, new ArrayList<>());
        GeneratedPropertyContainer wrapper = new GeneratedPropertyContainer(container);
        wrapper.addGeneratedProperty(UserGridField.DELETE.getColumnName(),
                generatedColumn(String.class, itemId -> "Delete"));
        wrapper.addGeneratedProperty(UserGridField.CHANGE_PASSWORD.getColumnName(),
                generatedColumn(String.class, itemId -> "Change Password"));
        wrapper.addGeneratedProperty(UserGridField.CHANGE_ROLE.getColumnName(),
                generatedColumn(String.class, itemId -> {
                    return UserRole.ADMIN.equals(container.getItem(itemId).getBean().getUserRole()) ? "Revoke Access"
                            : "Promote";
                }));
        wrapper.addGeneratedProperty(UserGridField.ACTIVATE.getColumnName(), generatedColumn(String.class, itemId -> {
            return container.getItem(itemId).getBean().isActive() ? "Deactivate" : "Activate";
        }));

        grid = new Grid(wrapper);
        grid.setSizeFull();
        grid.setSelectionMode(SelectionMode.NONE);
        grid.setEditorEnabled(false);
        grid.setColumnOrder(UserGridField.NAME.getColumnName(), UserGridField.USERNAME.getColumnName(),
                UserGridField.PASSWORD.getColumnName(), UserGridField.ACTIVE.getColumnName(),
                UserGridField.USERROLE.getColumnName(), UserGridField.ACTIVATE.getColumnName(),
                UserGridField.CHANGE_ROLE.getColumnName(), UserGridField.CHANGE_PASSWORD.getColumnName(),
                UserGridField.DELETE.getColumnName());
        grid.getColumn(UserGridField.NAME.getColumnName()).setExpandRatio(2);
        grid.getColumn(UserGridField.USERNAME.getColumnName()).setExpandRatio(2);
        grid.getColumn(UserGridField.PASSWORD.getColumnName()).setHidden(true);
        grid.getColumn(UserGridField.ACTIVE.getColumnName()).setHidden(true);
        grid.getColumn(UserGridField.USERROLE.getColumnName()).setExpandRatio(2);

        setColumnFiltering(true, UserGridField.NAME.getColumnName(), UserGridField.USERNAME.getColumnName());

        // Custom Renderers
        grid.getColumn(UserGridField.DELETE.getColumnName()).setRenderer(generateButtonRenderer(this::deleteUser))
                .setHeaderCaption("").setExpandRatio(0);
        grid.getColumn(UserGridField.CHANGE_ROLE.getColumnName())
                .setRenderer(generateButtonRenderer(this::changeUserRole)).setHeaderCaption("").setExpandRatio(0);
        grid.getColumn(UserGridField.CHANGE_PASSWORD.getColumnName())
                .setRenderer(generateButtonRenderer(this::changeUserPassword)).setHeaderCaption("").setExpandRatio(0);
        grid.getColumn(UserGridField.ACTIVATE.getColumnName()).setRenderer(generateButtonRenderer(this::activateUser))
                .setHeaderCaption("").setExpandRatio(0);

    }

    @Override
    protected void initButtonsPanel() {
        buttonPanel = new HorizontalLayout();
        buttonPanel.setSpacing(true);
        buttonPanel.setSizeUndefined();
        buttonPanel.setWidth(100, Unit.PERCENTAGE);

        Button refreshButton = new Button("Refresh");
        refreshButton.addClickListener(new Button.ClickListener() {

            private static final long serialVersionUID = 1L;

            @Override
            public void buttonClick(ClickEvent event) {
                refresh();
            }

        });
        refreshButton.addStyleName(ValoTheme.BUTTON_SMALL);
        buttonPanel.addComponent(refreshButton);
        buttonPanel.setExpandRatio(refreshButton, 0);
        buttonPanel.setComponentAlignment(refreshButton, Alignment.MIDDLE_RIGHT);
    }

    private void deleteUser(Object itemId) {
        String title = "Delete User";
        String message = "Do you want to delete user?";
        ConfirmDialog.show(UI.getCurrent(), title, message, "Yes", "No", new ConfirmDialog.Listener() {

            private static final long serialVersionUID = 1L;

            public void onClose(ConfirmDialog dialog) {
                if (dialog.isConfirmed()) {
                    UserDto impactedUser = container.getItem(itemId).getBean();
                    UserDto user = VaadinUtils.getLoggedInUser();
                    ApiResults results = backendManager.deleteUser(impactedUser.getUsername(), user.getUsername(),
                            VaadinUtils.getLoggedInUserPassword());
                    if (results.getStatus().equals(ApiResponseStatus.SUCCESS)) {
                        VaadinUtils.showNotification(results.getMessage(), null, Notification.Type.TRAY_NOTIFICATION);
                        container.removeItem(itemId);
                    } else {
                        VaadinUtils.showNotification(ERROR, results.getMessage(),
                                Notification.Type.ERROR_MESSAGE);
                    }
                }
            }
        });
    }

    private void changeUserRole(Object itemId) {
        UserDto impactedUser = container.getItem(itemId).getBean();
        boolean isAdmin = impactedUser.getUserRole().equals(UserRole.ADMIN);
        String title = isAdmin ? "Revoke Access" : "Promote User";
        String message = isAdmin ? "Are you sure that you want to revoke user admin access ?"
                : "Are you sure that you want to grant admin access ?";
        ConfirmDialog.show(UI.getCurrent(), title, message, "Yes", "No", new ConfirmDialog.Listener() {

            private static final long serialVersionUID = 1L;

            public void onClose(ConfirmDialog dialog) {
                if (dialog.isConfirmed()) {
                    UserDto user = VaadinUtils.getLoggedInUser();
                    UserRole newRole = isAdmin ? UserRole.USER : UserRole.ADMIN;
                    ApiResults results = backendManager.changeUserRole(impactedUser.getUsername(), newRole,
                            user.getUsername(), VaadinUtils.getLoggedInUserPassword());
                    if (results.getStatus().equals(ApiResponseStatus.SUCCESS)) {
                        VaadinUtils.showNotification(results.getMessage(), null, Notification.Type.TRAY_NOTIFICATION);
                        impactedUser.setUserRole(newRole);
                        grid.clearSortOrder();
                    } else {
                        VaadinUtils.showNotification(ERROR, results.getMessage(),
                                Notification.Type.ERROR_MESSAGE);
                    }
                }
            }
        });
    }

    private void activateUser(Object itemId) {
        UserDto impactedUser = container.getItem(itemId).getBean();
        boolean isActive = impactedUser.isActive();
        String title = isActive ? "Deactivate User" : "Activate User";
        String message = isActive ? "Are you sure that you want to deactivate this user ?"
                : "Are you sure that you want to activate this user ?";
        ConfirmDialog.show(UI.getCurrent(), title, message, "Yes", "No", new ConfirmDialog.Listener() {

            private static final long serialVersionUID = 1L;

            public void onClose(ConfirmDialog dialog) {
                if (dialog.isConfirmed()) {
                    UserDto user = VaadinUtils.getLoggedInUser();
                    ApiResults results = backendManager.setUserStatus(impactedUser.getUsername(),
                            !impactedUser.isActive(), user.getUsername(), VaadinUtils.getLoggedInUserPassword());
                    if (results.getStatus().equals(ApiResponseStatus.SUCCESS)) {
                        VaadinUtils.showNotification(results.getMessage(), null, Notification.Type.TRAY_NOTIFICATION);
                        impactedUser.setActive(!impactedUser.isActive());
                        grid.clearSortOrder();
                    } else {
                        VaadinUtils.showNotification(ERROR, results.getMessage(),
                                Notification.Type.ERROR_MESSAGE);
                    }
                }
            }
        });
    }

    private void changeUserPassword(Object itemId) {
        UserDto impactedUser = container.getItem(itemId).getBean();
        ChangePasswordDialog dialog = new ChangePasswordDialog() {

            private static final long serialVersionUID = -109124647156318148L;

            @Override
            protected void updatePassword(String newPassword) {
                UserDto user = VaadinUtils.getLoggedInUser();
                ApiResults results = backendManager.resetPassword(impactedUser.getUsername(),
                        PasswordUtils.hashPassword(newPassword), user.getUsername(),
                        VaadinUtils.getLoggedInUserPassword());
                if (results.getStatus().equals(ApiResponseStatus.SUCCESS)) {
                    VaadinUtils.showNotification(results.getMessage(), null, Notification.Type.TRAY_NOTIFICATION);
                } else {
                    VaadinUtils.showNotification(ERROR, results.getMessage(),
                            Notification.Type.ERROR_MESSAGE);
                }
            }

        };
        UI.getCurrent().addWindow(dialog);
    }

    private static enum UserGridField {
        NAME("name"), USERNAME("username"), PASSWORD("password"), ACTIVE("active"), USERROLE("userRole"), ACTIVATE(
                "activate"), CHANGE_ROLE("changeRole"), CHANGE_PASSWORD("changePassword"), DELETE("delete");

        private final String columnName;

        private UserGridField(String columnName) {
            this.columnName = columnName;
        }

        public String getColumnName() {
            return columnName;
        }
    }

}
