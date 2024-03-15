package com.example.demo.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

/**
 * @author Avijeet
 * @project UserRegistration
 * @github avijeetas
 * @date 03-15-2024
 **/
@Configuration
public class ThymeleafConfig {

    @Bean
    public ClassLoaderTemplateResolver emailTemplateResolver() {
        ClassLoaderTemplateResolver resolver = new ClassLoaderTemplateResolver();
        resolver.setPrefix("/templates/"); // Set the prefix where your email templates are located
        resolver.setSuffix(".html"); // Set the suffix of your email template files
        resolver.setTemplateMode("HTML"); // Set the template mode
        resolver.setCharacterEncoding("UTF-8"); // Set the character encoding
        resolver.setOrder(1);
        return resolver;
    }

    // Other Thymeleaf configuration beans...
}
