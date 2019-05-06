package upb.ida.test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
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
	public void fetchNegTest() throws JsonProcessingException, IOException
	{
		String filePath= "dataset/city/movehubcostofliving.csv";
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
	    
	    assertNotEquals(dataMapList.size(),expected.size());
	    
	    //boolean testPassed = expected.containsAll(dataMapList);
	    
		
		
		
		
	}
	
	
	
	@Test
	public void fetchPosTest() throws JsonProcessingException, IOException
	{
		String filePath= "dataset/city/movehubcostofliving.csv";
		File file = new File(dem.fetchSysFilePath(filePath));
		List<Map<String, String>> dataMapList = dem.convertToMap(file);
		System.out.println(dataMapList.get(0));
		Map<String,String> firstrow = new LinkedHashMap<String,String>();
		firstrow.put("City", "Lausanne");
		firstrow.put("Cappuccino", "3.15");
		firstrow.put("Cinema", "12.59");
		firstrow.put("Wine", "8.4");
		firstrow.put("Gasoline", "1.32");
		firstrow.put("Avg Rent", "1714");
		firstrow.put("Avg Disposable Income", "4266.11");
		Map<String,String> secondrow = new LinkedHashMap<String,String>();
		secondrow.put("City", "Zurich");
		secondrow.put("Cappuccino", "3.28");
		secondrow.put("Cinema", "12.59");
		secondrow.put("Wine", "8.4");
		secondrow.put("Gasoline", "1.31");
		secondrow.put("Avg Rent", "2378.61");
		secondrow.put("Avg Disposable Income", "4197.55");
		Map<String,String> thirdrow = new LinkedHashMap<String,String>();
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
			
			
	    
	     assertEquals(dataMapList.get(0),expected.get(0));
	     assertEquals(dataMapList.get(1),expected.get(1));
	     assertEquals(dataMapList.get(2),expected.get(2));
			
	    
	    //boolean testPassed = expected.containsAll(dataMapList);
	    
		
		
		
		
	}
	

	@Test
	public void fetchExtTest() throws JsonProcessingException, IOException
	{
		String filePath= "dataset/city/movehubcostofliving.csv";
		File file = new File(dem.fetchSysFilePath(filePath));
		List<Map<String, String>> dataMapList = dem.convertToMap(file);
		//System.out.println(dataMapList);
		Map<String,String> firstrow = new LinkedHashMap<String,String>();
		firstrow.put("City", "Lausanne");
		firstrow.put("Cappuccino", "3.15");
		firstrow.put("Cinema", "12.59");
		firstrow.put("We", "8.4");
		firstrow.put("Gasoline", "1.32");
		firstrow.put("Avg Rent", "1714");
		firstrow.put("Avg Disposable Income", "4266.11");
		Map<String,String> secondrow = new LinkedHashMap<String,String>();
		secondrow.put("City", "Zurich");
		secondrow.put("Cappuccino", "3.28");
		secondrow.put("Cinema", "12.59");
		secondrow.put("Wie", "8.4");
		secondrow.put("Gasoline", "1.31");
		secondrow.put("Avg Rent", "2378.61");
		secondrow.put("Avg Disposable Income", "4197.55");
		Map<String,String> thirdrow = new LinkedHashMap<String,String>();
		thirdrow.put("City", "Geneva");
		thirdrow.put("Cappuccino", "2.8");
		thirdrow.put("Cinema", "12.94");
		thirdrow.put("Wine", "10.4");
		thirdrow.put("Gasoline", "1.28");
		thirdrow.put("Avg Rent", "2607.95");
		thirdrow.put("Avg Disposable Income", "3917.72");
		
		List<Object> expected = new ArrayList<Object>(); 
		expected.add(firstrow);
			expected.add(secondrow);
			expected.add(thirdrow);
			
			
	    
	     assertNotEquals(dataMapList.get(0),expected.get(0));
	    assertNotEquals(dataMapList.get(1),expected.get(1));
			
			
	    
	    //boolean testPassed = expected.containsAll(dataMapList);
	    
		
		
		
		
	}
	
	@Test
	public void getDTFilePathPosTest() throws JsonProcessingException, IOException
	{
		String path = DemoMain.getDTFilePath("city","citydistancetest.csv" );
		File file = new File(dem.fetchSysFilePath(path));
		List<Map<String, String>> dataMapList = dem.convertToMap(file);
		//System.out.println(dataMapList);
		Map<String, String> expect0 = new HashMap<String, String>();
		expect0.put("city1","Berlin");
		expect0.put("city2","Buenos Aires");
		expect0.put("distance","7402");
		//System.out.println(expect0);
		Map<String, String> expect1 = new HashMap<String, String>();
		expect1.put("city1","Berlin");
		expect1.put("city2","Cairo");
		expect1.put("distance","1795");
		//System.out.println(expect1);
		Map<String, String> expect2 = new HashMap<String, String>();
		expect2.put("city1","Buenos Aires");
		expect2.put("city2","Cairo");
		expect2.put("distance","7345");
		//System.out.println(expect2);
		List<Object> exp = new ArrayList<Object>();
		exp.add(expect0);
		exp.add(expect1);
		exp.add(expect2);
		//System.out.println(exp);
		assertEquals(dataMapList,exp);
	       


	}


	@Test
	public void getDTFilePathExtTest() throws JsonProcessingException, IOException
	{
		String path = DemoMain.getDTFilePath("city","citydistancetest.csv" );
		File file = new File(dem.fetchSysFilePath(path));
		List<Map<String, String>> dataMapList = dem.convertToMap(file);
		//System.out.println(dataMapList);
		Map<String, String> expect0 = new HashMap<String, String>();
		expect0.put("city1","lin");
		expect0.put("city2","Buenos Aires");
		expect0.put("distance","7402");
		//System.out.println(expect0);
		Map<String, String> expect1 = new HashMap<String, String>();
		expect1.put("city1","Berlin");
		expect1.put("city2","Cairo");
		expect1.put("distance","1795");
		//System.out.println(expect1);
		Map<String, String> expect2 = new HashMap<String, String>();
		expect2.put("city1","Buenos Aires");
		expect2.put("city2","Cairo");
		expect2.put("distance","7345");
		//System.out.println(expect2);
		List<Object> exp = new ArrayList<Object>();
		exp.add(expect0);
		exp.add(expect1);
		exp.add(expect2);
		//System.out.println(exp);
		assertEquals(dataMapList.size(),exp.size());
		assertNotEquals(dataMapList,exp);
	}
	
	
	@Test
	public void getDTFilePathNegTest() throws JsonProcessingException, IOException
	{
		String path = DemoMain.getDTFilePath("city","citydistancetest.csv" );
		File file = new File(dem.fetchSysFilePath(path));
		List<Map<String, String>> dataMapList = dem.convertToMap(file);
		//System.out.println(dataMapList);
		Map<String, String> expect0 = new HashMap<String, String>();
		expect0.put("city1","lin");
		expect0.put("city2","Buenos Aires");
		expect0.put("distance","7402");
		//System.out.println(expect0);
		Map<String, String> expect1 = new HashMap<String, String>();
		expect1.put("city1","Berlin");
		expect1.put("city2","Cairo");
		expect1.put("distance","1795");
		//System.out.println(expect1);
		
		//System.out.println(expect2);
		List<Object> exp = new ArrayList<Object>();
		exp.add(expect0);
		exp.add(expect1);
		
		//System.out.println(exp);
		assertNotEquals(dataMapList.size(),exp.size());
		assertNotEquals(dataMapList,exp);
	       
	    
		
	}
	

}
