package upb.ida.provider;

import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.rivescript.macro.Subroutine;

import upb.ida.bean.ResponseBean;
import upb.ida.bean.cluster.ClusterParam;
import upb.ida.constant.IDALiteral;
import upb.ida.temp.DemoMain;
import upb.ida.util.DataDumpUtil;
import upb.ida.util.GetCorrectParamTypes;
import upb.ida.util.KernelHttpRequest;
import upb.ida.util.SessionUtil;

@Component
public class ClusterDataGetter implements Subroutine {
	
	@Autowired
	private DemoMain DemoMain;
	@Autowired
	private ResponseBean responseBean;
	@Autowired
	private SessionUtil sessionUtil;
	@Autowired
	private ServletContext context;
	@Autowired
	private DataDumpUtil DataDumpUtil;
	/**
	 * Method to create and set response of clustering results 
	 * @param rs
	 *            - {@link call#rs}
	 * @param args
	 *            - {@link call#args}
	 * @return - String "pass" or "fail"
	 */

	public String call (com.rivescript.RiveScript rs, String[] args) {

		String columns;
		try {
			
			columns=args[0].replaceAll("\\sand\\s"," ");
			List<String> columnsForCluster = Arrays.asList(columns.split("\\s+"));
			String actvTbl = (String) responseBean.getPayload().get("actvTbl");
			String actvDs = (String) responseBean.getPayload().get("actvDs");
			//String actvScrId = (String) responseBean.getPayload().get("actvScrId");
			Map<String, Object> dataMap = responseBean.getPayload();
			String path = DemoMain.getFilePath(actvDs, actvTbl);

			File file = new File(context.getRealPath(path));
			List<Map<String, String>> lstt = DemoMain.convertToMap(file);
			ObjectMapper mapper = new ObjectMapper();
			ArrayNode nodeArr1 = mapper.createArrayNode();
			List <String> keys = new ArrayList <String> ();
			for(int i=0;i<columnsForCluster.size();i++) {
				keys.add(getMatchingKey(columnsForCluster.get(i), lstt.get(0)));
				
			}
            
			List<Object> outer_list = new ArrayList<>();
			/**
			 * getting Array list of columns from file provided by user  
			 */

			for (int i = 0; i < lstt.size(); i++) {
			
				List<Double> inner_list = new ArrayList<>();
				
				for(int x=0;x<keys.size();x++) {
				
					inner_list.add(Double.parseDouble(NumberFormat.getNumberInstance(java.util.Locale.US).parse(lstt.get(i).get(keys.get(x))).toString()));
				}
                
				outer_list.add(inner_list);
			}
			
			@SuppressWarnings("unchecked")
			Map<String , Object> paramList = (Map<String, Object>) sessionUtil.getSessionMap().get("clusterParams");
				GetCorrectParamTypes paramsMap= new GetCorrectParamTypes();
				HashMap<String, Object> mMap = new HashMap<String, Object>();
				String algoName=sessionUtil.getAlgoNameOrignal();
				List<ClusterParam> algoParams =DataDumpUtil.getClusterAlgoParams(algoName);
			
				/**
				 * function call to get data with corrected types
				 */
				mMap=paramsMap.correctTypeValues(paramList,algoName,algoParams);
				
				/**
				 * creating hash map to contain data , parameters
				 *  and algorithm name
				 */
		    	HashMap<String, Object> jsonDataForCluster = new HashMap<String, Object>();
				jsonDataForCluster.put("data", outer_list);
				jsonDataForCluster.put("params", mMap);
				jsonDataForCluster.put("algoname", sessionUtil.getAlgoNameOrignal());
				/**
				 * creating Array node of jsonDataForCluster - which is the data
				 *  on which clustering algorithm will be appied 
				 */
				nodeArr1.add(mapper.readTree(mapper.writeValueAsString(jsonDataForCluster)));
				nodeArr1.toString().replace('\"','\'');
				/**
				 * creating instance of kernelHttpRequest call to make an http request 
				 * to Jupyter kernel gateway server for running clustering on nodeArr1.
				 */
				KernelHttpRequest kernel=new KernelHttpRequest();
				List<String> clusterResult = kernel.getClusterResults(nodeArr1);
				columns=args[0].replaceAll("\\sand\\s"," ");
				String keyFeature=args[1];
				columns=keyFeature+" "+columns;
				List<String> columnsForResponse = Arrays.asList(columns.split("\\s+"));
				/**
				 * calling prepareResponseForCluster function to create response for user's request for clustering
				 * this function takes columns , path of file , clustered results and map of payload from reponsebean
				 *  as paramters.
				 */
				prepareResponseForCluster(columnsForResponse,path,clusterResult,dataMap,actvTbl);
				
			    /**
			     * updating payload in responseBean
			     */
				
//				responseBean.setActnCode(IDALiteral.UIA_FDG);
				
				
				return "pass";
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return "fail";
	
	}
	
	/** Method to get all columns 
	 * 
	 * @param key - column names provided by user
	 * @param dataMap - map of data fetched from file
	 * 
	 * @return - String
	 */
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
	
	/** Method to create response for user's request of clustering 
	 * 
	 * @param columnsForResponse - all columns for clustering.
	 * @param path - path of file.
	 * @param clusterResult - result that returned from jupyter server.
	 * @param dataMap - map to put data i  responseBean.
	 * @param actvTbl - gives the active table name. 
	 * 
	 * @throws - JsonProcessingException
	 * @throws - IOException
	 * @throws - NumberFormatException
	 * @throws - ParseException
	 * 
	 * 
	 */
	private  void prepareResponseForCluster(List<String> columnsForResponse,String path,List<String>clusterResult,Map<String,Object> dataMap,String actvTbl) throws JsonProcessingException, IOException, NumberFormatException, ParseException {
		
		List<Map<String, Object>> responseList=new ArrayList<>();
		File responseReader = new File(context.getRealPath(path));
		List<Map<String, String>> responseFileContent = DemoMain.convertToMap(responseReader);
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
		
		sessionUtil.getSessionMap().remove("clusterParams");

		sessionUtil.getSessionMap().remove("colledtedParams");
		dataMap.put("clusterData", responseList);
		//dataMap.put("tabLabel","Clustered"+" "+actvTbl);
		dataMap.put("tabLabel","Clustered Data");
		responseBean.setPayload(dataMap);
		responseBean.setActnCode(IDALiteral.UIA_CLUSTER);
		
	}
}


