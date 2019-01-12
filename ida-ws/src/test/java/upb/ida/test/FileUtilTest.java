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
		//System.out.println(dataMapList);
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
		Map<String,String> forthrow = new LinkedHashMap<String,String>();
		forthrow.put("City", "Basel");
		forthrow.put("Cappuccino", "3.5");
		forthrow.put("Cinema", "11.89");
		forthrow.put("Wine", "7.35");
		forthrow.put("Gasoline", "1.25");
		forthrow.put("Avg Rent", "1649.29");
		forthrow.put("Avg Disposable Income", "3847.76");
		Map<String,String> fifthrow = new LinkedHashMap<String,String>();
		fifthrow.put("City", "Perth");
		fifthrow.put("Cappuccino", "2.87");
		fifthrow.put("Cinema", "11.43");
		fifthrow.put("Wine", "10.08");
		fifthrow.put("Gasoline", "0.97");
		fifthrow.put("Avg Rent", "2083.14");
		fifthrow.put("Avg Disposable Income", "3358.55");
		Map<String,String> sixthrow = new LinkedHashMap<String,String>();
		sixthrow.put("City", "Nashville");
		sixthrow.put("Cappuccino", "3.84");
		sixthrow.put("Cinema", "12");
		sixthrow.put("Wine", "13.5");
		sixthrow.put("Gasoline", "0.65");
		sixthrow.put("Avg Rent", "2257.14");
		sixthrow.put("Avg Disposable Income", "3089.75");
		Map<String,String> seventhrow = new LinkedHashMap<String,String>();
		seventhrow.put("City", "Canberra");
		seventhrow.put("Cappuccino", "2.35");
		seventhrow.put("Cinema", "11.42");
		seventhrow.put("Wine", "10.08");
		seventhrow.put("Gasoline", "0.99");
		seventhrow.put("Avg Rent", "1984.74");
		seventhrow.put("Avg Disposable Income", "3023.91");
		Map<String,String> eightrow = new LinkedHashMap<String,String>();
		eightrow.put("City", "Bergen");
		eightrow.put("Cappuccino", "3.92");
		eightrow.put("Cinema", "12.32");
		eightrow.put("Wine", "12.32");
		eightrow.put("Gasoline", "1.57");
		eightrow.put("Avg Rent", "1725.37");
		eightrow.put("Avg Disposable Income", "3002.59");
		Map<String,String> ninthrow = new LinkedHashMap<String,String>();
		ninthrow.put("City", "Luxembourg");
		ninthrow.put("Cappuccino", "2.13");
		ninthrow.put("Cinema", "7.25");
		ninthrow.put("Wine", "4.26");
		ninthrow.put("Gasoline", "1.15");
		ninthrow.put("Avg Rent", "1704.96");
		ninthrow.put("Avg Disposable Income", "2983.69");
		Map<String,String> tenthrow = new LinkedHashMap<String,String>();
		tenthrow.put("City", "Stavanger");
		tenthrow.put("Cappuccino", "4.48");
		tenthrow.put("Cinema", "10.65");
		tenthrow.put("Wine", "13.44");
		tenthrow.put("Gasoline", "1.68");
		tenthrow.put("Avg Rent", "2240.74");
		tenthrow.put("Avg Disposable Income", "2957.77");
		Map<String,String> elevenrow = new LinkedHashMap<String,String>();
		elevenrow.put("City", "Adelaide");
		elevenrow.put("Cappuccino", "2.49");
		elevenrow.put("Cinema", "11.42");
		elevenrow.put("Wine", "10.08");
		elevenrow.put("Gasoline", "0.95");
		elevenrow.put("Avg Rent", "1382.26");
		elevenrow.put("Avg Disposable Income", "2911.69");
		List<Object> expected = new ArrayList<Object>(); 
		expected.add(firstrow);
			expected.add(secondrow);
			expected.add(thirdrow);
			expected.add(forthrow);
			expected.add(fifthrow);
			expected.add(sixthrow);
			expected.add(seventhrow);
			expected.add(eightrow);
			expected.add(ninthrow);
			expected.add(tenthrow);
			expected.add(elevenrow);
			
	    
	     assertEquals(dataMapList,expected);
			
			
	    
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
		firstrow.put("Wine", "8.4");
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
		Map<String,String> forthrow = new LinkedHashMap<String,String>();
		forthrow.put("City", "Basel");
		forthrow.put("Cappuccino", "3.5");
		forthrow.put("Cinema", "11.89");
		forthrow.put("Wine", "7.35");
		forthrow.put("Gasoline", "1.25");
		forthrow.put("Avg Rent", "1649.29");
		forthrow.put("Avg Disposable Income", "3847.76");
		Map<String,String> fifthrow = new LinkedHashMap<String,String>();
		fifthrow.put("City", "Perth");
		fifthrow.put("Cappuccino", "2.87");
		fifthrow.put("Cinema", "11.43");
		fifthrow.put("Wine", "10.08");
		fifthrow.put("Gasoline", "0.97");
		fifthrow.put("Avg Rent", "2083.14");
		fifthrow.put("Avg Disposable Income", "3358.55");
		Map<String,String> sixthrow = new LinkedHashMap<String,String>();
		sixthrow.put("City", "Nashville");
		sixthrow.put("Cappuccino", "3.84");
		sixthrow.put("Cinema", "12");
		sixthrow.put("Wine", "13.5");
		sixthrow.put("Gasoline", "0.65");
		sixthrow.put("Avg Rent", "2257.14");
		sixthrow.put("Avg Disposable Income", "3089.75");
		Map<String,String> seventhrow = new LinkedHashMap<String,String>();
		seventhrow.put("City", "Canberra");
		seventhrow.put("Cappuccino", "2.35");
		seventhrow.put("Cinema", "11.42");
		seventhrow.put("Wine", "10.08");
		seventhrow.put("Gasoline", "0.99");
		seventhrow.put("Avg Rent", "1984.74");
		seventhrow.put("Avg Disposable Income", "3023.91");
		Map<String,String> eightrow = new LinkedHashMap<String,String>();
		eightrow.put("City", "Bergen");
		eightrow.put("Cappuccino", "3.92");
		eightrow.put("Cinema", "12.32");
		eightrow.put("Wine", "12.32");
		eightrow.put("Gasoline", "1.57");
		eightrow.put("Avg Rent", "1725.37");
		eightrow.put("Avg Disposable Income", "3002.59");
		Map<String,String> ninthrow = new LinkedHashMap<String,String>();
		ninthrow.put("City", "Luxembourg");
		ninthrow.put("Cappuccino", "2.13");
		ninthrow.put("Cinema", "7.25");
		ninthrow.put("Wine", "4.26");
		ninthrow.put("Gasoline", "1.15");
		ninthrow.put("Avg Rent", "1704.96");
		ninthrow.put("Avg Disposable Income", "2983.69");
		Map<String,String> tenthrow = new LinkedHashMap<String,String>();
		tenthrow.put("City", "Stavanger");
		tenthrow.put("Cappuccino", "4.48");
		tenthrow.put("Cinema", "10.65");
		tenthrow.put("Wine", "13.44");
		tenthrow.put("Gasoline", "1.68");
		tenthrow.put("Avg Rent", "2240.74");
		tenthrow.put("Avg Disposable Income", "2957.77");
		Map<String,String> elevenrow = new LinkedHashMap<String,String>();
		elevenrow.put("City", "Adelaide");
		elevenrow.put("Cappuccino", "2.49");
		elevenrow.put("Cinema", "11.42");
		elevenrow.put("Wine", "10.08");
		elevenrow.put("Gasoline", "0.95");
		elevenrow.put("Avg Rent", "1382.26");
		elevenrow.put("Avg Disposable Income", "2911.69");
		List<Object> expected = new ArrayList<Object>(); 
		expected.add(firstrow);
			expected.add(secondrow);
			expected.add(thirdrow);
			expected.add(forthrow);
			expected.add(fifthrow);
			expected.add(sixthrow);
			expected.add(seventhrow);
			expected.add(eightrow);
			expected.add(ninthrow);
			expected.add(tenthrow);
			expected.add(elevenrow);
			
	    
	     assertNotEquals(dataMapList,expected);
	     assertEquals(dataMapList.size(),expected.size());
			
			
	    
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
