package com.sample.ui.timezones;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.sample.ui")
public class TimezonesUiApp extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(TimezonesUiApp.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(TimezonesUiApp.class);
    }

}
