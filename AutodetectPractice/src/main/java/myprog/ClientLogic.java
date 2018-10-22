package myprog;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ClientLogic {

	public static void main(String[] args) {
		
		ApplicationContext context = new ClassPathXmlApplicationContext("NewFile.xml");
		Catagories a= (Catagories) context.getBean("id1");
		a.show();
		

	}

}
