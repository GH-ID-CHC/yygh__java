package com.chai.cmn;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

/**
 * @program: yygh
 * @author:
 * @create: 2023-02-19 16:27
 **/
@SpringBootApplication
@ComponentScan("com.chai")
@EnableDiscoveryClient
public class ServiceCmnApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServiceCmnApplication.class, args);
        System.out.println("=======================================");
        System.out.println("http://localhost:8201/swagger-ui.html");
        System.out.println("=======================================");
    }
}
