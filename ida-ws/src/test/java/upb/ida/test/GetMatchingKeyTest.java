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
import com.sun.glass.ui.Application;

import upb.ida.util.BarGraphUtil;
import upb.ida.util.FileUtil;

@RunWith(SpringRunner.class)
@WebMvcTest
@ContextConfiguration(classes = { Application.class })

public class GetMatchingKeyTest {

	@Autowired
	BarGraphUtil barUtil;
	FileUtil fileutil;

	@Test
	public void getMatchingKeysTest() throws JsonProcessingException, IOException {

		File flower = new File(fileutil.fetchSysFilePath("FlowersTest.csv"));
		List<Map<String, String>> actual = fileutil.convertToMap(flower);
		//Map<String, String> actualMap = actual.get(0);

		List<Map<String, String>> expected = new ArrayList<>();

		HashMap<String, String> mapE1 = new HashMap<String, String>();
		mapE1.put("name", "andrew");
		mapE1.put("height", "144");
		mapE1.put("weight", "56");
		mapE1.put("sibling", "yes");

		HashMap<String, String> mapE2 = new HashMap<String, String>();
		mapE2.put("name", "dave");
		mapE2.put("height", "154");
		mapE2.put("weight", "23");
		mapE2.put("sibling", "no");

		HashMap<String, String> mapE3 = new HashMap<String, String>();
		mapE3.put("name", "lisa");
		mapE3.put("height", "165");
		mapE3.put("weight", "46");
		mapE3.put("sibling", "yes");

		expected.add(mapE1);
		expected.add(mapE2);
		expected.add(mapE3);

		for (int i = 0; i < actual.size(); i++) {

			Map<String, String> aMap = actual.get(i);
			Map<String, String> bMap = expected.get(i);

			//Set<String> aKeys = aMap.keySet();
			Set<String> bKeys = bMap.keySet();
			
			for(String key : bKeys) {
				
				String actualKey = barUtil.getMatchingKey(key, aMap);
				assertEquals(key, actualKey);
			}
			
//			String eKey1 = "name";
//			String eKey2 = "weight";
//			String eKey3 = "height";
//			String eKey4 = "sibling";
//
//			String actualKey1 = barUtil.getMatchingKey(eKey1, aMap);
//			String actualKey2 = barUtil.getMatchingKey(eKey2, aMap);
//			String actualKey3 = barUtil.getMatchingKey(eKey3, aMap);
//			String actualKey4 = barUtil.getMatchingKey(eKey4, aMap);
//
//			assertEquals(eKey1, actualKey1);
//			assertEquals(eKey2, actualKey2);
//			assertEquals(eKey3, actualKey3);
//			assertEquals(eKey4, actualKey4);
		}
	}	
}