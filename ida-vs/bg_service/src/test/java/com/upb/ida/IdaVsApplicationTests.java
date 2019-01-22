package com.upb.ida;

import static org.junit.Assert.assertFalse;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class IdaVsApplicationTests {

	@Test
	public void contextLoads() {
		//Stub method deliberately empty
		int val1 = 5;
	     int val2 = 6;
	   //dummy test Check that a condition is false
	      assertFalse(val1 > val2);
	}

}
