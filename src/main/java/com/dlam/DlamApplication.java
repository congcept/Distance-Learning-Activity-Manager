package com.dlam;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@SpringBootApplication
@ServletComponentScan // To support Filters if we keep them as standard filters temporarily
@org.springframework.data.jpa.repository.config.EnableJpaRepositories(basePackages = "com.dlam.repository")
@org.springframework.boot.autoconfigure.domain.EntityScan(basePackages = "com.dlam.model")
public class DlamApplication {

    public static void main(String[] args) {
        SpringApplication.run(DlamApplication.class, args);
    }
}
