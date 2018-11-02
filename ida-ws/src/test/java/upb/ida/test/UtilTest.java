package upb.ida.test;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

import upb.ida.util.FileUtil;
import upb.ida.util.StringLengthComparator;

public class UtilTest {

	public static final String STR1 = "medium str";
	public static final String STR2 = "longest string";
	public static final String STR3 = "smlstr";

	@Test
	public void testLenComparator() {
		List<String> arrList = new ArrayList<>();
		arrList.add(STR1);
		arrList.add(STR3);
		arrList.add(STR2);

		Collections.sort(arrList, new StringLengthComparator());

		assertEquals(arrList.get(0), STR3);
		assertEquals(arrList.get(1), STR1);
		assertEquals(arrList.get(2), STR2);

		try {
			FileUtil fileUtil = new FileUtil();
			String filePath = fileUtil.fetchSysFilePath("./dummy.txt");
			BufferedReader reader = new BufferedReader(new FileReader(filePath));
			for (String line = reader.readLine(); line != null; line = reader.readLine()) {
				System.out.println(line);
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
