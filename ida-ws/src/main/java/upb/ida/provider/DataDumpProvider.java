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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

	public static Pattern optParamSel = Pattern
			.compile("\\s*(\\w*)\\s*:\\s*([\\w\\s,\\.\\{\\}\\-]*)\\s*(\\(?[oO]ptional\\)?)");
	public static Pattern allParamSel = Pattern.compile("\\s*(\\w*)\\s*:\\s*([\\w\\s,\\.\\{\\}\\-]*)\\s*");

	@Bean
	@Scope(value = "singleton", proxyMode = ScopedProxyMode.TARGET_CLASS)
	@Qualifier("scktClstrDtDmp")
	public Map<String, ClusterAlgoDesc> getScktClstrDtDmp()
			throws JsonGenerationException, JsonMappingException, IOException {
		Map<String, ClusterAlgoDesc> resMap = new HashMap<>();
		ObjectMapper mapper = new ObjectMapper();
		// Read datadump json
		File clstrDd = new File(context.getRealPath("./scikit/datadump/cluster_datadump.json"));
		// Create Json Array
		ObjectReader reader = mapper.reader();
		ObjectNode objNode = (ObjectNode) reader.readTree(new FileInputStream(clstrDd));
		ArrayNode algoArr = (ArrayNode) objNode.get("data");
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
			fnName = clstrAlgo.get("funcName").asText();
			// select fnDesc
			fnDesc = clstrAlgo.get("funcDesc").asText();
			// select note
			note = clstrAlgo.get("notes").asText();
			// select params
			paramList = generateParamList((ArrayNode) clstrAlgo.get("allFuncParams"),
					clstrAlgo.get("funcParamBody").asText());
			// add to map
			algoDesc = new ClusterAlgoDesc(++i, fnName, fnDesc, note, paramList);
			resMap.put(fnName, algoDesc);
		}
		return resMap;
	}

	public List<ClusterParam> generateParamList(ArrayNode paramArr, String paramStr) {
		List<ClusterParam> paramList = new ArrayList<>();
		// Fetch all params and types
		Matcher allMatcher = allParamSel.matcher(paramStr);
		// Fetch optional params and types
		Matcher optMatcher = optParamSel.matcher(paramStr);
		List<String> optionalParams = new ArrayList<>();
		while (optMatcher.find()) {
			optionalParams.add(optMatcher.group(1).trim());
		}
		ClusterParam curParam;
		String paramName;
		List<String> paramTypes;
		boolean optional;
		while (allMatcher.find()) {
			// fetch paramName
			paramName = allMatcher.group(1).trim();
			// fetch paramtypes
			paramTypes = fetchAllTypes(allMatcher.group(2).trim());
			// check if optional
			optional = optionalParams.contains(paramName);
			// create param object
			curParam = new ClusterParam(paramName, paramTypes, optional);
			// add to list
			paramList.add(curParam);
		}

		return paramList;
	}

	public List<String> fetchAllTypes(String typeStr) {
		List<String> resList = Arrays.asList(typeStr.split("\\s*,\\s*|\\s*or\\s*"));
		return resList;
	}

}
