package com.sample.ui.timezones.web;

import org.springframework.beans.factory.annotation.Autowired;

import com.sample.ui.timezones.web.login.LoginView;
import com.vaadin.annotations.PreserveOnRefresh;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinSession;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.spring.navigator.SpringViewProvider;
import com.vaadin.ui.Panel;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

@SpringUI
@PreserveOnRefresh
@Title("Timezones App")
@Theme("sample")
public class TimezoneUI extends UI {

    private static final long serialVersionUID = -1608892839181883672L;

    @Autowired
    private SpringViewProvider viewProvider;

    @Override
    protected void init(VaadinRequest request) {
        final VerticalLayout root = new VerticalLayout();
        root.setSizeFull();
        root.setMargin(true);
        root.setSpacing(true);
        setContent(root);

        final Panel viewContainer = new Panel();
        viewContainer.setSizeFull();
        root.addComponent(viewContainer);
        root.setExpandRatio(viewContainer, 1.0f);

        Navigator navigator = new Navigator(this, viewContainer);
        navigator.addProvider(viewProvider);
        navigator.addViewChangeListener(new ViewChangeListener() {

            private static final long serialVersionUID = 1L;

            @Override
            public boolean beforeViewChange(ViewChangeEvent event) {
                boolean isLoggedIn = VaadinSession.getCurrent().getSession().getAttribute("user") != null;
                boolean isLoginView = event.getNewView() instanceof LoginView;
                if (!isLoggedIn && !isLoginView) {
                    getNavigator().navigateTo(LoginView.VIEW_NAME);
                    return false;
                } else if (isLoggedIn && isLoginView) {
                    return false;
                }

                if (event.getNewView() instanceof MainView) {
                    MainView mainView = (MainView) event.getNewView();
                    mainView.refresh();
                }

                return true;
            }

            @Override
            public void afterViewChange(ViewChangeEvent event) {

            }
        });

    }
}
