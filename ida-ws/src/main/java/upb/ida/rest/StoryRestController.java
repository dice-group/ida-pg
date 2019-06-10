package upb.ida.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import upb.ida.bean.ResponseBean;
import upb.ida.service.DataService;
import upb.ida.service.RiveScriptService;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	/**
	 * Method to accept stories
	 * @return - instance of {@link ResponseBean}
	 * @throws Exception
	 */
	@RequestMapping(value = "/id")
	public ResponseBean getStoryData(@RequestParam("id") String storyId) throws Exception {

		//get details from database against this id
		String id = storyId ;
		//This data will be filled from backend
		String actvScrId = "1";
		String actvTbl = "movehubqualityoflife.csv";
		String actvDs = "city";
		String actvVs = "bar-graph";
		List<String> columnsList = Arrays.asList("City", "Pollution");


		Map<String, Object> dataMap = new HashMap<>();
		dataMap.put("actvScrId", actvScrId);
		dataMap.put("actvTbl", actvTbl);
		dataMap.put("actvDs", actvDs);
		dataMap.put("actvVs", actvVs);
		response.setPayload(dataMap);
		dataService.getDataTableOfSpecificColumns(actvDs, actvTbl, columnsList);
		return response;
	}

}
