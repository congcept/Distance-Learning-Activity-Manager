package com.dlam;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@SpringBootApplication
@ServletComponentScan // To support Filters if we keep them as standard filters temporarily
public class DlamApplication {

    public static void main(String[] args) {
        SpringApplication.run(DlamApplication.class, args);
    }
}
