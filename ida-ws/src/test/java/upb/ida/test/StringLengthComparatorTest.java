package upb.ida.test;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import upb.ida.Application;
import upb.ida.util.StringLengthComparator;
@RunWith(SpringRunner.class)
@WebMvcTest
@ContextConfiguration(classes = {Application.class})
public class StringLengthComparatorTest {
	@Autowired
	private StringLengthComparator lengthComparator;
	
	public static final String STR1 = "medium str";
	public static final String STR2 = "longest string";
	public static final String STR3 = "smlstr";
	
	@Test
	public void strLenCompTest() {
		List<String> arrList = new ArrayList<>();
		arrList.add(STR1);
		arrList.add(STR3);
		arrList.add(STR2);
		
		Collections.sort(arrList, lengthComparator);
		
		assertEquals(arrList.get(0), STR3);
		assertEquals(arrList.get(1), STR1);
		assertEquals(arrList.get(2), STR2);
	}

}
