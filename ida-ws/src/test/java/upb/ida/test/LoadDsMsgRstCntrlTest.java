package upb.ida.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.constraints.Null;

import org.json.JSONArray;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import net.minidev.json.JSONObject;
import upb.ida.Application;
import upb.ida.bean.ResponseBean;
import upb.ida.rest.MessageRestController;
import upb.ida.util.BarGraphUtil;
import upb.ida.util.FileUtil;

@RunWith(SpringRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = { Application.class })
public class LoadDsMsgRstCntrlTest {

	@Autowired
	MessageRestController msgRstCntrl;
	@Autowired
	FileUtil fileutil;

	@Test
	public void sendmessagetestPos() throws Exception {

		ResponseBean responseBean;
		responseBean = msgRstCntrl.sendmessage("i would like you to load the city3 dataset", "0", "", "");
		System.out.println(responseBean.getPayload().get("dsMd"));

		ObjectMapper mapper = new ObjectMapper();
		ArrayNode jArray1 = mapper.createArrayNode();

		ObjectNode user1 = mapper.createObjectNode();
		user1.put("Wine", 13.5);
		user1.put("City", "Nashville");

		ObjectNode col1 = mapper.createObjectNode();
		col1.put("colIndex", 1);
		col1.put("colName", "City");
		col1.put("colType", "string");

		ObjectNode col2 = mapper.createObjectNode();
		col2.put("colIndex", 2);
		col2.put("colName", "Wine");
		col2.put("colType", "number");

		jArray1.add(col1);
		jArray1.add(col2);

		// System.out.println(jArray1);		
		
		//convert map to json array
		ArrayNode jArray2 = mapper.createArrayNode();
		ObjectNode details = mapper.createObjectNode();
		details.put("fileName", "movehubcostofliving.csv");
		details.put("fileDesc", "estimate on basic things");
		details.put("fileColMd", jArray1);
		
		jArray2.add(details);
		//System.out.println(jArray);
		

		// JsonObject fileInfo = new JsonObject();
		//Map<String, Object> fileInfo = new HashMap<String, Object>();
		ArrayNode jArray3 = mapper.createArrayNode();
		ObjectNode fileInfo = mapper.createObjectNode();
		fileInfo.put("dsName", "City3");
		fileInfo.put("dsDesc",
				"This dataset contains the movehub ratings");
		fileInfo.put("filesMd", jArray2);
		
		jArray3.add(fileInfo);
		
		//convert map to json node
		ObjectMapper mapper1 = new ObjectMapper();
		JsonNode jsonNodeMap = mapper1.convertValue(fileInfo, JsonNode.class);

		//convert to objectnode
		ObjectNode expected= jsonNodeMap.deepCopy();
		System.out.println(expected);

		//actual output
		ObjectNode actualMap = (ObjectNode)(responseBean.getPayload().get("dsMd"));
		 //System.out.println(actualMap);
		
		assertTrue(expected.equals(actualMap));
		
	}

}
