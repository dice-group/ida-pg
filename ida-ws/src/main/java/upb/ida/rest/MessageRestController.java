package upb.ida.rest;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import upb.ida.bean.ResponseBean;
import upb.ida.fdg.FDG_Util;
import upb.ida.temp.DemoMain;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/message")
public class MessageRestController {
	@Autowired
	ResponseBean response;
	@Autowired
	DemoMain dem;
	@Autowired
	FDG_Util fdgUtil;

	@RequestMapping("/sayhello")
	public String greeting(@RequestParam(value = "name", defaultValue = "World") String name) {
		return "Hello " + name + "!";
	}

	@RequestMapping("/sendmessage")
	public ResponseBean sendmessage(@RequestParam(value = "msg") String msg,
			@RequestParam(value = "actvScrId") String actvScrId, @RequestParam(value = "actvTbl") String actvTbl,
			@RequestParam(value = "actvDs") String actvDs) throws Exception {

		if (msg.matches(".*[cC]ity.*dataset.*")) {
			response.setChatmsg("City Dataset loaded, you can access the table(s) in the main view.");
			Map<String, Object> dataMap = new HashMap<String, Object>();
			dataMap.put("label", "City");
			dataMap.put("dataset", dem.getDatasetContent("city"));
			response.setPayload(dataMap);
			response.setActnCode(1);
		} else if (msg.matches(".*[mM]ovie.*dataset.*")) {
			response.setChatmsg("Movie Dataset loaded, you can access the table(s) in the main view.");
			Map<String, Object> dataMap = new HashMap<String, Object>();
			dataMap.put("label", "Movie");
			dataMap.put("dataset", dem.getDatasetContent("movie"));
			response.setPayload(dataMap);
			response.setActnCode(1);
		} else if (msg.matches("[fF]orce|fdg|directed graph")) {
			response.setChatmsg("Your requested Force Directed Graph is now added to the main view.");
			Map<String, Object> dataMap = new HashMap<String, Object>();
			dataMap.put("fdgData", fdgUtil.generateFDG("/"+actvDs+"/"+actvTbl, "city1", "city2", "distance"));
			dataMap.put("actvScrId", actvScrId);
			response.setPayload(dataMap);
			response.setActnCode(2);
		} else
			response.setChatmsg("Service under development. Please try later.");
		return response;
	}

	@RequestMapping("/sumNum")
	public ResponseBean sumNum(@RequestParam(value = "a") int a, @RequestParam(value = "b") int b) throws Exception {
		response.setPayload(a + b);
		return response;
	}

}