package upb.ida.fdg;

import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletContext;

import org.apache.commons.math3.stat.StatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import upb.ida.temp.DemoMain;

@Component
public class FDG_Util {
	@Autowired
	DemoMain dem;
	@Autowired
	ServletContext context;
	public static final GsonBuilder builder = new GsonBuilder();
	public static final Gson gson = builder.create();
	public static final int MAX_STR = 10;

	/**
	 * Method to convert a list of triples into formatted json data for FDG
	 * Visualization
	 * 
	 * @param triples
	 *            - list of triples
	 * @return jsonobject of data
	 * @throws IOException 
	 * @throws JsonProcessingException 
	 */
	public static ObjectNode getFDGData(List<FDG_Triple> triples, double[] strngthValArr) throws JsonProcessingException, IOException {
		double max = StatUtils.max(strngthValArr);
		double min = StatUtils.min(strngthValArr);
		
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode rootNode = mapper.createObjectNode();
		
		ArrayNode nodeArr1 = mapper.createArrayNode();
		ArrayNode edgeArr1 = mapper.createArrayNode();
		
		rootNode.set("nodes", nodeArr1);
		rootNode.set("links", edgeArr1);
		
		Set<FDG_Node> nodeSet = new HashSet<>();
		FDG_Node srcNd, trgtNd;
		ObjectNode edgeNode;
		boolean isNew;
		for (FDG_Triple entry : triples) {
			srcNd = entry.getSourceNode();
			trgtNd = entry.getTargetNode();
			// Add nodes to the set
			isNew = nodeSet.add(srcNd);
			if (isNew) {
				nodeArr1.add(mapper.readTree(mapper.writeValueAsString(srcNd)));
			}
			isNew = nodeSet.add(trgtNd);
			if (isNew)
				nodeArr1.add(mapper.readTree(mapper.writeValueAsString(trgtNd)));
			// Add the edge
			edgeNode = mapper.createObjectNode();
			edgeArr1.add(edgeNode);
			// add the properties
			edgeNode.put("id", entry.getId());
			edgeNode.put("source", entry.getSourceNode().getId());
			edgeNode.put("target", entry.getTargetNode().getId());
			// TODO: Normalization needed between 1-10 (Use standard deviation to decide the
			// strength of the edge, calculate mean, min and max; then normalize the
			// deviation between min - mean to max - mean
			// edgeNode.put("str_val", entry.getStr_val());
			edgeNode.put("str_val", calcNrmlStrngth(entry.getStrngthVal(), max, min));
			if (entry.getLabel() != null)
				edgeNode.put("label", entry.getLabel());
		}
		return rootNode;
	}

	public ObjectNode generateFDG(String filePath, String srcNodeFtr, String trgtNodeFtr, String strngthFtr)
			throws JsonProcessingException, IOException, ParseException {
		ObjectNode res = null;
		int ndUniqueId = 1;
		int tripUniqueId = 1;
		File file = new File(context.getRealPath(filePath));
		List<FDG_Triple> tripleList = new ArrayList<>();

		// Fetch the map for the file
		List<Map<String, String>> dataMapList = dem.convertToMap(file);
		double[] strngthValArr = new double[dataMapList.size()];
		int sindx = 0;
		// Create node map
		Map<String, FDG_Node> nodeMap = new HashMap<>();
		// Loop through the file map
		for (Map<String, String> entry : dataMapList) {
			String srcNdLbl = entry.get(srcNodeFtr);
			String trgtNdLbl = entry.get(trgtNodeFtr);
			String strngthVal = entry.get(strngthFtr);
			// 1. Create/Fetch Source Node in/from map
			FDG_Node srcNd = nodeMap.get(srcNdLbl);
			if (srcNd == null) {
				srcNd = new FDG_Node(ndUniqueId++, srcNdLbl, 1);
				nodeMap.put(srcNdLbl, srcNd);
			}
			// 2. Create Target Node in/from map
			FDG_Node trgtNd = nodeMap.get(trgtNdLbl);
			if (trgtNd == null) {
				trgtNd = new FDG_Node(ndUniqueId++, trgtNdLbl, 1);
				nodeMap.put(trgtNdLbl, trgtNd);
			}
			Double strngthValD = Double
					.parseDouble(NumberFormat.getNumberInstance(java.util.Locale.US).parse(strngthVal).toString());
			strngthValArr[sindx++] = strngthValD;
			// 3. Create Triple Object
			FDG_Triple triple = new FDG_Triple(tripUniqueId++, srcNd, trgtNd, strngthValD);
			tripleList.add(triple);
		}
		// Call FDGData
		res = getFDGData(tripleList, strngthValArr);
		return res;
	}

	public static double calcNrmlStrngth(double num, double max, double min) {
		double res = 0;
		res = ((0.9)*(num - min)/(max - min))+ 0.1;
		return res;
	}

}
