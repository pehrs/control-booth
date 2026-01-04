package com.pehrs.controlbooth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication
@EnableScheduling
//@EnableJpaRepositories("com.pehrs.controlbooth.*")
//@ComponentScan(basePackages = { "com.pehrs.controlbooth.*"  })
@EntityScan("com.pehrs.controlbooth.*")
public class ControlBoothApplication {

    public static void main(final String[] args) {
        SpringApplication.run(ControlBoothApplication.class, args);
    }

}
