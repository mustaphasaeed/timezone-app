package com.sample.backend.test.config;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.hibernate.dialect.H2Dialect;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Copyright (c) 2015, All rights reserved. 
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * 
 * Configuration class that hold the required H2 DB configuration
 */
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(DatabaseConfig.PACKAGES_TO_SCAN)
public class DatabaseConfig {

    public static final String PACKAGES_TO_SCAN = "com.sample";
    
    private static final String UTF8 = "UTF-8";

    private static final String HBM2DLL = "hibernate.hbm2ddl.auto";

    private static final Object VALIDATE = "validate";

    @Bean
    public DataSource dataSource() {

        EmbeddedDatabaseBuilder builder = new EmbeddedDatabaseBuilder();
        return builder.setType(EmbeddedDatabaseType.H2).setScriptEncoding(UTF8).continueOnError(true)
                .ignoreFailedDrops(true).build();
    }

    @Bean
    public EntityManagerFactory entityManagerFactory() {

        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter() {
            protected Class<?> determineDatabaseDialectClass(Database database) {
                switch (database) {
                case H2:
                    return H2Dialect.class;
                default:
                    return null;
                }
            }
        };
        vendorAdapter.setDatabase(Database.H2);
        vendorAdapter.setGenerateDdl(true);
        vendorAdapter.setShowSql(true);

        LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
        factory.setJpaVendorAdapter(vendorAdapter);
        factory.setPackagesToScan(PACKAGES_TO_SCAN);
        factory.setDataSource(dataSource());
        factory.setJpaDialect(vendorAdapter.getJpaDialect());
        factory.getJpaPropertyMap().put(HBM2DLL, VALIDATE);
        factory.afterPropertiesSet();

        return factory.getObject();
    }

    @Bean
    public JdbcTemplate jdbcTemplate() {
        return new JdbcTemplate(dataSource());
    }

}
