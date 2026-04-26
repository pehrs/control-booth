package com.pehrs.cb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication
@EnableScheduling
//@EnableJpaRepositories("com.pehrs.cb.*")
// @ComponentScan(basePackages = { "com.pehrs.cb.*"  })
@EntityScan("com.pehrs.cb.*")
public class ControlBoothApplication {

    public static void main(final String[] args) {
        SpringApplication.run(ControlBoothApplication.class, args);
    }

}
