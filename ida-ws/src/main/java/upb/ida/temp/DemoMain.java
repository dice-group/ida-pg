package upb.ida.temp;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
@Component
public class DemoMain {
	
	public static Map<String, String> dsPathMap;
	
	static {
		dsPathMap = new HashMap<String, String>();
		dsPathMap.put("city", "/city");
		dsPathMap.put("movie", "/movie");
	}
	@Autowired
	ServletContext context;
	public static void main(String[] args) throws Exception {
		
	}
	
	public String printJson(File input) throws JsonProcessingException, IOException {

		CsvSchema csvSchema = CsvSchema.builder().setUseHeader(true).build();
		CsvMapper csvMapper = new CsvMapper();

		// Read data from CSV file
		List<Object> readAll = csvMapper.readerFor(Map.class).with(csvSchema).readValues(input).readAll();

		ObjectMapper mapper = new ObjectMapper();

		// Write JSON formated data to stdout
		return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(readAll);
	}
	
	public Map<String, String> getDatasetContent(String keyword) throws JsonProcessingException, IOException{
		Map<String, String> resMap = new HashMap<String, String>();
		String path = dsPathMap.get(keyword.toLowerCase());
		if(path!=null) {
			 File dir = new File(context.getRealPath(path));
			  File[] directoryListing = dir.listFiles();
			  if (directoryListing != null) {
			    for (File child : directoryListing) {
			      // Do something with child
			    	resMap.put(child.getName(), printJson(child));
			    }
			  }
		}
		return resMap;
	}

}