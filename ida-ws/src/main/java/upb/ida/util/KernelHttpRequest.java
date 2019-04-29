package upb.ida.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.databind.node.ArrayNode;

/**
 * KernelHttpRequest connects with jupyter kernel gateway and gets results of
 * clustering .
 * 
 * @author Faisal
 *
 */
public class KernelHttpRequest {

	public static final String ip_address = "http://131.234.28.84:8889/cluster/";

	/**
	 * Method to connect with jupyter kernel gatway server and create a GET request
	 * to fetch results of clustering
	 * 
	 * @param nodeArr1
	 *            - {@link getClusterResults#nodeArr1}
	 * 
	 * @throws -
	 *             IOException
	 * 
	 * @return - List of string containing response from jupyter kernel gateway
	 *         server. It gets an array and converts it to string.
	 *
	 */
	public List<String> getClusterResults(ArrayNode nodeArr1) throws IOException {
		StringBuilder stringBuilder = new StringBuilder(ip_address);
		stringBuilder.append(nodeArr1.toString());
		// stringBuilder.append(URLEncoder.encode(username, "UTF-8"));
		List<String> clusterResult = null;
		URL obj = new URL(stringBuilder.toString());

		HttpURLConnection con = null;
		BufferedReader in = null;
		try {
			con = (HttpURLConnection) obj.openConnection();
			con.setConnectTimeout(30000);
			con.setRequestMethod("GET");
			con.setRequestProperty("Accept-Charset", "UTF-8");
			in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String line;
			StringBuffer response = new StringBuffer();

			while ((line = in.readLine()) != null) {
				response.append(line);
			}
			String m;
			m = response.toString();
			m = m.replaceAll("^.|.$", "").trim();
			clusterResult = Arrays.asList(m.split("\\s+"));
		} finally {
			if (con != null) {
				con.disconnect();
			}
			if (in != null) {
				in.close();
			}
		}

		return clusterResult;
	}
}
