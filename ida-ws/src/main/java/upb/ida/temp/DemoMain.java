package upb.ida.temp;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

import upb.ida.bean.cluster.ClusterAlgoDesc;
import upb.ida.util.GetAxisJson;
import upb.ida.util.JsonMaker;

@Component
public class DemoMain {

	public static Map<String, String> dsPathMap;

	static {
		dsPathMap = new HashMap<String, String>();
		dsPathMap.put("city", "/city");
		dsPathMap.put("movie", "/movie");
	}
	@Autowired
	private ServletContext context;
	@Autowired
	@Qualifier("scktClstrDtDmp")
	private Map<String, ClusterAlgoDesc> scktClstrDtDmp;

	public String printJson(File input) throws JsonProcessingException, IOException {

		CsvSchema csvSchema = CsvSchema.builder()
		                               .setUseHeader(true)
		                               .build();
		CsvMapper csvMapper = new CsvMapper();

		// Read data from CSV file
		List<Object> readAll = csvMapper.readerFor(Map.class)
		                                .with(csvSchema)
		                                .readValues(input)
		                                .readAll();

		ObjectMapper mapper = new ObjectMapper();

		// Write JSON formated data to stdout
		return mapper.writerWithDefaultPrettyPrinter()
		             .writeValueAsString(readAll);
	}

	@SuppressWarnings("unchecked")
	public List<Map<String, String>> convertToMap(File input) throws JsonProcessingException, IOException {

		CsvSchema csvSchema = CsvSchema.builder()
		                               .setUseHeader(true)
		                               .build();
		CsvMapper csvMapper = new CsvMapper();

		// Read data from CSV file
		List<Object> readAll = csvMapper.readerFor(Map.class)
		                                .with(csvSchema)
		                                .readValues(input)
		                                .readAll();
		List<Map<String, String>> resMapList = new ArrayList<>();
		for (Object entry : readAll) {
			resMapList.add((Map<String, String>) entry);
		}
		return resMapList;
	}

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

	public static boolean datasetExists(String keyword) {
		return dsPathMap.get(keyword.toLowerCase()) != null;
	}

	public Object fileCsv(File input, String x, String y) throws JsonProcessingException, IOException {

		InputStream in = new FileInputStream(input);
		JsonMaker lst = new JsonMaker();
		List<Map<String, String>> lstt = lst.jsonObject(in);
		GetAxisJson jsn = new GetAxisJson();

        return	jsn.newJsonObjct(x, y, lstt);

	}

	public Map<String, Object> getJsonData(String filepath, String x, String y) throws JsonProcessingException, IOException {

		Map<String, Object> resMap = new HashMap<String, Object>();
		File file = new File(context.getRealPath(filepath));
		if (file != null) {
			// Do something with child
			resMap.put(file.getName(), fileCsv(file, x, y));
		}
		return resMap;
	}

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
