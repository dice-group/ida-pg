package upb.ida.rest;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import upb.ida.bean.ResponseBean;
import upb.ida.service.RiveScriptService;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/message")
public class MessageRestController {
	@Autowired
	private ResponseBean response;
	@Autowired
	private RiveScriptService rsService;
	@RequestMapping("/sayhello")
	public String greeting(@RequestParam(value = "name", defaultValue = "World") String name) {
		return "Hello " + name + "!";
	}

	@RequestMapping("/sendmessage")
	public ResponseBean sendmessage(@RequestParam(value = "msg") String msg,
			@RequestParam(value = "actvScrId") String actvScrId, @RequestParam(value = "actvTbl") String actvTbl,
			@RequestParam(value = "actvDs") String actvDs) throws Exception {
		Map<String, Object> dataMap = new HashMap<>();
		dataMap.put("actvScrId", actvScrId);
		dataMap.put("actvTbl", actvTbl);
		dataMap.put("actvDs", actvDs);
		response.setPayload(dataMap);
		String reply = rsService.getRSResponse(msg);
		
		response.setChatmsg(reply);
		return response;
	}

}