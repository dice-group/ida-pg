package upb.ida.util;

//import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.text.NumberFormat;
import java.text.ParseException;
import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;
//import com.fasterxml.jackson.databind.node.ObjectNode;

public class GetAxisJson {

	// To initialize variables with parameterized constructor
	public Object newJsonObjct(String x, String y, List<Map<String, String>> lstt) throws java.io.IOException, NumberFormatException, ParseException {
		ObjectMapper mapper = new ObjectMapper();
		ArrayNode nodeArr1 = mapper.createArrayNode();
		
//		ArrayList<String> x_axis = new ArrayList<String>();
		for (int i = 0; i < lstt.size(); i++) {
			HashMap<String, Object> mMap = new HashMap<String, Object>();
			if (lstt.get(i).containsKey(x) && lstt.get(i).containsKey(y)) {
				mMap = new HashMap<String, Object>(); // create a new one!
				mMap.put(x, lstt.get(i).get(x));
				Double StrngthValID = 
						Double.parseDouble(NumberFormat.getNumberInstance(java.util.Locale.US).parse(lstt.get(i).get(y)).toString());
				mMap.put(y,StrngthValID) ;
				
				nodeArr1.add(mapper.readTree(mapper.writeValueAsString(mMap)));
			}
		}
		
		
//		String jjson = new Gson().toJson(listt);
//	    Object ar[] = new String[2];
//		x_axis.add(x);
//		String xaxis = new Gson().toJson(x_axis);
//		ar[0] = xaxis;
//		ar[1] = jjson;
		return nodeArr1;
	}

}