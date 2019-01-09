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

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import upb.ida.bean.ResponseBean;
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
	@RequestMapping(value = "/visualization" , method = RequestMethod.GET , headers="Accept=application/json")
		public String process(@RequestParam Map<String,String> jsn)throws Exception {
		//ObjectMapper mapper = new ObjectMapper();
		
//		Map<String, Object> jsonRequest = mapper.readValues(jsn,
//			    new TypeReference<Map<String,Object>>(){});
		 
		if( jsn.get("vName") != null) {
			return "git it ";
		
//			List<Map<String, String>> data = rdfDataFetch.getData(jsonRequest);
//		
//		    if(jsonRequest.get("vName") == "bg") {
//		    	barGraph.generateBarGraphData((String) jsonRequest.get("xaxis"), (String)jsonRequest.get("yaxis"),data , response);
		    }
		return "not good";	
			
		}
//		response.setPayload(dataMap);

//		return response;
	
	
	

}