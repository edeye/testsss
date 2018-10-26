package com.infoland;


import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
@MapperScan("com.infoland.dao")
public class DoorApplication {


    public static void main(String[] args) {
        SpringApplication.run(DoorApplication.class, args);
    }


}
