package com.bcs.competition;

import com.btmatthews.springboot.memcached.EnableMemcached;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@EnableMemcached
@SpringBootApplication
public class ServiceApplication {

    public static void main(String[] args) {
        SpringApplication application = new SpringApplicationBuilder(ServiceApplication.class).build();
        application.run(args);
    }

}