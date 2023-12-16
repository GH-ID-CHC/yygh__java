package com.chai.yygh;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

/**
 * @program: yygh
 * @author:
 * @create: 2023-01-15 14:49
 **/
@SpringBootApplication
@ComponentScan("com.chai")
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.chai")
public class ServiceHospApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServiceHospApplication.class, args);

        System.out.println("=======================================");
        System.out.println("http://localhost:8201/swagger-ui.html");
        System.out.println("=======================================");
    }
}
