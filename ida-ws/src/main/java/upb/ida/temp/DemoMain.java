package upb.ida.temp;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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

/**
 * Class to expose util methods for File based operations in IDA
 * 
 * @author Nikit
 *
 */
@Component
public class DemoMain {

	public static Map<String, String> dsPathMap;

	static {
		dsPathMap = new HashMap<String, String>();
		dsPathMap.put("city", "/city");
		dsPathMap.put("movie", "/movie");
	}
	/**
	 * current context instance of SpringContext
	 */
	@Autowired
	private ServletContext context;
	/**
	 * Method to generate a json string from a csv file
	 * @param input - csv file
	 * @return - json string
	 * @throws JsonProcessingException
	 * @throws IOException
	 */
	public String printJson(File input) throws JsonProcessingException, IOException {

		CsvSchema csvSchema = CsvSchema.builder().setUseHeader(true).build();
		CsvMapper csvMapper = new CsvMapper();

		// Read data from CSV file
		List<Object> readAll = csvMapper.readerFor(Map.class).with(csvSchema).readValues(input).readAll();

		ObjectMapper mapper = new ObjectMapper();

		// Write JSON formated data to stdout
		return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(readAll);
	}
	/**
	 * Method to generate a collection of rows from a csv file in List<Map<String, String>> format
	 * @param input - csv file
	 * @return - collection of rows in List<Map<String, String>> format 
	 * @throws JsonProcessingException
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, String>> convertToMap(File input) throws JsonProcessingException, IOException {

		CsvSchema csvSchema = CsvSchema.builder().setUseHeader(true).build();
		CsvMapper csvMapper = new CsvMapper();

		// Read data from CSV file
		List<Object> readAll = csvMapper.readerFor(Map.class).with(csvSchema).readValues(input).readAll();
		List<Map<String, String>> resMapList = new ArrayList<>();
		for (Object entry : readAll) {
			resMapList.add((Map<String, String>) entry);
		}
		return resMapList;
	}
	/**
	 * Method to generate a dataset map for a given dataset
	 * @param keyword - name of dataset
	 * @return - dataset map
	 * @throws JsonProcessingException
	 * @throws IOException
	 */
	public Map<String, String> getDatasetContent(String keyword) throws JsonProcessingException, IOException {
		Map<String, String> resMap = new HashMap<String, String>();
		String path = dsPathMap.get(keyword.toLowerCase());
		if (path != null) {
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
	/**
	 * Method to check if given dataset exists
	 * @param keyword - name of dataset
	 * @return - if dataset exists
	 */
	public static boolean datasetExists(String keyword) {
		return dsPathMap.get(keyword.toLowerCase()) != null;
	}
	/**
	 * Method to fetch the filePath of a given datable
	 * @param actvDs - active dataset
	 * @param actvTbl - active datatable name
	 * @return - file path of the datatable
	 */
	public String getFilePath(String actvDs, String actvTbl) {
		String path = null;
		String dir = dsPathMap.get(actvDs.toLowerCase());
		if (dir != null) {
			path = dir + "/" + actvTbl;
		}
		return path;
	}

	public static int sumNum(int a, int b) {
		return a + b;

	}

}
