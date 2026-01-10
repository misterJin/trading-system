package com.example.tradingsystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableAsync  // 启用异步支持，用于异步处理领域事件
public class TradingSystemApplication {
    public static void main(String[] args) {
        SpringApplication.run(TradingSystemApplication.class, args);
    }
}
