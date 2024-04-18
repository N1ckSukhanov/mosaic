package com.example.mosaica.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.dialect.IDialect;
import org.thymeleaf.extras.java8time.dialect.Java8TimeDialect;

@Configuration
public class ApplicationConfiguration {
    @Bean
    public IDialect conditionalCommentDialect() {
        return new Java8TimeDialect();
    }
}
