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
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import upb.ida.Application;
import upb.ida.bean.ResponseBean;
import upb.ida.rest.MessageRestController;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Map;

import static org.junit.Assert.fail;


@RunWith(SpringRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = {Application.class})
@TestPropertySource("classpath:application.properties")
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
			@SuppressWarnings("unchecked")
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
	}

}
