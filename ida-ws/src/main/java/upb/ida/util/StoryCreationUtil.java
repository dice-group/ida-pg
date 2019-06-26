package upb.ida.util;

import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import upb.ida.bean.ResponseBean;
import upb.ida.constant.IDALiteral;
import java.io.IOException;
import java.util.*;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFormatter;
import org.apache.jena.rdfconnection.*;
import org.apache.jena.update.UpdateFactory;
import org.apache.jena.update.UpdateRequest;
import org.springframework.web.bind.annotation.*;

/**
 * Exposes util methods for Story Creation
 */
@Component
public class StoryCreationUtil {
	private static String dbhost = System.getenv("FUSEKI_URL"); //"http://localhost:3030"; 
	private static String datasetName = "/storyboard";
	private static String dbUrl = dbhost + datasetName;
	/**
	 * Method to create response for bar gaph visualization request
	 *
	 * @param args -  arguments of for filtering
	 * @param responseBean -  ResponseBean is used as a uniform response 
	 * format for the incoming REST calls
	 * @return - void
	 */
	public void generateStory(String[] args, ResponseBean responseBean)
			throws IOException {

		 RDFConnectionRemoteBuilder builder = RDFConnectionFuseki.create()
			.destination(dbUrl);

		// Fetch active dataset details
		String actvScrId = (String) responseBean.getPayload().get("actvScrId");
		String actvTbl = (String) responseBean.getPayload().get("actvTbl");
		String actvDs = (String) responseBean.getPayload().get("actvDs");
		String actvVs = (String) responseBean.getPayload().get("actvVs");
		List<String> columnsList = Arrays.asList(args);

		//Create url for stroy
		UUID uuid = UUID.randomUUID();
		String uuidStr = uuid.toString();
		//String urlStory = "localhost:8080/ida-ws" + "/getstory/" + "id?id=" + uuidStr;

		// In this variation, a connection is built each time.
		try ( RDFConnectionFuseki conn = (RDFConnectionFuseki)builder.build() ) {
			UpdateRequest request = UpdateFactory.create();

			String insertString = " PREFIX dc: <http://www.w3.org/2001/vcard-rdf/3.0#> "
				+ " PREFIX ab: <http://storydata/#" + uuidStr + "> " + " INSERT DATA " + " { ab: "
				+ "dc:actvScrId \"" + actvScrId + "\" ; " + "dc:actvTbl \"" + actvTbl
				+ "\" ; " + "dc:actvDs \"" + actvDs + "\" ; " + "dc:actvVs \""
				+ actvVs + "\" ; " + "dc:columnsList \"" + columnsList
				+ "\" . " + " } ";
			request.add(insertString);
			conn.update(request);
		}

		Map<String, Object> dataMap = responseBean.getPayload();
		dataMap.put("storyUuid", uuid);
		responseBean.setPayload(dataMap);
		responseBean.setActnCode(IDALiteral.UIA_SC);
	}
}
