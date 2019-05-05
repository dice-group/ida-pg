package upb.ida.test;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import upb.ida.Application;
import upb.ida.bean.ResponseBean;
import upb.ida.rest.MessageRestController;
import upb.ida.util.BarGraphUtil;

@RunWith(SpringRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = { Application.class })

public class BgMsgRestControllerTest {

	@Autowired
	private MessageRestController msgRstCntrl;
	@Autowired
	private BarGraphUtil barGraph;

	@Test
	public void sendmessagetestPos() throws Exception {
		ResponseBean responseBean;
		responseBean = msgRstCntrl.sendmessage("I want a bar-graph visualisation for the current table", "1",
				"movehubcostofliving.csv", "city");
		responseBean = msgRstCntrl.sendmessage("x-axis is city", "1", "movehubcostofliving.csv", "city2");
		responseBean = msgRstCntrl.sendmessage("y-axis is wine", "1", "movehubcostofliving.csv", "city2");
		responseBean = msgRstCntrl.sendmessage("Top 2 records, sorted descending on wine", "1", "movehubcostofliving.csv", "city2");

		//System.out.println(responseBean.getPayload().get("bgData"));

		
		ObjectMapper mapper = new ObjectMapper();
		ArrayNode jArray = mapper.createArrayNode();

		ObjectNode user1 = mapper.createObjectNode();
		user1.put("Wine", 10.49);
		user1.put("City", "Geneva");

		ObjectNode user2 = mapper.createObjectNode();
		user2.put("Wine", 8.4);
		user2.put("City", "Zurich");
		
		jArray.add(user1);
		jArray.add(user2);

		//System.out.print(jArray);
		// Object keys = "city";
		List<String> keys = new ArrayList<String>();
	
		keys.add("City");
	
		Map<String, Object> expected = new HashMap<String, Object>();

		expected.put("xaxisname", "City");
		expected.put("keys", keys);
		 expected.put("baritems", jArray);
		 expected.put("yaxisname", "Wine");
		
		

		// String xKey = "City";
		// String yKey = "wine";
		
		//
		// dataMap.put("xaxisname", xKey);
		// dataMap.put("yaxisname", yKey);
		// dataMap.put("keys", keys);
		// dataMap.put("barItems", inputList);
		// System.out.println(dataMap);

		// barGraph.newJsonObjct("city", "wine", );("city", "wine", expected);

		@SuppressWarnings("unchecked")
		Map<String, Object> actual = (Map<String, Object>) responseBean.getPayload().get("bgData");
		// System.out.println(expected);
		 //System.out.println(actual);
		assertEquals(expected, actual);

	}

}
