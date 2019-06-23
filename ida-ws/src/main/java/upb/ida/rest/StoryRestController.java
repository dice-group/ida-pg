package upb.ida.rest;

import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.RDFNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import upb.ida.bean.ResponseBean;
import upb.ida.constant.IDALiteral;
import upb.ida.service.DataService;
import upb.ida.service.RiveScriptService;

import java.util.*;

/**
 * Exposes RESTful RPCs for the IDA Storys
 * 
 * @author Ayaz
 *
 */
@RestController
@CrossOrigin(origins = "*", allowCredentials = "true")
@RequestMapping("/getstory")
public class StoryRestController {
	@Autowired
	private ResponseBean response;
	@Autowired 
	private DataService dataService;
	private static String dbhost = System.getenv("FUSEKI_URL"); //"http://localhost:3030";
	private static String datasetName = "/storyboard";
	private static String dbUrl = dbhost + datasetName;
	/**
	 * Method to accept stories
	 * @return - instance of {@link ResponseBean}
	 * @throws Exception
	 */
	@RequestMapping(value = "/id")
	public ResponseBean getStoryData(@RequestParam("id") String storyId) throws Exception {

		String id = storyId ;
		//This data will be filled from backend
		//String actvScrId = "1";
		//String actvTbl = "movehubqualityoflife.csv";
		//String actvDs = "city";
		//String actvVs = "bar-graph";
		//List<String> columnsList = Arrays.asList("City", "Pollution");

		Map<String, Object> dataMap = getStoryDataFromDB(id);
		String actvDs = dataMap.get("actvDs").toString();
		String actvTbl = dataMap.get("actvTbl").toString();
		String[] columnsList = dataMap.get("columnsList").toString().replaceAll("[\\[\\]]","").split(",");
		if(columnsList.length > 0)
			dataService.getDataTableOfSpecificColumns(actvDs, actvTbl, Arrays.asList(columnsList));
		else
			dataService.getDataTable(actvDs, actvTbl);
		response.setPayload(dataMap);
		response.setActnCode(IDALiteral.UIA_GS);
		return response;
	}

	public static Map<String, Object> getStoryDataFromDB(String uuid) {
		String serviceURI = dbUrl;
		Map<String, Object> dataMap =  new HashMap<>();

		String query = " PREFIX ab: <http://storydata/#" + uuid + "> "
			+ "prefix dc: <http://www.w3.org/2001/vcard-rdf/3.0#>select ?actvScrId ?actvTbl ?actvDs ?actvVs  ?columnsList where {ab: dc:actvScrId ?actvScrId ;dc:actvTbl ?actvTbl; dc:actvVs ?actvDs ; dc:actvVs ?actvVs; dc:columnsList ?columnsList .}";

		QueryExecution q = null;
		ResultSet results = null;
		try {
			q = QueryExecutionFactory.sparqlService(serviceURI, query.trim());
			results = q.execSelect();
			while (results.hasNext()) {
				QuerySolution soln = results.nextSolution();
				RDFNode actvScrId = soln.get("actvScrId");
				RDFNode actvTbl = soln.get("actvTbl");
				RDFNode actvDs = soln.get("actvDs");
				RDFNode actvVs = soln.get("actvVs");
				RDFNode columnsList = soln.get("columnsList");

				dataMap.put("actvScrId", actvScrId);
				dataMap.put("actvTbl", actvTbl);
				dataMap.put("actvDs", actvDs);
				dataMap.put("actvVs", actvVs);
				dataMap.put("columnsList", columnsList);
			}
		}
		finally {
			if (q != null)
				q.close();
		}
		return dataMap;
	}

}
