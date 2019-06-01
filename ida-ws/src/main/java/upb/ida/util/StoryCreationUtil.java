package upb.ida.util;

import org.springframework.stereotype.Component;
import upb.ida.bean.ResponseBean;
import upb.ida.constant.IDALiteral;
import java.io.IOException;
import java.util.*;

/**
 * Exposes util methods for Story Creation
 */
@Component
public class StoryCreationUtil {

	/**
	 * Method to create response for bar gaph visualization request
	 *
	 * @param args -  arguments of for filtering
	 * @param responseBean -  ResponseBean is used as a uniform response 
	 * format for the incoming REST calls
	 * @return - void
	 */
	public void generateStory(String[] args, ResponseBean responseBean)
			throws IOException {

		// Fetch active dataset details
		String actvTbl = (String) responseBean.getPayload().get("actvTbl");
		String actvDs = (String) responseBean.getPayload().get("actvDs");
		String actvScrId = (String) responseBean.getPayload().get("actvScrid");
		String actvVz = (String) responseBean.getPayload().get("actvVz");

		//Create url for stroy
		UUID uuid = UUID.randomUUID();
		String uuidStr = uuid.toString();
		String urlStory = "/getStory/" + uuidStr;

		Map<String, Object> dataMap = responseBean.getPayload();
		dataMap.put("storyUrl", urlStory);
		responseBean.setPayload(dataMap);
		responseBean.setActnCode(IDALiteral.UIA_SC);
	}
}
