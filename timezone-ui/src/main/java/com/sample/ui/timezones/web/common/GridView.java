package com.sample.ui.timezones.web.common;

import java.util.function.Consumer;
import java.util.function.Function;

import com.vaadin.data.Container.Filterable;
import com.vaadin.data.Item;
import com.vaadin.data.util.PropertyValueGenerator;
import com.vaadin.data.util.filter.SimpleStringFilter;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.HeaderRow;
import com.vaadin.ui.renderers.ButtonRenderer;
import com.vaadin.ui.renderers.ClickableRenderer.RendererClickEvent;
import com.vaadin.ui.renderers.ClickableRenderer.RendererClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

public abstract class GridView extends CustomComponent implements View {

    private static final long serialVersionUID = 7650998892637599226L;

    protected Grid grid;

    protected HorizontalLayout buttonPanel;

    private HeaderRow filteringHeader;

    protected void init() {
        setSizeFull();

        // Init Grid
        initGrid();

        // Init Buttons
        initButtonsPanel();

        // Init layout
        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setSizeFull();
        verticalLayout.addComponent(grid);
        verticalLayout.setExpandRatio(grid, 1);

        verticalLayout.addComponent(buttonPanel);
        verticalLayout.setExpandRatio(buttonPanel, 0);

        setCompositionRoot(verticalLayout);
    }

    protected void setColumnFiltering(boolean filtered, String... columnIds) {
        if (filtered && filteringHeader == null) {
            filteringHeader = grid.appendHeaderRow();

            for (String columnId : columnIds) {
                TextField filter = getColumnFilter(columnId);
                filteringHeader.getCell(columnId).setComponent(filter);
                filteringHeader.getCell(columnId).setStyleName("filter-header");
            }
        } else if (!filtered && filteringHeader != null) {
            grid.removeHeaderRow(filteringHeader);
            filteringHeader = null;
        }
    }

    protected <T> PropertyValueGenerator<T> generatedColumn(Class<T> kalss, Function<Object, T> valueGenearator) {
        return new PropertyValueGenerator<T>() {

            private static final long serialVersionUID = 4965500383857557229L;

            @Override
            public T getValue(Item item, Object itemId, Object propertyId) {
                return valueGenearator.apply(itemId);
            }

            @Override
            public Class<T> getType() {
                return kalss;
            }
        };
    }

    protected ButtonRenderer generateButtonRenderer(Consumer<Object> clickAction) {
        return new ButtonRenderer(new RendererClickListener() {
            private static final long serialVersionUID = 6317172548079450299L;

            @Override
            public void click(RendererClickEvent e) {
                clickAction.accept(e.getItemId());
            }
        });
    }

    private TextField getColumnFilter(final Object columnId) {
        TextField filter = new TextField();
        filter.setWidth("100%");
        filter.addStyleName(ValoTheme.TEXTFIELD_TINY);
        filter.setInputPrompt("Filter");
        filter.addTextChangeListener(new TextChangeListener() {

            private static final long serialVersionUID = -8130159779767411293L;

            private SimpleStringFilter filter = null;

            @Override
            public void textChange(TextChangeEvent event) {
                Filterable f = (Filterable) grid.getContainerDataSource();

                if (filter != null) {
                    f.removeContainerFilter(filter);
                }

                filter = new SimpleStringFilter(columnId, event.getText(), true, true);
                f.addContainerFilter(filter);

                grid.cancelEditor();
            }
        });
        return filter;
    }

    @Override
    public void enter(ViewChangeEvent event) {
        // the view is constructed in the init() method
    }

    protected abstract void initGrid();

    protected abstract void initButtonsPanel();

    public abstract void refresh();
}
