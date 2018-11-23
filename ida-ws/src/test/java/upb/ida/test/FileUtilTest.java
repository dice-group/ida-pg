package upb.ida.test;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

import upb.ida.constant.IDALiteral;

import upb.ida.Application;
import upb.ida.util.DataDumpUtil;
import upb.ida.util.FileUtil;
@RunWith(SpringRunner.class)
@WebMvcTest
@ContextConfiguration(classes = {Application.class})

public class FileUtilTest {
	
	@Autowired
	private FileUtil dem;
	
	
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
		List<Object> expected = new ArrayList<Object>() {{
	        add(firstrow);
	        add(secondrow);
	        add(thirdrow);
	    }};
		
	    
	    assertEquals(dataMapList.size(),expected.size());
	    
	    boolean testPassed = expected.containsAll(dataMapList);
	    
		
		
		
		
	}
	

}
