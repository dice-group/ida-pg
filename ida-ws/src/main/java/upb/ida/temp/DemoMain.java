package upb.ida.temp;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
<<<<<<< HEAD
import java.io.InputStream;
=======
import java.util.ArrayList;
>>>>>>> a14a58a207a9c1e977ab4afd51bbdbbd9b929db5
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import javax.servlet.ServletContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import upb.ida.util.CSV;
import upb.ida.util.getAxisJson;
import upb.ida.util.jsonMaker;
@Component
public class DemoMain {
	
	public static Map<String, String> dsPathMap;
	
	static {
		dsPathMap = new HashMap<String, String>();
		dsPathMap.put("input", "/input");
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
	
	@SuppressWarnings("unchecked")
	public List<Map<String, String>> convertToMap(File input) throws JsonProcessingException, IOException {

		CsvSchema csvSchema = CsvSchema.builder().setUseHeader(true).build();
		CsvMapper csvMapper = new CsvMapper();

		// Read data from CSV file
		List<Object> readAll = csvMapper.readerFor(Map.class).with(csvSchema).readValues(input).readAll();
		List<Map<String, String>> resMapList = new ArrayList<>();
		for(Object entry : readAll) {
			resMapList.add((Map<String, String>) entry);
		}
		return resMapList;
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

	
	public Object fileCsv(File input,String x,String y) throws JsonProcessingException, IOException {
		
		InputStream in = new FileInputStream(input);
	    jsonMaker lst= new jsonMaker(in);
        List <Map< String, String >> lstt = lst.jsonObject(in);
        getAxisJson jsn= new getAxisJson(x,y);

        Object p[];
        p= jsn.NewJsonObjct(x,y,lstt);

		return p;
	}
	public Map<String, 	Object> getJsonData(String keyword,String x, String y) throws JsonProcessingException, IOException{
	
      //  File output = new File("C:\\Users\\Faisal Mahmood\\Desktop\\dice-ida\\ida-ws\\src\\main\\java\\upb\\ida\\util\\input.csv");
        
       // InputStream in = new FileInputStream(output);
        //InputStream in = new ByteArrayInputStream(output.getBytes("UTF-8"));

        String xy =keyword;
		Map<String, Object> resMap = new HashMap<String, Object>();
		String path = dsPathMap.get(keyword.toLowerCase());
		if(path!=null) {
			 File dir = new File(context.getRealPath(path));
			  File[] directoryListing = dir.listFiles();
			  if (directoryListing != null) {
			    for (File child : directoryListing) {
			      // Do something with child
//			    	resMap.put("x_axis", x);
//			    	resMap.put("y_axis", y);
			    	resMap.put(child.getName(), fileCsv(child,x,y));
			    }
			  }
		}
		return resMap;
	}
	public static int sumNum(int a, int b) {
		return a+b;

	}

}
