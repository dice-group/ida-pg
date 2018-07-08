package upb.ida.util;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import upb.ida.bean.FilterOption;
import upb.ida.bean.FilterSort;
import upb.ida.bean.ResponseBean;
import upb.ida.constant.IDALiteral;
import upb.ida.temp.DemoMain;

@Component
public class BarGraphUtil {

	public static final String FIRST_N_REC = "FIRSTN";
	public static final String LAST_N_REC = "LASTN";
	public static final String TOP_N_REC = "TOPN";
	public static final String FROM_TO_REC = "FROMTO";

	@Autowired
	DemoMain demoMain;
	@Autowired
	private ServletContext context;
	@Autowired
	FilterUtil filterUtil;

	// To initialize variables with parameterized constructor
	public void newJsonObjct(String x, String y, List<Map<String, String>> lstt, Map<String, Object> dataMap)
			throws java.io.IOException, NumberFormatException, ParseException {
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

	public void fileCsv(File input, String x, String y, Map<String, Object> dataMap, String[] args)
			throws JsonProcessingException, IOException, NumberFormatException, ParseException {

		List<Map<String, String>> lstt = demoMain.convertToMap(input);
		//Filter data
		lstt = fetchFilteredData(lstt, args);
		GetAxisJson jsn = new GetAxisJson();

		jsn.newJsonObjct(x, y, lstt, dataMap);

	}

	public void getJsonData(String filepath, String x, String y, Map<String, Object> dataMap, String args[])
			throws JsonProcessingException, IOException, NumberFormatException, ParseException {
		File file = new File(context.getRealPath(filepath));
		fileCsv(file, x, y, dataMap, args);

	}

	public FilterOption getFilterOption(String filterType, String[] params) {
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
			Integer toSeq = Integer.parseInt(params[0]);
			filterOption = filterUtil.getSubDatasetFilterOpt(fromSeq, toSeq);
		}
		return filterOption;
	}

	public void generateBarGraphData(String[] args, ResponseBean responseBean)
			throws NumberFormatException, JsonProcessingException, IOException, ParseException {

		// Fetch active dataset details
		String actvTbl = (String) responseBean.getPayload().get("actvTbl");
		String actvDs = (String) responseBean.getPayload().get("actvDs");
		Map<String, Object> dataMap = new HashMap<String, Object>();
		String path = demoMain.getFilePath(actvDs, actvTbl);
		String xaxisname = args[0];
		String yaxisname = args[1];
		// set the bargraph in dataMap
		getJsonData(path, xaxisname, yaxisname, dataMap, args);
		Map<String, Object> submap_data = responseBean.getPayload();
		submap_data.put("bgData", dataMap);
		responseBean.setActnCode(IDALiteral.UIA_BG);
	}

	public List<Map<String, String>>  fetchFilteredData(List<Map<String, String>> data, String[] args) throws NumberFormatException, ParseException {
		List<Map<String, String>> resList = null;
		String filterType = args[0];
		String[] params = null;
		if (filterType.equalsIgnoreCase(FIRST_N_REC) || filterType.equalsIgnoreCase(LAST_N_REC)) {
			// Process for First/Last N records
			String[] tempParams = {args[1], String.valueOf(data.size())};
			params = tempParams;
		} else if (filterType.equalsIgnoreCase(TOP_N_REC)) {
			//Perform check for isNumeric
			String fieldName = args[2];
			Boolean isNumeric = isNumeric(data.get(0).get(fieldName));
			// Process for top N records
			String[] tempParams = {args[1], fieldName, args[3], isNumeric.toString()};
			params = tempParams;
		} else if (filterType.equalsIgnoreCase(FROM_TO_REC)) {
			// process for sub selection
			String[] tempParams = {args[1], args[2]};
			params = tempParams;
		}
		FilterOption filterOption = getFilterOption(filterType, params);
		resList = filterUtil.getFilteredData(data, filterOption);
		return resList;
	}
	
	public boolean isNumeric(String entry) {
		return entry.matches("^[1-9]\\d*(\\.\\d+)?$");
	}

}
