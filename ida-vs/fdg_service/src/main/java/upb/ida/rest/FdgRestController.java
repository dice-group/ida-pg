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
import upb.ida.util.FDG_Util;
/**
 * Exposes RESTful RPCs for the IDA NLP engine
 * 
 *
 */
@RestController
@CrossOrigin(origins = "*", allowCredentials = "true")
@RequestMapping("/vs")
public class FdgRestController {
	@Autowired
	private ResponseBean response;
	
	@Autowired
	private SessionValues sessionVal;
	
	@Autowired 
	private RdfDataService rdfDataFetch;
	
	@Autowired 
	private FDG_Util fdg;
	
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
	
	@RequestMapping(value = "/vs/fdg/source" , method = RequestMethod.GET , headers="Accept=application/json")
	public ResponseBean sourceNode(@RequestParam Map<String,String> jsn)throws Exception {
		
		if( jsn.get("source") != null) {
			Map<String,Object> dataVal = new HashMap<String,Object>();
			dataVal.put("source",jsn.get("source") );
			sessionVal.setData(dataVal);
			response.setValues(true);
		}
		
		else{
			response.setValues(false);
			
		}
		
		return response;
		
	}
	
	@RequestMapping(value = "/vs/fdg/target" , method = RequestMethod.GET , headers="Accept=application/json")
	public ResponseBean targetNode(@RequestParam Map<String,String> jsn)throws Exception {
		
		if( jsn.get("target") != null) {
			Map<String,Object> dataVal = new HashMap<String,Object>();
			dataVal.put("target",jsn.get("target") );
			sessionVal.setData(dataVal);
			response.setValues(true);
		}
		
		else{
			response.setValues(false);
			
		}
		
		return response;
	}
		
	
	
	@RequestMapping(value = "/vs/fdg/strength" , method = RequestMethod.GET , headers="Accept=application/json")
	public ResponseBean strengthNode(@RequestParam Map<String,String> jsn)throws Exception {
		
		if( jsn.get("strength") != null) {
			Map<String,Object> dataVal = new HashMap<String,Object>();
			dataVal.put("strength",jsn.get("strength") );
			sessionVal.setData(dataVal);
			response.setValues(true);
		}
		
		else{
			response.setValues(false);
			
		}
		
		return response;
		
	}
	
	@RequestMapping(value = "/vs/fdg/sparql" , method = RequestMethod.GET , headers="Accept=application/json")
	public ResponseBean sparqlQuery(@RequestParam Map<String,String> jsn)throws Exception {
		
		if( jsn.get("query") != null) {
			Map<String, Object> dataMap = response.getPayload();
			Map<String,Object> dataVal = new HashMap<String,Object>();
			dataVal.put("sparql",jsn.get("query") );
			sessionVal.setData(dataVal);
			
			if(sessionVal.getData().size() == 4) {
			List<Map<String, String>> data =  rdfDataFetch.getData(sessionVal.getData().get("sparql"));
				
			dataMap.put("fdg",fdg.generateFDG(data,(String) sessionVal.getData().get("source"),(String) sessionVal.getData().get("target"),(String) sessionVal.getData().get("strength")));
			} 	    
			
			response.setPayload(dataMap);
			response.setValues(true);
			sessionVal.getData().remove("source");
			sessionVal.getData().remove("target");
			sessionVal.getData().remove("strength");
			sessionVal.getData().remove("sparql");
			
		}
		
		else{
			response.setValues(false);
			
		}
		
		return response;
	}
	
}
		
