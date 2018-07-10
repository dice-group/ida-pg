package upb.ida.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.databind.node.ArrayNode;

public class KernelHttpRequest {
	 
	
	public List<String> getClusterResults(ArrayNode nodeArr1) throws IOException {
	StringBuilder stringBuilder = new StringBuilder("http://127.0.0.1:8890/contacts/");
     stringBuilder.append(nodeArr1.toString());
     //stringBuilder.append(URLEncoder.encode(username, "UTF-8"));
    
     URL obj = new URL(stringBuilder.toString());

		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		con.setRequestMethod("GET");
		con.setRequestProperty("Accept-Charset", "UTF-8");
		BufferedReader in = new BufferedReader(
		        new InputStreamReader(con.getInputStream()));
		String line;
		StringBuffer response = new StringBuffer();

		while ((line = in.readLine()) != null) {
			response.append(line);
		}
		in.close();
		String m;
		m=response.toString();
		m=m.replaceAll("^.|.$", "");
		List<String> clusterResult = Arrays.asList(m.split("\\s"));
		return clusterResult;
		}
}
