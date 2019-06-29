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
	private static String dbhost = "http://localhost:3030";
	private static String datasetName = "/storyboard";
	private static String dbUrl = dbhost + datasetName;
	/**
	 * Method to accept stories
	 * @return - instance of {@link ResponseBean}
	 * @throws Exception
	 */
	@RequestMapping
	public ResponseBean getStoryData(@RequestParam("id") String storyId) throws Exception {

		String id = storyId ;

		Map<String, Object> dataFromDB = getStoryDataFromDB(id);
		String actvScrId = dataFromDB.get("actvScrId").toString();
		String actvDs = dataFromDB.get("actvDs").toString();
		String actvTbl = dataFromDB.get("actvTbl").toString();
		String actvVs = dataFromDB.get("actvVs").toString();
		String[] columnsList = dataFromDB.get("columnsList").toString().replaceAll("[\\[\\]\\s+]","").split(",");

		Map<String, Object> dataMap = new HashMap<>();
		dataMap.put("actvScrId", actvScrId);
		dataMap.put("actvTbl", actvTbl);
		dataMap.put("actvDs", actvDs);
		dataMap.put("actvVs", actvVs);
		response.setPayload(dataMap);
		if(columnsList.length > 0)
			dataService.getDataTableOfSpecificColumns(actvDs, actvTbl, Arrays.asList(columnsList));
		else
			dataService.getDataTable(actvDs, actvTbl);
		return response;
	}

	public static Map<String, Object> getStoryDataFromDB(String uuid) {
		String serviceURI = dbUrl;
		Map<String, Object> dataMap =  new HashMap<>();

		String query = " PREFIX ab: <http://storydata/#" + uuid + "> "
			+ "prefix dc: <http://www.w3.org/2001/vcard-rdf/3.0#>select ?actvScrId ?actvTbl ?actvDs ?actvVs  ?columnsList where {ab: dc:actvScrId ?actvScrId ;dc:actvTbl ?actvTbl; dc:actvDs ?actvDs ; dc:actvVs ?actvVs; dc:columnsList ?columnsList .}";

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
