package com.tickeron.counters.configuration;

import com.tickeron.counters.DbHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

/**
 * Created by slaviann on 16.02.16.
 */

@Configuration
@PropertySource("classpath:app.properties")
public class DataBaseConfiguration {

    @Autowired
    private Environment environment;

    @Bean()
    public DbHelper dbHelper()  {
        return new DbHelper(
                environment.getProperty("url"),
                environment.getProperty("username"),
                environment.getProperty("password"));
    }

}
