package upb.ida.provider;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import upb.ida.bean.cluster.ClusterAlgoDesc;
import upb.ida.bean.cluster.ClusterParam;

@Component
public class DataDumpProvider {
	@Autowired
	public ServletContext context;
	
	public static final String DATADUMP_PATH = "./scikit/datadump/cluster_datadump.json";
	public static final String OPT_PARAM_REGEX = "[oO]ptional";
	public static final String MULT_PARAM_REGEX = "\\s*,\\s*|\\s*or\\s*";
	//Datadump field names
	public static final String DD_DATA = "data";
	public static final String DD_FN_NAME = "funcName";
	public static final String DD_FN_DESC = "funcDesc";
	public static final String DD_FN_NOTES = "notes";
	public static final String DD_FN_PARAMBODYARR = "funcParamBodyArr";
	public static final String DD_FN_ALLPARAMS = "allFuncParams";
	
	public static final String DD_FN_PARAM_NAME = "paramName";
	public static final String DD_FN_PARAM_TYPE = "type";
	public static final String DD_FN_PARAM_DESC = "desc";
	/**
	 * Method to generate a map of clustering algorithms in datadump json file
	 * @return map of clustering alogrithms
	 * @throws JsonGenerationException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	@Bean
	@Scope(value = "singleton", proxyMode = ScopedProxyMode.TARGET_CLASS)
	@Qualifier("scktClstrDtDmp")
	public Map<String, ClusterAlgoDesc> getScktClstrDtDmp()
			throws JsonGenerationException, JsonMappingException, IOException {
		Map<String, ClusterAlgoDesc> resMap = new HashMap<>();
		ObjectMapper mapper = new ObjectMapper();
		// Read datadump json
		File clstrDd = new File(context.getRealPath(DATADUMP_PATH));
		// Create Json Array
		ObjectReader reader = mapper.reader();
		ObjectNode objNode = (ObjectNode) reader.readTree(new FileInputStream(clstrDd));
		ArrayNode algoArr = (ArrayNode) objNode.get(DD_DATA);
		Iterator<JsonNode> algoItr = algoArr.iterator();
		ObjectNode clstrAlgo;
		String fnName;
		String fnDesc;
		String note;
		List<ClusterParam> paramList;
		ClusterAlgoDesc algoDesc;
		int i = 0;
		// Loop through array and create ClusterAlgoDesc instance
		while (algoItr.hasNext()) {
			clstrAlgo = (ObjectNode) algoItr.next();
			// select fnName
			fnName = clstrAlgo.get(DD_FN_NAME).asText();
			// select fnDesc
			fnDesc = clstrAlgo.get(DD_FN_DESC).asText();
			// select note
			note = clstrAlgo.get(DD_FN_NOTES).asText();
			// select params
			paramList = generateParamList((ArrayNode) clstrAlgo.get(DD_FN_ALLPARAMS),
					(ArrayNode) clstrAlgo.get(DD_FN_PARAMBODYARR));
			// add to map
			algoDesc = new ClusterAlgoDesc(++i, fnName, fnDesc, note, paramList);
			resMap.put(fnName, algoDesc);
		}
		return resMap;
	}
	/**
	 * Method to generate a list of parameters for a clustering algorithm from datadump
	 * @param paramArr - Array of all the parameter names
	 * @param paramBodyArr - Array of all the parameters along with the type and description
	 * @return List of CLustering algorithm parameters
	 */
	public List<ClusterParam> generateParamList(ArrayNode paramArr, ArrayNode paramBodyArr) {
		List<ClusterParam> paramList = new ArrayList<>();
		ObjectNode paramEntry;
		String paramName;
		String tempTypes;
		String desc;
		List<String> paramTypes;
		List<String> allTypeList;
		boolean optional;
		ClusterParam curParam;
		//Loop through all the elements in paramBodyArr
		Iterator<JsonNode> paramItr = paramBodyArr.iterator();
		while(paramItr.hasNext()) {
			paramEntry = (ObjectNode) paramItr.next();
			// fetch paramName
			paramName = paramEntry.get(DD_FN_PARAM_NAME).asText();
			// fetch paramtypes
			tempTypes = paramEntry.get(DD_FN_PARAM_TYPE).asText();
			allTypeList = Arrays.asList(tempTypes.split(MULT_PARAM_REGEX));
			paramTypes = fetchAllTypes(allTypeList);
			// check if optional
			optional = allTypeList.size() !=  paramTypes.size();
			// fetch desc
			desc = paramEntry.get(DD_FN_PARAM_DESC).asText();
			// create param object
			curParam = new ClusterParam(paramName, paramTypes, optional, desc);
			// add to list
			paramList.add(curParam);
		}
		return paramList;
	}
	/**
	 * Method to fetch all the types from a list of string except the keyword "optional"
	 * @param typeList - List of types
	 * @return list of types excluding the keyword "optional"
	 */
	public List<String> fetchAllTypes(List<String> typeList) {
		List<String> resList = new ArrayList<>();
		for(String entry: typeList) {
			if(entry.matches(OPT_PARAM_REGEX)) {
				continue;
			}
			resList.add(entry);
		}
		return resList;
	}

}
