package upb.ida.rest;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import upb.ida.bean.ResponseBean;
import upb.ida.temp.DemoMain;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/message")
public class MessageRestController {
	@Autowired
	ResponseBean response;
	@Autowired
	DemoMain dem;
	@RequestMapping("/sayhello")
	public String greeting(@RequestParam(value = "name", defaultValue = "World") String name) {
		return "Hello " + name + "!";
	}
	
	@RequestMapping("/sendmessage")
	public ResponseBean sendmessage(@RequestParam(value = "msg") String msg) throws Exception {
		
		if(msg.matches(".*[cC]ity.*dataset.*")) {
			response.setChatmsg("City Dataset loaded, you can access the table(s) in the main view.");
			Map<String, Object> dataMap =   new HashMap<String, Object>();
			dataMap.put("label", "City");
			dataMap.put("dataset", dem.getDatasetContent("city"));
			response.setPayload(dataMap);
			response.setActnCode(1);
		}
		else if(msg.matches(".*[mM]ovie.*dataset.*")) {
			response.setChatmsg("Movie Dataset loaded, you can access the table(s) in the main view.");
			Map<String, Object> dataMap =   new HashMap<String, Object>();
			dataMap.put("label", "Movie");
			dataMap.put("dataset", dem.getDatasetContent("movie"));
			response.setPayload(dataMap);
			response.setActnCode(1);
		}
		else
			response.setChatmsg("Service under development. Please try later.");
		return response;
	}
	@RequestMapping("/sen")
	public ResponseBean sen() throws Exception {
		response.setPayload("hi i am faisal");
		return response;
	}

}