package com.tickeron.counters.configuration;

import com.tickeron.counters.DbHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by slaviann on 16.02.16.
 */

@Configuration
@PropertySource("classpath:app.properties")
public class DataBaseConfiguration {

    @Autowired
    Environment environment;

    @Bean()
    DbHelper dbHelper()  {

        System.out.println(environment.getProperty("url"));
        System.out.println(environment.getProperty("username"));
        System.out.println(environment.getProperty("password"));

        return new DbHelper(
                environment.getProperty("url"),
                environment.getProperty("username"),
                environment.getProperty("password"));
    }

}
