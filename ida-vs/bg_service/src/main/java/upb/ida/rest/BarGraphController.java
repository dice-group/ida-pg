package upb.ida.rest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


import upb.ida.bean.ResponseBean;
import upb.ida.bean.SessionValues;
import upb.ida.service.RdfDataService;
import upb.ida.util.BarGraphUtil;
/**
 * Exposes RESTful RPCs for the IDA NLP engine
 * 
 *
 */
@RestController
@CrossOrigin(origins = "*", allowCredentials = "true")
@RequestMapping("/vs")
public class BarGraphController {
	@Autowired
	private ResponseBean response;
	
	@Autowired
	private SessionValues sessionVal;
	
	@Autowired 
	private RdfDataService rdfDataFetch;
	
	@Autowired 
	private BarGraphUtil barGraph;
	
	/**
	 * Method to accept queries for the visualization
	 * @RequestBody - request 
	 * @return - instance of {@link ResponseBean}
	 * @throws Exception
	 */
	@RequestMapping("/sayhello")
	public String greeting(@RequestParam(value = "name", defaultValue = "World") String name) {
		return "Hello " + name + "!";
	}
	
	@RequestMapping(value = "/vs/bargraph/xaxis" , method = RequestMethod.GET , headers="Accept=application/json")
	public ResponseBean xAxis(@RequestParam Map<String,String> jsn)throws Exception {
		
		if( jsn.get("xAxis") != null) {
			Map<String,Object> dataVal = new HashMap<String,Object>();
			dataVal.put("xAxis",jsn.get("xAxis") );
			sessionVal.setData(dataVal);
			response.setValues(true);
		}
		
		else{
			response.setValues(false);
			
		}
		
		return response;
	}
		
	@RequestMapping(value = "/vs/bargraph/yaxis" , method = RequestMethod.GET , headers="Accept=application/json")
	public ResponseBean yAxis(@RequestParam Map<String,String> jsn)throws Exception {
		
		if( jsn.get("yAxis") != null) {
			Map<String,Object> dataVal = new HashMap<String,Object>();
			dataVal.put("yAxis",jsn.get("yAxis") );
			sessionVal.setData(dataVal);
			response.setValues(true);
		}
		
		else{
			response.setValues(false);
			
		}
		
		return response;
		
	}
	
	@RequestMapping(value = "/vs/bargraph/sparql" , method = RequestMethod.GET , headers="Accept=application/json")
	public ResponseBean sparqlQuery(@RequestParam Map<String,String> jsn)throws Exception {
		
		if( jsn.get("query") != null) {
			Map<String,Object> dataVal = new HashMap<String,Object>();
			dataVal.put("sparql",jsn.get("query") );
			sessionVal.setData(dataVal);
			
			if(sessionVal.getData().size() == 3) {
			List<Map<String, String>> data =  rdfDataFetch.getData(sessionVal.getData().get("sparql"));
				
			barGraph.generateBarGraphData((String) sessionVal.getData().get("xAxis"),(String) sessionVal.getData().get("yAxis"),data , response);
			} 	    
			response.setValues(true);
			sessionVal.getData().remove("xAxis");
			sessionVal.getData().remove("yAxis");
			sessionVal.getData().remove("sparql");
			
		}
		
		else{
			response.setValues(false);
			
		}
		
		return response;
	}
	
}
		
