package com.mynt.parcel.parcelmynt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class ParcelMyntApplication {

	public static void main(String[] args) {
		SpringApplication.run(ParcelMyntApplication.class, args);
	}

}
