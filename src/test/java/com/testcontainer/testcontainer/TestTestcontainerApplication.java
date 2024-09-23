package com.testcontainer.testcontainer;

import org.springframework.boot.SpringApplication;

public class TestTestcontainerApplication {

	public static void main(String[] args) {
		SpringApplication.from(TestcontainerApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
