package com.xixi;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@MapperScan("com.xixi.module.*.mapper")
@EnableAsync
public class XixiApplication {
    public static void main(String[] args) {
        SpringApplication.run(XixiApplication.class, args);
    }
}
