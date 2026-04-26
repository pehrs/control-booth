package com.pehrs.cb.config;

import java.time.OffsetDateTime;
import java.util.Optional;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;


@Configuration
@EntityScan("com.pehrs.cb.port")
@EnableJpaRepositories({"com.pehrs.cb.port.out.repos"})
@ComponentScan("com.pehrs.cb.port.out.repos.domain")
@EnableTransactionManagement
@EnableJpaAuditing(dateTimeProviderRef = "auditingDateTimeProvider")
public class DomainConfig {

    @Bean(name = "auditingDateTimeProvider")
    public DateTimeProvider dateTimeProvider() {
        return () -> Optional.of(OffsetDateTime.now());
    }

}
