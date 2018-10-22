package com.java2novice;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SpringDemo {
	public static void main(String a[]) {
		String confFile = "applicationContext.xml";
		ConfigurableApplicationContext context = new ClassPathXmlApplicationContext(confFile);
		PaymentGateway paymentGateway = (PaymentGateway) context.getBean(PaymentGateway.class);
		System.out.println(paymentGateway.toString());
	}
}
