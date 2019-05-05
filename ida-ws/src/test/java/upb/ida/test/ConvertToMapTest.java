package upb.ida.test;

import static org.junit.Assert.assertEquals;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import com.fasterxml.jackson.core.JsonProcessingException;

import upb.ida.Application;
import upb.ida.util.FileUtil;

@RunWith(SpringRunner.class)
@WebMvcTest
@ContextConfiguration(classes = { Application.class })

public class ConvertToMapTest {
	@Autowired
	private FileUtil fileutil;

	@Test
	public void convertMaptest() throws JsonProcessingException, IOException {

		File flower = new File(fileutil.fetchSysFilePath("FlowersTest.csv"));
		List<Map<String, String>> actual = fileutil.convertToMap(flower);
		// System.out.println(actual);
		List<Map<String, String>> expected = new ArrayList<>();

		HashMap<String, String> map1 = new HashMap<String, String>();
		map1.put("name", "sunflower");
		map1.put("quantity", "20");
		map1.put("origin", "india");

		HashMap<String, String> map2 = new HashMap<String, String>();
		map2.put("name", "rose");
		map2.put("quantity", "45");
		map2.put("origin", "dubai");

		HashMap<String, String> map3 = new HashMap<String, String>();
		map3.put("name", "orchids");
		map3.put("quantity", "34");
		map3.put("origin", "austria");

		expected.add(map1);
		expected.add(map2);
		expected.add(map3);

		// check the size of actual and expected list of maps

		int actListSize = actual.size();
		int expListSize = expected.size();
		assertEquals(actListSize, expListSize);

		for (int i = 0; i < actual.size(); i++) {

			Map<String, String> aMap = actual.get(i);
			Map<String, String> bMap = expected.get(i);
			//System.out.println(aMap);
			//System.out.println(bMap);

			boolean result = compareMaps(aMap, bMap);
			//System.out.println("final" + result);
			assertTrue(result);
		}
	}

	public static boolean compareMaps(Map<String, String> aMap, Map<String, String> bMap) {
		boolean res = true;

		int aMapSize = aMap.size();
		int bMapSize = bMap.size();
		res = aMapSize == bMapSize;

		if (res) {

			Set<String> aKeys = aMap.keySet();
			Set<String> bKeys = bMap.keySet();

			boolean leftCheck = aKeys.containsAll(bKeys);
			boolean rightCheck = bKeys.containsAll(aKeys);
			res = leftCheck && rightCheck;
			//System.out.println(res + "leftright");

			// if (res) {
			//
			// for (String key : aKeys) {
			//
			// String aKey = aMap.get(key);
			// //System.out.println(aKey);
			// String bKey = bMap.get(key);
			// //System.out.println(bKey);
			// res = aKey.equals(bKey);
			// //System.out.println(res + "keyssame");
			// if (res == false) {
			// break;
			// }

			// }
			// }
		}
		//System.out.println(res + "last but one");
		return res;
	}
}
