package upb.ida.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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

import com.fasterxml.jackson.annotation.JacksonInject.Value;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.node.ObjectNode;

import upb.ida.Application;
import upb.ida.util.FileUtil;
@RunWith(SpringRunner.class)
@WebMvcTest
@ContextConfiguration(classes= {Application.class})

public class LoadDatasetTest {
		@Autowired
		FileUtil fileutil;
		
		
		@Test
		public void convertToMapTest() throws JsonProcessingException, IOException {
			
			
			File flower = new File(fileutil.fetchSysFilePath("FlowersTest.csv"));
			List<Map<String, String>> data = fileutil.convertToMap(flower);
			
			Map<String, String> map1 = data.get(0);
			Map<String, String> map2 = data.get(1);
			Map<String, String> map3 = data.get(2);
		
			
			String myKey1 = (String) map1.keySet().toArray()[0];
			String myKey2 = (String) map1.keySet().toArray()[1];
			String myKey3 = (String) map1.keySet().toArray()[2];
			
			assertEquals("name", myKey1);
			assertEquals("quantity", myKey2);
			assertEquals("origin", myKey3);
			
			String map1Value1 = (String) map1.values().toArray()[0];
			String map1Value2 = (String) map1.values().toArray()[1];
			String map1Value3 = (String) map1.values().toArray()[2];
			
			assertEquals("sunflower", map1Value1.trim());
			assertEquals("20", map1Value2.trim());
			assertEquals("india", map1Value3.trim());
			
			String map2Value1 = (String) map2.values().toArray()[0];
			String map2Value2 = (String) map2.values().toArray()[1];
			String map2Value3 = (String) map2.values().toArray()[2];
			
			assertEquals("rose", map2Value1.trim());
			assertEquals("45", map2Value2.trim());
			assertEquals("dubai", map2Value3.trim());
			
			String map3Value1 = (String) map3.values().toArray()[0];
			String map3Value2 = (String) map3.values().toArray()[1];
			String map3Value3 = (String) map3.values().toArray()[2];
			
			assertEquals("orchids", map3Value1.trim());
			assertEquals("34", map3Value2.trim());
			assertEquals("austria", map3Value3.trim());
			
			
			
	    	
		}
}
