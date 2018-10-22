package mypackage;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;



public class App {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ApplicationContext context = new ClassPathXmlApplicationContext("NewFile.xml");

		Customer cust = (Customer) context.getBean("parchod");
		System.out.println(cust);

	}

}
