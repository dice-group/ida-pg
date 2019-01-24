package upb.ida.util;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import upb.ida.bean.FilterOption;
import upb.ida.bean.FilterSort;
import upb.ida.bean.ResponseBean;
import upb.ida.constant.IDALiteral;

/**
 * Exposes util methods for Bargraph visualization
 */
@Component
public class BarGraphUtil {

	public static final String FIRST_N_REC = "FIRSTN";
	public static final String LAST_N_REC = "LASTN";
	public static final String TOP_N_REC = "TOPN";
	public static final String FROM_TO_REC = "FROMTO";

	@Autowired
	private FileUtil demoMain;
	@Autowired
	private FilterUtil filterUtil;

	// To initialize variables with parameterized constructor
	/**
	 * Method to create response for Bar Graph visualization
	 * @param x - string value for x-axis
	 * @param y - string value for y-axis
	 * @param lstt - List of data read from csv file based on x and y axis
	 * @param dataMap - setting values for responseBean payload
	 * @return 
	 * @return - void
	 */
	public  void newJsonObjct(String x, String y, List<Map<String, String>> lstt, Map<String, Object> dataMap)
			throws IOException, NumberFormatException, ParseException {
		System.out.println("Inside newJsonObjct funtion");
		
		ObjectMapper mapper = new ObjectMapper();
		ArrayNode nodeArr1 = mapper.createArrayNode();
		String xKey = getMatchingKey(x, lstt.get(0));
		String yKey = getMatchingKey(y, lstt.get(0));
		List<String> keys = new ArrayList<String>();
		keys.add(xKey);
		// dataMap.put("Label", "BgData");
		dataMap.put("xaxisname", xKey);
		dataMap.put("yaxisname", yKey);
		dataMap.put("keys", keys);
		// ArrayList<String> x_axis = new ArrayList<String>();
		for (int i = 0; i < lstt.size(); i++) {
			HashMap<String, Object> mMap = new HashMap<String, Object>();
			mMap = new HashMap<String, Object>(); // create a new one!
			mMap.put(xKey, lstt.get(i).get(xKey));
			Double StrngthValID = Double.parseDouble(lstt.get(i).get(yKey));
			mMap.put(yKey, StrngthValID);

			nodeArr1.add(mapper.readTree(mapper.writeValueAsString(mMap)));
		}
		dataMap.put("baritems", nodeArr1);
		
	}
	/**
	 * Method to match user x and y axis values 
	 * with that in orignal data read from csv files
	 *  and getting actual values as in file
	 * @param key - string for x-axis and y-axis values
	 * @param dataMap - map containing data from csv files
	 * @return - String returning orignal key if matched
	 */
	public String getMatchingKey(String key, Map<String, String> dataMap) {
		
		System.out.println("Inside getMatchingKey funtion");
		Set<String> keySet = dataMap.keySet();
		String res = null;
		for (String entry : keySet) {
			if (key.trim().equalsIgnoreCase(entry)) {
				res = entry;
				break;
			}
		}
		return res;
	}
	
	/**
	 * Method to get list of map of data for bar graph based on x and y axis given by user
	 * 
	 * @param input - current file for bar graph visualization
	 * @param x - string value for x-axis
	 * @param y - string value for y-axis
	 * @param dataMap - map containing data from csv files
	 * @param args -  arguments of for filtering
	 * @return - void
	 */

	public void fileCsv(File input, String x, String y, Map<String, Object> dataMap, String[] args)
			throws JsonProcessingException, IOException, NumberFormatException, ParseException {
		System.out.println("Inside fileCsv funtion");
		List<Map<String, String>> lstt = demoMain.convertToMap(input);
		// Filter data
		lstt = fetchFilteredData(lstt, args);
		newJsonObjct(x, y, lstt, dataMap);

	}
	/**
	 * Method to get file from repository and call fileCsv function
	 *  with real path of file for bar graph visualization
	 * @param filepath - current file's name
	 * @param x - string value for x-axis
	 * @param y - string value for y-axis
	 * @param dataMap - map containing data from csv files
	 * @param args -  arguments of for filtering
	 * @return - void
	 */
	public void getJsonData(String filepath, String x, String y, Map<String, Object> dataMap, String args[])
			throws JsonProcessingException, IOException, NumberFormatException, ParseException {
		System.out.println("Inside getJsonData funtion");
		File file = new File(demoMain.fetchSysFilePath(filepath));
		fileCsv(file, x, y, dataMap, args);

	}
	/**
	 * Method to create an instance of FilterOptions for the provided parameters
	 * @param filterType - type of filtering operation
	 * @param params - parameters for the fitlering operation
	 * @return - FilterOption instance
	 */
	public FilterOption getFilterOption(String filterType, String[] params) {
		
		System.out.println("Inside getFilterOption funtion");
		FilterOption filterOption = null;
		if (filterType.equalsIgnoreCase(FIRST_N_REC)) {
			// Get Filter Option for First N records
			Integer n = Integer.parseInt(params[0]);
			filterOption = filterUtil.getFirstNFilterOpt(n);
		} else if (filterType.equalsIgnoreCase(LAST_N_REC)) {
			// Get Filter Option for Last N records
			Integer n = Integer.parseInt(params[0]);
			Integer datasetSize = Integer.parseInt(params[1]);
			filterOption = filterUtil.getLastNFilterOpt(n, datasetSize);
		} else if (filterType.equalsIgnoreCase(TOP_N_REC)) {
			// Get Filter Option for Top N records
			Integer n = Integer.parseInt(params[0]);
			String fieldName = params[1];
			String sortDirStr = params[2];
			Boolean isNumeric = Boolean.parseBoolean(params[3]);
			FilterSort sortDirection = sortDirStr.matches(".*[aA][sS][cC].*") ? FilterSort.ASC : FilterSort.DESC;
			filterOption = filterUtil.getSortedFirstNFilterOpt(fieldName, n, sortDirection, isNumeric);
		} else if (filterType.equalsIgnoreCase(FROM_TO_REC)) {
			// Get Filter Option for particular sequence of records
			Integer fromSeq = Integer.parseInt(params[0]);
			Integer toSeq = Integer.parseInt(params[1]);
			filterOption = filterUtil.getSubDatasetFilterOpt(fromSeq, toSeq);
		}
		return filterOption;
	}
	/**
	 * Method to create response for bar graph visualization request
	 * 
	 * @param input - current file for bar graph visualization
	 * @param args -  arguments of for filtering
	 * @param responseBean -  ResponseBean is used as a uniform response 
	 * format for the incoming REST calls
	 * @return - void
	 */
	public void generateBarGraphData(String[] args, ResponseBean responseBean)
			throws NumberFormatException, JsonProcessingException, IOException, ParseException {

		System.out.println("Inside generateBarGraphData funtion");
		// Fetch active dataset details
		String actvTbl = (String) responseBean.getPayload().get("actvTbl");
		String actvDs = (String) responseBean.getPayload().get("actvDs");
		Map<String, Object> dataMap = new HashMap<String, Object>();
		String path = demoMain.getDTFilePath(actvDs, actvTbl);
		String xaxisname = args[0];
		String yaxisname = args[1];
		String[] filterArgs = new String[args.length - 2];
		for (int i = 2; i < args.length; i++) {
			filterArgs[i - 2] = args[i];
		}
		// set the bargraph in dataMap
		getJsonData(path, xaxisname, yaxisname, dataMap, filterArgs);
		Map<String, Object> submap_data = responseBean.getPayload();
		submap_data.put("bgData", dataMap);
		responseBean.setActnCode(IDALiteral.UIA_BG);
	}
	/**
	 * Method to fetch filtered data for a given dataset and filter arguments
	 * @param data - dataset to perform filtering on
	 * @param args - arguments of for filtering
	 * @return - filtered dataset
	 * @throws NumberFormatException
	 * @throws ParseException
	 */
	public List<Map<String, String>> fetchFilteredData(List<Map<String, String>> data, String[] args)
			throws NumberFormatException, ParseException {
		
	System.out.println("Inside fetchFilteredData funtion");
	//	System.out.println("Input args" + args.toString());
	
		//for (String var: args) {
	//		System.out.println(var);
		//}
		List<Map<String, String>> resList = null;
		String filterType = args[0];
		String[] params = null;
		if (filterType.equalsIgnoreCase(FIRST_N_REC) || filterType.equalsIgnoreCase(LAST_N_REC)) {
			// Process for First/Last N records
			String[] tempParams = { args[1], String.valueOf(data.size()) };
			params = tempParams;
		} else if (filterType.equalsIgnoreCase(TOP_N_REC)) {
			// Perform check for isNumeric
			String fieldName = args[2];
			Map<String, String> sampleEntry = data.get(0);
			String tempKey = getMatchingKey(fieldName, sampleEntry);
			fieldName = tempKey;
			Boolean isNumeric = isNumeric(sampleEntry.get(fieldName));
			// Process for top N records
			String[] tempParams = { args[1], fieldName, args[3], isNumeric.toString() };
			params = tempParams;
		} else if (filterType.equalsIgnoreCase(FROM_TO_REC)) {
			// process for sub selection
			String[] tempParams = { args[1], args[2] };
			params = tempParams;
		}
		FilterOption filterOption = getFilterOption(filterType, params);
		resList = filterUtil.getFilteredData(data, filterOption);
		return resList;
	}
	/**
	 * Method to check if given String is of numeric format
	 * @param entry - string to check
	 * @return - if string is numeric
	 */
	public boolean isNumeric(String entry) {
		return entry.matches("^[1-9]\\d*(\\.\\d+)?$");
	}

}
