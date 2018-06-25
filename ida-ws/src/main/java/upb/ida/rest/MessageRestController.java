package upb.ida.rest;

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

		String reply = rsService.getRSResponse(msg);
		response.setChatmsg(reply);
		return response;
	}
	@RequestMapping("/sen")
	public ResponseBean sen() throws Exception {
		response.setPayload("hi i am faisal");
		return response;
	}

	@RequestMapping("/sumNum")
	public ResponseBean sumNum(@RequestParam(value = "a") int a, @RequestParam(value = "b") int b) throws Exception {
		response.setPayload(a + b);
		return response;
	}

}