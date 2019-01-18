package upb.ida.test;

import static org.junit.Assert.assertEquals;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
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
@ContextConfiguration(classes = {Application.class})

public class FetchFiltered {

	@Autowired
	FileUtil fileutil;
	BarGraphUtil barutil;
	ListMapsForFilteringCheck filterMaps;
	
	
	@Test
	public void checkFilters() throws JsonProcessingException, IOException, NumberFormatException, ParseException {
		
			File filterTest = new File(fileutil.fetchSysFilePath("FilterTest.csv"));
		List<Map<String, String>> listMapA = fileutil.convertToMap(filterTest);
			String[] topAsc = {"TopN" ,"3", "height", "ascending" };
			String[] topDes =  {"TopN" ,"3", "height", "descending" };
		
		
		List<Map<String,String>> actualHeightAsc = barutil.fetchFilteredData(listMapA, topAsc); 
		List<Map<String,String>> expectedHeightAsc = filterMaps.generateMapsforTopAsc();
		
		List<Map<String,String>> actualHeightDes = barutil.fetchFilteredData(listMapA, topDes); 
		List<Map<String,String>> expectedHeightDes = filterMaps.generateMapsforTopAsc();
		//System.out.println(expected);

	
		assertEquals(expectedHeightAsc, actualHeightAsc);
		assertEquals(expectedHeightDes, actualHeightDes);
		
	}	
}
