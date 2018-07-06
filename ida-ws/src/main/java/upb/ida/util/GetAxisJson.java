package upb.ida.util;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;
//import com.fasterxml.jackson.databind.node.ObjectNode;

public class GetAxisJson {

	// To initialize variables with parameterized constructor
	public void newJsonObjct(String x, String y, List<Map<String, String>> lstt, Map<String, Object> dataMap)
			throws java.io.IOException, NumberFormatException, ParseException {
		ObjectMapper mapper = new ObjectMapper();
		ArrayNode nodeArr1 = mapper.createArrayNode();
		String xKey = getMatchingKey(x, lstt.get(0));
		String yKey = getMatchingKey(y, lstt.get(0));
		List <String> keys = new ArrayList <String> ();
	    keys.add(xKey);
	    //dataMap.put("Label", "BgData");
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
		dataMap.put("baritems",nodeArr1);
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

}