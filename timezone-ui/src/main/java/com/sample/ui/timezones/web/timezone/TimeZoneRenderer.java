package com.sample.ui.timezones.web.timezone;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.vaadin.ui.renderers.HtmlRenderer;

import elemental.json.JsonValue;

public class TimeZoneRenderer extends HtmlRenderer {

    private static final long serialVersionUID = -7483564293868147555L;

    private static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("EEEEE, MMMMM d, yyyy h:mm:ss aaa");

    @Override
    public JsonValue encode(String value) {
        int offset = Integer.valueOf(value);
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MILLISECOND, -1 * cal.get(Calendar.ZONE_OFFSET) + offset * 60000);
        value = String.format(
                "<span class=\"clock\" title=\"" + value + "\">" + DATE_FORMATTER.format(cal.getTime()) + "</span><br>",
                value);
        return super.encode(value);
    }
}