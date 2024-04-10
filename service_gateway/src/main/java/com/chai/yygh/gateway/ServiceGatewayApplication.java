package com.chai.yygh.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @program: yygh
 * @author:
 * @create: 2024-04-10 21:30
 */
@SpringBootApplication
public class ServiceGatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServiceGatewayApplication.class, args);

        System.out.println("=======================================");
        System.out.println("http://localhost:8080/swagger-ui.html");
        System.out.println("=======================================");
    }
}