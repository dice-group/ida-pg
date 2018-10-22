package com.journaldev.spring.autowiring.main;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;



public class Test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ApplicationContext ctx = new ClassPathXmlApplicationContext(
				"classpath:com\\journaldev\\spring\\autowiring\\main\\Beans.xml");
		Employee employee = ctx.getBean("employee", Employee.class);
		if (employee != null) {
			System.out.println(employee.getEmployeeId() + "" + employee.getEmployeeName() + "" + employee.getEmail());
			Pancard pancard = employee.getPancard();
			if (pancard != null)
				System.out.println(pancard.getPanHolderName() + "\t" + pancard.getPanno() + "");
		}
		((AbstractApplicationContext) ctx).close();

	}

}
