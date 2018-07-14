package com.springtest.run;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(value = "com.springtest.*")
public class Startup {

    public static void main(String[] args) {

        SpringApplication.run(Startup.class, args);

    }

}
