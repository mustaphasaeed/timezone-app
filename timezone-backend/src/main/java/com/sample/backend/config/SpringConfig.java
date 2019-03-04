package com.sample.backend.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

/**
 * Copyright (c) 2015, All rights reserved. 
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * 
 * Configuration class that hold the required Spring configuration
 */
@Configuration
@ComponentScan(basePackages = "com.sample")
@EnableWebMvc
@EnableTransactionManagement
@EnableAsync
public class SpringConfig {

    private static final String PROPERTIES_FILE_NAME = "application.properties";

    public static PropertySourcesPlaceholderConfigurer getPropertySourcesPlaceholderConfigurer() {
        PropertySourcesPlaceholderConfigurer propertiesPlaceholder = new PropertySourcesPlaceholderConfigurer();
        Resource[] resourceLocations = new Resource[] { new ClassPathResource(PROPERTIES_FILE_NAME) };
        propertiesPlaceholder.setLocations(resourceLocations);
        return propertiesPlaceholder;
    }

}
