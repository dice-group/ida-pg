package upb.ida.test;
import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
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
import upb.ida.util.FileUtil;
@RunWith(SpringRunner.class)
@WebMvcTest
@ContextConfiguration(classes = {Application.class})

public class FileUtilTest {
	
	@Autowired
	private FileUtil dem;
	@Autowired
	private FileUtil DemoMain;
	
	
	@Test
	public void fetchSysFilePathTest() throws JsonProcessingException, IOException
	{
		String filePath= "dataset/city/movehubcostoflivingtest.csv";
		File file = new File(dem.fetchSysFilePath(filePath));
		List<Map<String, String>> dataMapList = dem.convertToMap(file);
		//System.out.println(dataMapList);
		Map<String,String> firstrow = new HashMap<String,String>();
		firstrow.put("City", "Lausanne");
		firstrow.put("Cappuccino", "3.15");
		firstrow.put("Cinema", "12.59");
		firstrow.put("Wine", "8.4");
		firstrow.put("Gasoline", "1.32");
		firstrow.put("Avg Rent", "1714");
		firstrow.put("Avg Disposable Income", "5000.17");
		Map<String,String> secondrow = new HashMap<String,String>();
		secondrow.put("City", "1");
		secondrow.put("Cappuccino", "3.28");
		secondrow.put("Cinema", "12.59");
		secondrow.put("Wine", "8.4");
		secondrow.put("Gasoline", "1.31");
		secondrow.put("Avg Rent", "2378.61");
		secondrow.put("Avg Disposable Income", "4197.55");
		Map<String,String> thirdrow = new HashMap<String,String>();
		thirdrow.put("City", "Geneva");
		thirdrow.put("Cappuccino", "2.8");
		thirdrow.put("Cinema", "12.94");
		thirdrow.put("Wine", "10.49");
		thirdrow.put("Gasoline", "1.28");
		thirdrow.put("Avg Rent", "2607.95");
		thirdrow.put("Avg Disposable Income", "3917.72");
		List<Object> expected = new ArrayList<Object>(); 
			expected.add(firstrow);
			expected.add(secondrow);
			expected.add(thirdrow);
	    
		
	    
	    assertEquals(dataMapList.size(),expected.size());
	    
	    //boolean testPassed = expected.containsAll(dataMapList);
	    
		
		
		
		
	}
	
	@Test
	
	public void getDTFilePathTest() throws JsonProcessingException, IOException
	{
		String path = DemoMain.getDTFilePath("city","citydistancetest.csv" );
		File file = new File(dem.fetchSysFilePath(path));
		List<Map<String, String>> dataMapList = dem.convertToMap(file);
		//System.out.println(dataMapList);
		Map<String, String> expect0 = new HashMap<String, String>();
		expect0.put("city1","Berlin");
		expect0.put("city2","Buenos Aires");
		expect0.put("distance","7402");
		Map<String, String> expect1 = new HashMap<String, String>();
		expect1.put("city1","Berlin");
		expect1.put("city2","Cairo");
		expect1.put("distance","1795");
		List<Object> expected = new ArrayList<Object>();
		expected.add(expect0);
		expected.add(expect1);
		assertEquals(dataMapList,expected);
	       
	    
		
	}
	

}
