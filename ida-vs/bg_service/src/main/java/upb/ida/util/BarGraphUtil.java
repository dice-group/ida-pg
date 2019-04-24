package upb.ida.util;

//import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

//import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import upb.ida.bean.ResponseBean;
//import upb.ida.constant.IDALiteral;

/**
 * Exposes util methods for Bargraph visualization
 */
@Component
public class BarGraphUtil {

	
	// To initialize variables with parameterized constructor
	/**
	 * Method to create response for Bar Graph visualization
	 * @param x - string value for x-axis
	 * @param y - string value for y-axis
	 * @param lstt - List of data read from csv file based on x and y axis
	 * @param dataMap - setting values for responseBean payload
	 * @return - void
	 */
	public void newJsonObjct(String x, String y, List<Map<String, String>> lstt, Map<String, Object> dataMap)
			throws IOException, NumberFormatException, ParseException {
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
	private String getMatchingKey(String key, Map<String, String> dataMap) {
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
	 * Method to create response for bar gaph visualization request
	 * 
	 * @param input - current file for bar graph visualization
	 * @param args -  arguments of for filtering
	 * @param responseBean -  ResponseBean is used as a uniform response 
	 * format for the incoming REST calls
	 * @return - void
	 */
	public void generateBarGraphData(String xaxis, String yaxis,List<Map<String, String>> data, ResponseBean responseBean)
			throws NumberFormatException, JsonProcessingException, IOException, ParseException {

		
		Map<String, Object> dataMap = new HashMap<String, Object>();
		String x = xaxis;
		String y = yaxis;

		// set the bargraph in dataMap
		newJsonObjct(x, y, data, dataMap);
		Map<String, Object> submap_data = responseBean.getPayload();
		submap_data.put("bgData", dataMap);

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
