package upb.ida.test;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import upb.ida.Application;
import upb.ida.bean.ResponseBean;
import upb.ida.service.RiveScriptService;
@RunWith(SpringRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = {Application.class})
public class MessageRestControllerTest {
	
	@Autowired
	private ResponseBean response;
	@Autowired
	private RiveScriptService rsService;

	
	@Test
	
	
	public void  sendmessagetest() throws Exception  {

		String actvScrId;
		Map<String, Object> dataMap = new HashMap<>();
		
		//dataMap.put("1");
		dataMap.put("citydistance.csv","actvTbl");
		dataMap.put("city","actvDs");
		response.setPayload(dataMap);
		String msg = "I would like a force directed graph vi	sualization for the current table";
		String reply = rsService.getRSResponse(msg);
		
		String expected = "Alright!, but before proceeding tell me what is the source node ?";
		//assertEquals(reply, expected);

		response.setChatmsg(reply);
		System.out.println(response);
		
		
		
	}
}
