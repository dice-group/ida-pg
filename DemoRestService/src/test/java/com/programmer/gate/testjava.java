package com.programmer.gate;

import static org.junit.Assert.*;

import org.junit.Test;

public class testjava {

	@Test
	public void test() {
		DemoController obj = new DemoController();
		int result= obj.sum(25,3,3,7);
		assertEquals(22, result);
	}

}
