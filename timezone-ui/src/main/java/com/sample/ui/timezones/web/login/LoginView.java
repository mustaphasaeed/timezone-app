package com.sample.ui.timezones.web.login;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import com.sample.ui.timezones.service.BackendManager;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Reindeer;

@SpringView(name = LoginView.VIEW_NAME)
public class LoginView extends CustomComponent implements View {

    private static final long serialVersionUID = -4146563633662719740L;

    private static final String LOGIN_TAB_NAME = "Login";

    private static final String REGISTER_TAB_NAME = "Register";

    public static final String VIEW_NAME = "Timezone Login";

    private final BackendManager backendManager;

    @Autowired
    public LoginView(BackendManager backendManager) {
        this.backendManager = backendManager;
    }

    @PostConstruct
    void init() {
        setSizeFull();

        TabSheet tabsheet = new TabSheet();

        VerticalLayout layout = new VerticalLayout();
        layout.setSizeUndefined();
        layout.setSpacing(false);
        layout.setMargin(new MarginInfo(false, false, false, false));
        layout.setHeight(230, Unit.POINTS);
        layout.addComponent(new LoginLayout(backendManager));
        tabsheet.addTab(layout, LOGIN_TAB_NAME);

        layout = new VerticalLayout();
        layout.setSizeUndefined();
        layout.setSpacing(false);
        layout.setMargin(new MarginInfo(false, false, false, false));
        layout.setHeight(230, Unit.POINTS);
        layout.addComponent(new RegisterLayout(backendManager));
        tabsheet.addTab(layout, REGISTER_TAB_NAME);

        tabsheet.setSizeUndefined();

        VerticalLayout viewLayout = new VerticalLayout(tabsheet);
        viewLayout.setSizeFull();
        viewLayout.setComponentAlignment(tabsheet, Alignment.MIDDLE_CENTER);
        viewLayout.setStyleName(Reindeer.LAYOUT_BLUE);
        setCompositionRoot(viewLayout);

    }

    @Override
    public void enter(ViewChangeEvent event) {
        // the view is constructed in the init() method
    }

}