package com.sample.ui.timezones.domain;

import java.util.ArrayList;
import java.util.List;

public class OffsetDto {

    private static List<OffsetDto> results;

    private final int offset;

    private final String description;

    private OffsetDto(int offset, String description) {
        this.offset = offset;
        this.description = description;
    }

    public int getOffset() {
        return offset;
    }

    public String getDescription() {
        return description;
    }

    public static synchronized List<OffsetDto> getOffsetList() {
        if (results == null) {
            results = new ArrayList<>();

            int startOffset = -12 * 60;
            double startHour = -12;
            while (startHour <= 12) {
                String description = "GMT ";
                if (startHour < 0)
                    description += "-" + Math.abs(startHour);
                else
                    description += "+" + Math.abs(startHour);
                results.add(new OffsetDto(startOffset, description));
                startHour += .5;
                startOffset += 30;
            }
        }
        return results;
    }

}
