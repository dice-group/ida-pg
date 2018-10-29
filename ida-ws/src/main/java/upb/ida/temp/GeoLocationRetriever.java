package upb.ida.temp;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import upb.ida.util.FileUtil;

public class GeoLocationRetriever {
	public static final JsonNodeFactory JSON_FACTORY = JsonNodeFactory.instance;
	public static final ObjectMapper OBJ_MAPPER = new ObjectMapper();
	public static final ObjectReader OBJ_READER = OBJ_MAPPER.reader();
	public static final ObjectWriter OBJ_WRITER = OBJ_MAPPER.writer(new DefaultPrettyPrinter());
	public static FileUtil fileUtil;
	public static final String NOMINATIM_URI = "https://nominatim.openstreetmap.org/search";

	public static void main(String[] args) throws IOException, InterruptedException {
		fileUtil = new FileUtil();
		File inpCsvFile = new File("D:\\Nikit\\ProjectGroup_WS\\Datasets\\ss-dienstalterliste\\ss-geo-dnst.csv");
		File outputJsonFile = new File("D:\\Nikit\\ProjectGroup_WS\\Datasets\\ss-dienstalterliste\\ss-geo-dnst-map.json");
		fetchGeoLocation(inpCsvFile, outputJsonFile);

	}

	public static void fetchGeoLocation(File inpCsvFile, File outputJsonFile) throws JsonProcessingException, IOException, InterruptedException {
		// initialize cache map for coordinates
		Map<String, ArrayNode> cityLocMap = new HashMap<>();
		// Initialize result json
		ObjectNode resObj = new ObjectNode(JSON_FACTORY);
		// read the csv file and convertToMap
		List<Map<String, String>> csvRows = fileUtil.convertToMap(inpCsvFile);
		// Initialize param map
		Map<String, String> parameters = new HashMap<>();
		parameters.put("format", "json");
		// loop through each item
		for (Map<String, String> entry : csvRows) {
			// fetch cityname
			String place = entry.get("dienststellung_ort");
			ArrayNode coordinate = null;
			if (place != null && place.trim().length() > 0) {
				// fetch coordinate from api if not in cache
				coordinate = cityLocMap.get(place);
				if (coordinate == null) {
					coordinate = OBJ_MAPPER.createArrayNode();
					// send a http request to nominatim
					parameters.put("q", place);
					setCityCoordinate(parameters, coordinate);
					cityLocMap.put(place, coordinate);
				}
			}
			// push the mapping to result json d_id: cityName, coordinate
			ObjectNode newNode = new ObjectNode(JSON_FACTORY);
			newNode.put("cityName", place);
			newNode.set("coordinate", coordinate);
			resObj.set(entry.get("dienststellung_id"), newNode);
		}
		// write the output to a file
		OBJ_WRITER.writeValue(outputJsonFile, resObj);
	}

	public static void setCityCoordinate(Map<String, String> params, ArrayNode coordinate) throws IOException, InterruptedException {
		// Query the nominatim server
		String respStr = sendHttpGetRequest(NOMINATIM_URI, params);
		// Nominatim Usage policy of maximum 1 request/second
		Thread.sleep(1000);
		ArrayNode respJsonArr = (ArrayNode) OBJ_MAPPER.reader().readTree(respStr);
		if (respJsonArr.size() > 0) {
			// pick the first result
			ObjectNode firstEntry = (ObjectNode) respJsonArr.get(0);
			// fetch the latitude and longitude
			coordinate.add(firstEntry.get("lat"));
			coordinate.add(firstEntry.get("lon"));
		}
	}

	// Util methods
	public static String sendHttpGetRequest(String urlStr, Map<String, String> parameters) throws IOException {
		URL url = new URL(urlStr+"?"+getParamsString(parameters));
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setRequestMethod("GET");
		con.setDoOutput(true);
		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer content = new StringBuffer();
		while ((inputLine = in.readLine()) != null) {
			content.append(inputLine);
		}	
		in.close();
		con.disconnect();
		return content.toString();
	}

	public static String getParamsString(Map<String, String> params) throws UnsupportedEncodingException {
		StringBuilder result = new StringBuilder();

		for (Map.Entry<String, String> entry : params.entrySet()) {
			result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
			result.append("=");
			result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
			result.append("&");
		}

		String resultString = result.toString();
		return resultString.length() > 0 ? resultString.substring(0, resultString.length() - 1) : resultString;
	}
}
