package com.sample.ui.timezones.web.timezone;

import static com.sample.ui.timezones.util.SharedConstants.ERROR;

import java.util.ArrayList;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.vaadin.dialogs.ConfirmDialog;

import com.sample.ui.timezones.def.ApiResponseStatus;
import com.sample.ui.timezones.def.UserRole;
import com.sample.ui.timezones.domain.TimezoneDto;
import com.sample.ui.timezones.domain.UserDto;
import com.sample.ui.timezones.service.BackendManager;
import com.sample.ui.timezones.service.model.AddTimezoneResponse;
import com.sample.ui.timezones.service.model.ApiResults;
import com.sample.ui.timezones.service.model.GetTimezonesResponse;
import com.sample.ui.timezones.util.VaadinUtils;
import com.sample.ui.timezones.web.common.GridView;
import com.sample.ui.timezones.web.dialouges.CreateNewTimezoneDialog;
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
import com.vaadin.ui.renderers.ButtonRenderer;
import com.vaadin.ui.renderers.ClickableRenderer.RendererClickEvent;
import com.vaadin.ui.renderers.ClickableRenderer.RendererClickListener;
import com.vaadin.ui.themes.ValoTheme;

@SpringView(name = TimezonesView.VIEW_NAME)
public class TimezonesView extends GridView {

    private static final long serialVersionUID = -2833163868830026326L;

    public static final String VIEW_NAME = "Timezones View";

    private final BackendManager backendManager;

    private BeanItemContainer<TimezoneDto> container;

    @Autowired
    public TimezonesView(BackendManager backendManager) {
        this.backendManager = backendManager;
    }

    @PostConstruct
    private void initView() {
        super.init();
    }

    @Override
    public void refresh() {
        UserDto user = VaadinUtils.getLoggedInUser();

        GetTimezonesResponse response = backendManager.getUserTimezones(
                UserRole.ADMIN.equals(user.getUserRole()) ? null : user.getUsername(), user.getUsername(),
                VaadinUtils.getLoggedInUserPassword());

        if (response.getStatus().equals(ApiResponseStatus.SUCCESS)) {
            container.removeAllItems();
            container.addAll(response.getTimezones());
        } else {
            VaadinUtils.showNotification(ERROR, String.format(response.getMessage()),
                    Notification.Type.ERROR_MESSAGE);
        }

        if (user.getUserRole().equals(UserRole.USER)) {
            grid.getColumn(TimezoneGridField.OWNER.getColumnName()).setHidden(true);
        }
    }

    @Override
    protected void initButtonsPanel() {
        buttonPanel = new HorizontalLayout();
        buttonPanel.setSpacing(true);
        buttonPanel.setSizeUndefined();
        buttonPanel.setWidth(100, Unit.PERCENTAGE);

        Button addNewButton = new Button("New Timezone");
        addNewButton.addStyleName(ValoTheme.BUTTON_SMALL);
        addNewButton.addClickListener(new Button.ClickListener() {

            private static final long serialVersionUID = 1L;

            @Override
            public void buttonClick(ClickEvent event) {
                UI.getCurrent().addWindow(new CreateNewTimezoneDialog(backendManager) {

                    private static final long serialVersionUID = 8672469226403942762L;

                    @Override
                    protected void saveNewTimeZone(String city, String name, int offset, String timezoneUsername) {
                        String loggedInUsername = VaadinUtils.getLoggedInUser().getUsername();
                        AddTimezoneResponse response = backendManager.addTimeZone(city, name, offset,
                                StringUtils.isEmpty(timezoneUsername) ? loggedInUsername : timezoneUsername,
                                loggedInUsername, VaadinUtils.getLoggedInUserPassword());

                        if (response.getStatus().equals(ApiResponseStatus.SUCCESS)) {
                            container.addBean(response.getTimezone());
                            VaadinUtils.showNotification(response.getMessage(), null,
                                    Notification.Type.TRAY_NOTIFICATION);
                        } else {
                            VaadinUtils.showNotification("Error!", response.getMessage(),
                                    Notification.Type.ERROR_MESSAGE);
                        }
                    }
                });
            }

        });
        buttonPanel.addComponent(addNewButton);
        buttonPanel.setExpandRatio(addNewButton, 1);
        buttonPanel.setComponentAlignment(addNewButton, Alignment.MIDDLE_RIGHT);

        Button refreshButton = new Button("Refresh");
        refreshButton.addStyleName(ValoTheme.BUTTON_SMALL);
        refreshButton.addClickListener(new Button.ClickListener() {

            private static final long serialVersionUID = 1L;

            @Override
            public void buttonClick(ClickEvent event) {
                refresh();
            }

        });
        buttonPanel.addComponent(refreshButton);
        buttonPanel.setExpandRatio(refreshButton, 0);
        buttonPanel.setComponentAlignment(refreshButton, Alignment.MIDDLE_RIGHT);
    }

    @Override
    protected void initGrid() {
        container = new BeanItemContainer<TimezoneDto>(TimezoneDto.class, new ArrayList<>());
        GeneratedPropertyContainer wrapper = new GeneratedPropertyContainer(container);
        wrapper.addGeneratedProperty(TimezoneGridField.DELETE.getColumnName(),
                generatedColumn(String.class, itemId -> "Delete"));

        grid = new Grid(wrapper);
        grid.setSizeFull();
        grid.setSelectionMode(SelectionMode.NONE);
        grid.setEditorEnabled(false);
        grid.setColumnOrder(TimezoneGridField.ID.getColumnName(), TimezoneGridField.CITY.getColumnName(),
                TimezoneGridField.NAME.getColumnName(), TimezoneGridField.OFFSET.getColumnName(),
                TimezoneGridField.OWNER.getColumnName(), TimezoneGridField.DELETE.getColumnName());
        grid.getColumn(TimezoneGridField.ID.getColumnName()).setExpandRatio(1);
        grid.getColumn(TimezoneGridField.CITY.getColumnName()).setExpandRatio(2);
        grid.getColumn(TimezoneGridField.NAME.getColumnName()).setExpandRatio(2);
        grid.getColumn(TimezoneGridField.OWNER.getColumnName()).setExpandRatio(2);

        // Custom Renderers
        grid.getColumn(TimezoneGridField.OFFSET.getColumnName()).setRenderer(new TimeZoneRenderer())
                .setHeaderCaption("Timezone").setExpandRatio(4);

        grid.getColumn(TimezoneGridField.DELETE.getColumnName())
                .setRenderer(new ButtonRenderer(new RendererClickListener() {
                    private static final long serialVersionUID = 6317172548079450299L;

                    @Override
                    public void click(RendererClickEvent e) {
                        deleteTimeZone(e.getItemId());
                    }
                })).setHeaderCaption("").setExpandRatio(1);

        setColumnFiltering(true, TimezoneGridField.NAME.getColumnName(), TimezoneGridField.OWNER.getColumnName());
    }

    private void deleteTimeZone(Object itemId) {
        String title = "Delete Timezone";
        String message = "Do you want to delete timezone?";
        ConfirmDialog.show(UI.getCurrent(), title, message, "Yes", "No", new ConfirmDialog.Listener() {

            private static final long serialVersionUID = 1L;

            public void onClose(ConfirmDialog dialog) {
                if (dialog.isConfirmed()) {
                    TimezoneDto timezoneDto = container.getItem(itemId).getBean();
                    UserDto user = VaadinUtils.getLoggedInUser();
                    ApiResults results = backendManager.deleteTimezone(timezoneDto.getId(), user.getUsername(),
                            VaadinUtils.getLoggedInUserPassword());
                    if (results.getStatus().equals(ApiResponseStatus.SUCCESS)) {
                        VaadinUtils.showNotification(results.getMessage(), null, Notification.Type.TRAY_NOTIFICATION);
                        container.removeItem(itemId);
                    } else {
                        VaadinUtils.showNotification(ERROR, results.getMessage(),
                                Notification.Type.WARNING_MESSAGE);
                    }
                }
            }
        });

    }

    private static enum TimezoneGridField {
        ID("id"), CITY("city"), NAME("name"), OFFSET("offset"), OWNER("owner"), DELETE("delete");

        private final String columnName;

        private TimezoneGridField(String columnName) {
            this.columnName = columnName;
        }

        public String getColumnName() {
            return columnName;
        }

    }

}
