package upb.ida.rest;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/message")
public class MessageRestController {

	@RequestMapping("/sayhello")
	public String greeting(@RequestParam(value = "name", defaultValue = "World") String name) {
		return "Hello " + name + "!";
	}
	
	@RequestMapping("/sendmessage")
	public String sendmessage(@RequestParam(value = "msg") String msg) {
		System.out.println(msg);
		return "Service under development. Please try later.";
	}

}
