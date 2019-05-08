package upb.ida.test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.io.File;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import upb.ida.Application;
import upb.ida.bean.ResponseBean;
import upb.ida.constant.IDALiteral;
import upb.ida.rest.MessageRestController;
import upb.ida.util.FileUtil;
@RunWith(SpringRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = {Application.class})

public class MessageRestControllerClusterTest {
	
	 @Autowired
	private FileUtil demoMain;
	
	@Autowired
	private MessageRestController mrc;
	@Test
	public void  sendmessagetestpos() throws Exception  {
		ResponseBean responseBean;
		responseBean = mrc.sendmessage("What are the available clustering algorithms?", "1", "movehubcostofliving.csv", "city");
		responseBean = mrc.sendmessage("I would like to run the KMeans algorithm on the current table", "1", "movehubcostofliving.csv", "city");
		responseBean = mrc.sendmessage("Optional parameters should be n_clusters, n_jobs and n_init", "1", "movehubcostofliving.csv", "city");
		responseBean = mrc.sendmessage("Set n_clusters as 5", "1", "movehubcostofliving.csv", "city");
		responseBean = mrc.sendmessage("Set n_jobs as 8", "1", "movehubcostofliving.csv", "city");
		responseBean = mrc.sendmessage("Set init as random", "1", "movehubcostofliving.csv", "city");
		responseBean = mrc.sendmessage("Set n_init as 5", "1", "movehubcostofliving.csv", "city");
		responseBean = mrc.sendmessage("Set precompute_distances as auto", "1", "movehubcostofliving.csv", "city"); 
		responseBean = mrc.sendmessage("Clustering features are wine, cinema and gasoline", "1", "movehubcostofliving.csv", "city");
		responseBean = mrc.sendmessage("Label feature should be city", "1", "movehubcostofliving.csv", "city");
		
		
		
		List<String> clusterResult = new ArrayList<String>();
		clusterResult.add("2");
		clusterResult.add("2");
		clusterResult.add("0");
		clusterResult.add("2");
		clusterResult.add("0");
		clusterResult.add("4");
		clusterResult.add("0");
		clusterResult.add("4");
		clusterResult.add("3");
		clusterResult.add("1");
		clusterResult.add("0");
		Map<String,Object> dataMap = new HashMap<String,Object>();
		dataMap.put("actvScrId", "1");
		dataMap.put("actvDs", "city");
		dataMap.put("actvTbl", "movehubcostofliving.csv");
		
		List<String> columnsForResponse = new ArrayList<String>();
		columnsForResponse.add("city");
		columnsForResponse.add("wine");
		columnsForResponse.add("cinema");
		columnsForResponse.add("gasoline");
		
		List<Map<String, Object>> responseList=new ArrayList<>();
		File responseReader = new File(demoMain.fetchSysFilePath("dataset/city/movehubcostofliving.csv"));
		List<Map<String, String>> responseFileContent = demoMain.convertToMap(responseReader);
		List <String> responseColumnsKeyValue = new ArrayList <String> ();
		for(int i=0;i<columnsForResponse.size();i++) {
			responseColumnsKeyValue.add(getMatchingKey(columnsForResponse.get(i), responseFileContent.get(0)));
			
		}
		for (int i = 0; i < responseFileContent.size(); i++) {
		
			Map<String,Object> innerMap=new HashMap<String,Object>();
			for(int x=0;x<responseColumnsKeyValue.size();x++) {
			    if(x==0) {
					innerMap.put(columnsForResponse.get(x),responseFileContent.get(i).get(responseColumnsKeyValue.get(x)));
					
			    }
			    else {
				innerMap.put(columnsForResponse.get(x),Double.parseDouble(NumberFormat.getNumberInstance(java.util.Locale.US).parse(responseFileContent.get(i).get(responseColumnsKeyValue.get(x))).toString()));
			    }
			}
			innerMap.put("clusterLabel",Integer.parseInt(clusterResult.get(i)));
			responseList.add(innerMap);
		}
		
		
		dataMap.put("clusterData", responseList);
		
		dataMap.put("tabLabel","Clustered Data");
		responseBean.setPayload(dataMap);
		responseBean.setActnCode(IDALiteral.UIA_CLUSTER);
		
		
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> predicted = (List<Map<String, Object>>) responseBean.getPayload().get("clusterData");
		assertEquals(predicted,responseList);
		
		
		
	}
	
	
	
	
	 @Test
	public void  sendmessagetestNeg() throws Exception  {
		ResponseBean responseBean;
		responseBean = mrc.sendmessage("What are the available clustering algorithms?", "1", "movehubcostofliving.csv", "city");
		responseBean = mrc.sendmessage("I would like to run the KMeans algorithm on the current table", "1", "movehubcostofliving.csv", "city");
		responseBean = mrc.sendmessage("Optional parameters should be init and n_init", "1", "movehubcostofliving.csv", "city");
		responseBean = mrc.sendmessage("Set n_clusters as 5", "1", "movehubcostofliving.csv", "city");
		responseBean = mrc.sendmessage("Set n_jobs as 8", "1", "movehubcostofliving.csv", "city");
		responseBean = mrc.sendmessage("Set init as random", "1", "movehubcostofliving.csv", "city");
		responseBean = mrc.sendmessage("Set n_init as 5", "1", "movehubcostofliving.csv", "city");
		responseBean = mrc.sendmessage("set precompute_distances as auto", "1", "movehubcostofliving.csv", "city");
		responseBean = mrc.sendmessage("Clustering features are wine, cinema and gasoline", "1", "movehubcostofliving.csv", "city");
		responseBean = mrc.sendmessage("Label feature should be city", "1", "movehubcostofliving.csv", "city");
		
		
		
		List<String> clusterResult = new ArrayList<String>();
		clusterResult.add("2");
		clusterResult.add("2");
		clusterResult.add("5");
		clusterResult.add("2");
		clusterResult.add("0");
		clusterResult.add("4");
		clusterResult.add("0");
		clusterResult.add("4");
		clusterResult.add("3");
		clusterResult.add("1");
		clusterResult.add("0");
		Map<String,Object> dataMap = new HashMap<String,Object>();
		dataMap.put("actvScrId", "1");
		dataMap.put("actvDs", "city");
		dataMap.put("actvTbl", "movehubcostofliving.csv");
		
		List<String> columnsForResponse = new ArrayList<String>();
		columnsForResponse.add("city");
		columnsForResponse.add("wine");
		columnsForResponse.add("cinema");
		
		
		List<Map<String, Object>> responseList=new ArrayList<>();
		File responseReader = new File(demoMain.fetchSysFilePath("dataset/city/movehubcostofliving.csv"));
		List<Map<String, String>> responseFileContent = demoMain.convertToMap(responseReader);
		List <String> responseColumnsKeyValue = new ArrayList <String> ();
		for(int i=0;i<columnsForResponse.size();i++) {
			responseColumnsKeyValue.add(getMatchingKey(columnsForResponse.get(i), responseFileContent.get(0)));
			
		}
		for (int i = 0; i < responseFileContent.size(); i++) {
		
			Map<String,Object> innerMap=new HashMap<String,Object>();
			for(int x=0;x<responseColumnsKeyValue.size();x++) {
			    if(x==0) {
					innerMap.put(columnsForResponse.get(x),responseFileContent.get(i).get(responseColumnsKeyValue.get(x)));
					
			    }
			    else {
				innerMap.put(columnsForResponse.get(x),Double.parseDouble(NumberFormat.getNumberInstance(java.util.Locale.US).parse(responseFileContent.get(i).get(responseColumnsKeyValue.get(x))).toString()));
			    }
			}

			if(i<=clusterResult.size())
			{
				innerMap.put("clusterLabel",Integer.parseInt(clusterResult.get(i)));
				responseList.add(innerMap);
			}
		}
		
		
		dataMap.put("clusterData", responseList);
		
		
		
		
		
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> predicted = (List<Map<String, Object>>) responseBean.getPayload().get("clusterData");
		
		
		
		
		assertNotEquals(responseList.get(0).keySet(),predicted.get(0).keySet());
		
		
	}
	

	
	
	//* Method to get all the columns
	
	private String getMatchingKey(String key, Map<String, String> dataMap) {
		Set<String> keySet = dataMap.keySet();
		String res = null;
		for (String entry : keySet) {
			if (key.trim().equalsIgnoreCase(entry)) {
				res = entry;
				break;
			}
		}
		return res;
	}
	
}


