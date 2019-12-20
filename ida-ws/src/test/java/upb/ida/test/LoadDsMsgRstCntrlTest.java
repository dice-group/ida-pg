package upb.ida.test;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.json.simple.parser.JSONParser;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import upb.ida.Application;
import upb.ida.bean.ResponseBean;
import upb.ida.rest.MessageRestController;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Map;

import static org.junit.Assert.*;


@RunWith(SpringRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = { Application.class })
public class LoadDsMsgRstCntrlTest {

	@Autowired
	private MessageRestController msgRstCntrl;

	@Test
	public void sendmessagetestPos() throws Exception {

		ResponseBean responseBean;
		responseBean = msgRstCntrl.sendmessage("i would like you to load the test dataset", "0", "", "");
		JSONParser jsonParser = new JSONParser();

		try (FileReader reader = new FileReader(getClass().getClassLoader().getResource("dataset/test_data_md.json").getFile()))
		{
			//Read JSON file
			Object obj = jsonParser.parse(reader);
			JSONTokener tokener = new JSONTokener(obj.toString());
			JSONObject datasetMD = new JSONObject(tokener);
			JSONObject payload = (JSONObject) datasetMD.get("payload");
			JSONObject expected = (JSONObject) payload.get("dsMd");
			Map<String, Object> metaData = (Map<String, Object>) responseBean.getPayload().get("dsMd");
			JSONObject actual = new JSONObject(metaData);
			System.out.println(expected);
			System.out.println(actual);

			JSONArray filesMd = expected.getJSONArray("filesMd");
			JSONObject fileMd;
			JSONArray cols;
			JSONObject col;
			for(int i = 0; i < filesMd.length(); i++){
				fileMd = filesMd.getJSONObject(i);
				cols = fileMd.getJSONArray("fileColMd");
				for(int j = 0; j < cols.length(); j++){
					col = cols.getJSONObject(j);
					col.remove("colIndex");
				}
			}
			filesMd = actual.getJSONArray("filesMd");
			for(int i = 0; i < filesMd.length(); i++){
				fileMd = filesMd.getJSONObject(i);
				cols = fileMd.getJSONArray("fileColMd");
				for(int j = 0; j < cols.length(); j++){
					col = cols.getJSONObject(j);
					col.remove("colIndex");
				}
			}

			JSONArray expectedArray = new JSONArray();
			expectedArray.put(expected);
			JSONArray actualArray = new JSONArray();
			actualArray.put(actual);
			JSONAssert.assertEquals(expectedArray, actualArray, false);
		} catch (FileNotFoundException e) {
			fail();
			e.printStackTrace();
		}


		/*ObjectMapper mapper = new ObjectMapper();
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

//		assertTrue(expected.equals(actualMap));
		assertTrue(true);*/
	}

}
