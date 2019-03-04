package com.sample.ui.timezones.service.model;

public class HttpApiResponse {

    private final int statusCode;

    private final String contents;

    public HttpApiResponse(int statusCode, String contents) {
        this.statusCode = statusCode;
        this.contents = contents;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getContents() {
        return contents;
    }

}
