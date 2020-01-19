package upb.ida.provider;

import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.rivescript.macro.Subroutine;
import upb.ida.bean.ResponseBean;
import upb.ida.bean.cluster.ClusterParam;
import upb.ida.constant.IDALiteral;
import upb.ida.dao.DataRepository;
import upb.ida.util.CodeGeneratorUtil;
import upb.ida.util.DataDumpUtil;
import upb.ida.util.FileUtil;
import upb.ida.util.GetCorrectParamTypes;
import upb.ida.util.SessionUtil;

@Component
public class ClusterDataGetter implements Subroutine {

	@Autowired
	private FileUtil demoMain;
	@Autowired
	private ResponseBean responseBean;
	@Autowired
	private SessionUtil sessionUtil;
	@Autowired
	private DataDumpUtil DataDumpUtil;
	@Autowired
	private CodeGeneratorUtil generatorUtil;

	/**
	 * Method to create and set response of clustering results
	 *
	 * @param rs   -
	 * @param args -
	 * @return - String "pass" or "fail"
	 */

	public String call(com.rivescript.RiveScript rs, String[] args) {

		String columns;
		try {

			columns = args[0].replaceAll("\\sand\\s", " ");
			List<String> columnsForCluster = Arrays.asList(columns.split("\\s+"));
			String actvTbl = (String) responseBean.getPayload().get("actvTbl");
			String actvDs = (String) responseBean.getPayload().get("actvDs");
			//String actvScrId = (String) responseBean.getPayload().get("actvScrId");
			Map<String, Object> dataMap = responseBean.getPayload();
			DataRepository dataRepository = new DataRepository(false);

			List<Map<String, String>> lstt = dataRepository.getData(actvTbl, actvDs);
			List<String> keys = new ArrayList<String>();
			for (int i = 0; i < columnsForCluster.size(); i++) {
				keys.add(getMatchingKey(columnsForCluster.get(i), lstt.get(0)));

			}

			List<List<Double>> outer_list = new ArrayList<>();
			/**
			 * getting Array list of columns from file provided by user
			 */

			for (int i = 0; i < lstt.size(); i++) {

				List<Double> inner_list = new ArrayList<>();
				for (int x = 0; x < keys.size(); x++) {

					/**
					 * Some entries might not have data for selected column. assuming the default value to be 0.0 for those entries.
					 */
					if (lstt.get(i).get(keys.get(x)) != null) {
						inner_list.add(Double.parseDouble(NumberFormat.getNumberInstance(java.util.Locale.US).parse(lstt.get(i).get(keys.get(x))).toString()));
					} else {
						inner_list.add(0.0);
					}
				}
				outer_list.add(inner_list);
			}

			@SuppressWarnings("unchecked")
			Map<String, Object> paramList = (Map<String, Object>) sessionUtil.getSessionMap().get("clusterParams");
			GetCorrectParamTypes paramsMap = new GetCorrectParamTypes();
			HashMap<String, Object> mMap = new HashMap<String, Object>();
			String algoName = (String) sessionUtil.getSessionMap().get("algoName");
			List<ClusterParam> algoParams = DataDumpUtil.getClusterAlgoParams(algoName);
			mMap = paramsMap.correctTypeValues(paramList, algoName, algoParams);
			/**
			 * creating hash map to contain data , parameters
			 *  and algorithm name
			 */
			HashMap<String, Object> jsonDataForCluster = new HashMap<String, Object>();
			jsonDataForCluster.put("data", outer_list);
			jsonDataForCluster.put("params", mMap);
			jsonDataForCluster.put("algoname", algoName);
			/**
			 * creating Array node of jsonDataForCluster - which is the data
			 *  on which clustering algorithm will be appied
			 */

			/**
			 * creating instance of kernelHttpRequest call to make an http request
			 * to Jupyter kernel gateway server for running clustering on nodeArr1.
			 */
			sessionUtil.getSessionMap().remove("clusterParams");
			sessionUtil.getSessionMap().remove("colledtedParams");
			sessionUtil.getSessionMap().remove("algoName");

			List<Long> clusterResult = generatorUtil.performClustering(algoName, outer_list, mMap);

			columns = args[0].replaceAll("\\sand\\s", " ");
			String keyFeature = args[1];
			columns = keyFeature + " " + columns;
			List<String> columnsForResponse = Arrays.asList(columns.split("\\s+"));
			prepareResponseForCluster(columnsForResponse, actvTbl, clusterResult, dataMap, actvDs);

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

	/**
	 * Method to get all columns
	 *
	 * @param key     - column names provided by user
	 * @param dataMap - map of data fetched from file
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

	/**
	 * Method to create response for user's request of clustering
	 *
	 * @param columnsForResponse - all columns for clustering.
	 * @param actvTbl               - name of the current table.
	 * @param clusterResult      - result that returned from jupyter server.
	 * @param dataMap            - map to put data i  responseBean.
	 * @param actvTbl            - gives the active table name.
	 * @throws - JsonProcessingException
	 * @throws - IOException
	 * @throws - NumberFormatException
	 * @throws - ParseException
	 */
	private void prepareResponseForCluster(List<String> columnsForResponse, String actvTbl, List<Long> clusterResult, Map<String, Object> dataMap, String actvDs) throws JsonProcessingException, IOException, NumberFormatException, ParseException {

		List<Map<String, Object>> responseList = new ArrayList<>();
//		File responseReader = new File(demoMain.fetchSysFilePath(path));
		DataRepository dataRepository = new DataRepository(false);
//		List<Map<String, String>> responseFileContent = demoMain.convertToMap(responseReader);
		List<Map<String, String>> responseFileContent = dataRepository.getData(actvTbl, actvDs);
		List<String> responseColumnsKeyValue = new ArrayList<String>();
		for (int i = 0; i < columnsForResponse.size(); i++) {
			responseColumnsKeyValue.add(getMatchingKey(columnsForResponse.get(i), responseFileContent.get(0)));

		}
		for (int i = 0; i < responseFileContent.size(); i++) {

			Map<String, Object> innerMap = new HashMap<String, Object>();
			for (int x = 0; x < responseColumnsKeyValue.size(); x++) {
				if (x == 0) {
					innerMap.put(columnsForResponse.get(x), responseFileContent.get(i).get(responseColumnsKeyValue.get(x)));

				} else {
					if(responseFileContent.get(i).get(responseColumnsKeyValue.get(x)) == null) {
						innerMap.put(columnsForResponse.get(x), 0.0);
					}else {
						innerMap.put(columnsForResponse.get(x), Double.parseDouble(NumberFormat.getNumberInstance(java.util.Locale.US).parse(responseFileContent.get(i).get(responseColumnsKeyValue.get(x))).toString()));
					}
				}
			}
			innerMap.put("clusterLabel", clusterResult.get(i));
			responseList.add(innerMap);
		}


		dataMap.put("clusterData", responseList);
		//dataMap.put("tabLabel","Clustered"+" "+actvTbl);
		dataMap.put("tabLabel", "Clustered Data");
		responseBean.setPayload(dataMap);
		responseBean.setActnCode(IDALiteral.UIA_CLUSTER);

	}
}


