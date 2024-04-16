package com.chai.yygh.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

/**
 * @program: yygh
 * @author:
 * @create: 2024-04-16 21:54
 */
@SpringBootApplication
@ComponentScan(basePackages = "com.chai")
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.chai")
public class ServiceUserApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServiceUserApplication.class, args);
    }
}
