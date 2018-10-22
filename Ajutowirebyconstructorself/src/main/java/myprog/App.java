package myprog;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class App {

	public static void main(String[] args) {
	
		ApplicationContext context = new ClassPathXmlApplicationContext("NewFile.xml");
		
		Developer dev= (Developer) context.getBean("efg");
		System.out.println(dev);
	}
}
