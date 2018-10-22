package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		
		// Initializing the Bean class
		ConfigurableApplicationContext context = SpringApplication.run(DemoApplication.class, args);
		
		// calling the bean class of intended class type
		Alien a= context.getBean(Alien.class);
        a.show();
		
	
		
	}
}
