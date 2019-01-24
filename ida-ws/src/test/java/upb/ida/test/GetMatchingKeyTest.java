package upb.ida.test;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import com.fasterxml.jackson.core.JsonProcessingException;

import upb.ida.Application;
import upb.ida.util.BarGraphUtil;
import upb.ida.util.FileUtil;

@RunWith(SpringRunner.class)
@WebMvcTest
@ContextConfiguration(classes = { Application.class })
public class GetMatchingKeyTest {
	@Autowired
	FileUtil fileutil;
	@Autowired
	BarGraphUtil barUtil;


	@Test
	public void getMatchingKeysTest() throws JsonProcessingException, IOException {
		

		File flower = new File(fileutil.fetchSysFilePath("FlowersTest.csv"));
		List<Map<String, String>> actual = fileutil.convertToMap(flower);
		
		Map<String, String> actualMap = actual.get(0);
		

			String eKey1 = "name";
			String eKey2 = "quantity";
			String eKey3 = "origin";
			
			String actualKey1 = barUtil.getMatchingKey(eKey1, actualMap);
			String actualKey2 = barUtil.getMatchingKey(eKey2, actualMap);
			String actualKey3 = barUtil.getMatchingKey(eKey3, actualMap);

			assertEquals(eKey1, actualKey1);
			assertEquals(eKey2, actualKey2);
			assertEquals(eKey3, actualKey3);

		}
	}	
