package upb.ida.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import upb.ida.Application;
import upb.ida.bean.ResponseBean;
import upb.ida.rest.MessageRestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = {Application.class})
@TestPropertySource("classpath:application.properties")
public class BgMsgRestControllerTest {

	@Autowired
	private MessageRestController msgRstCntrl;

	@Test
	public void sendmessagetestPos() throws Exception {
		ResponseBean responseBean = new ResponseBean();
		responseBean = msgRstCntrl.sendmessage("I want a bar-graph visualisation for the current table", "1",
				"Continent", "test");
		responseBean = msgRstCntrl.sendmessage("x-axis is name", "1", "Continent", "test");
		responseBean = msgRstCntrl.sendmessage("y-axis is population", "1", "Continent", "test");
		responseBean = msgRstCntrl.sendmessage("Top 2 records, sorted descending on population", "1", "Continent", "test");

		ObjectMapper mapper = new ObjectMapper();
		ArrayNode jArray = mapper.createArrayNode();
		ObjectNode user1 = mapper.createObjectNode();
		user1.put("name", "Africa");
		user1.put("population", Double.valueOf(922011000));
		ObjectNode user2 = mapper.createObjectNode();
		user2.put("name", "Europe");
		user2.put("population", Double.valueOf(731000000));
		jArray.add(user1);
		jArray.add(user2);
		List<String> keys = new ArrayList<String>();
		keys.add("name");
		Map<String, Object> expected = new HashMap<String, Object>();
		expected.put("xaxisname", "name");
		expected.put("keys", keys);
		expected.put("baritems", jArray);
		expected.put("yaxisname", "population");

		@SuppressWarnings("unchecked")
		Map<String, Object> actual = (Map<String, Object>) responseBean.getPayload().get("bgData");
		assertEquals(expected, actual);
	}

}
