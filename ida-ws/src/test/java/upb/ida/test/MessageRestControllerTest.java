package upb.ida.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.util.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.fasterxml.jackson.databind.node.ObjectNode;

import upb.ida.Application;
import upb.ida.bean.ResponseBean;
import upb.ida.fdg.FDG_Node;
import upb.ida.fdg.FDG_Triple;
import upb.ida.fdg.FdgUtil;
import upb.ida.rest.MessageRestController;

@RunWith(SpringRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = {Application.class})
public class MessageRestControllerTest {

	@Autowired
	private MessageRestController mrc;

	@Test
	public void sendmessagetestPos() throws Exception {
		ResponseBean responseBean;
		responseBean = mrc.sendmessage("I would like a force directed graph visualization for the current table", "1", "Continent", "test");
		responseBean = mrc.sendmessage("Source node is name", "1", "Continent", "test");
		responseBean = mrc.sendmessage("Target node is featurecode", "1", "Continent", "test");
		responseBean = mrc.sendmessage("Strength between the nodes should be represented by population", "1", "Continent", "test");

		System.out.println(responseBean.getPayload().get("fdgData"));

		String[] continents = {"Antarctica", "Oceania", "North America", "Asia", "Africa", "South America", "Europe"};
		String[] populations = {"0", "32000000", "528720588", "3879000000", "922011000", "382000000", "731000000"};
		List<Map<String, String>> dataMapList = new ArrayList<>();
		Map<String, String> row;
		for (int i = 0; i < continents.length; i++) {
			row = new HashMap<>();
			row.put("name", continents[i]);
			row.put("featurecode", "L.CONT");
			row.put("population", populations[i]);
			dataMapList.add(row);
		}

		double[] strngthValArr = new double[dataMapList.size()];
		int sindx = 0;
		int ndUniqueId = 1;
		int tripUniqueId = 1;
		ObjectNode res;
		List<FDG_Triple> tripleList = new ArrayList<>();
		// Create node map
		Map<String, FDG_Node> nodeMap = new HashMap<>();
		// Loop through the file map
		for (Map<String, String> entry : dataMapList) {
			String srcNdLbl = entry.get("name");
			String trgtNdLbl = entry.get("featurecode");
			String strngthVal = entry.get("population");
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
			Double strngthValD = Double.parseDouble(strngthVal);
			strngthValArr[sindx++] = strngthValD;
			// 3. Create Triple Object
			FDG_Triple triple = new FDG_Triple(tripUniqueId++, srcNd, trgtNd, strngthValD);
			tripleList.add(triple);
		}
		res = FdgUtil.getFDGData(tripleList, strngthValArr);
		ObjectNode actualNode = (ObjectNode) responseBean.getPayload().get("fdgData");
		System.out.println(res);
		assertEquals(actualNode, res);
	}

	@Test
	public void sendmessagetestNeg() throws Exception {
		ResponseBean responseBean;
		responseBean = mrc.sendmessage("I would like a force directed graph visualization for the current table", "1", "Continent", "test");
		responseBean = mrc.sendmessage("Source node is name", "1", "Continent", "test");
		responseBean = mrc.sendmessage("Target node is featurecode", "1", "Continent", "test");
		responseBean = mrc.sendmessage("Strength between the nodes should be represented by population", "1", "Continent", "test");

		System.out.println(responseBean.getPayload().get("fdgData"));

		String[] continents = {"Antarctica", "Oceania", "North America", "Asia", "Africa", "South America"};
		String[] populations = {"0", "32000000", "528720588", "3879000000", "922011000", "382000000"};
		List<Map<String, String>> dataMapList = new ArrayList<>();
		Map<String, String> row;
		for (int i = 0; i < continents.length; i++) {
			row = new HashMap<>();
			row.put("name", continents[i]);
			row.put("featurecode", "L.CONT");
			row.put("population", populations[i]);
			dataMapList.add(row);
		}


		double[] strngthValArr = new double[dataMapList.size()];
		int sindx = 0;
		int ndUniqueId = 1;
		int tripUniqueId = 1;
		ObjectNode res;
		List<FDG_Triple> tripleList = new ArrayList<>();
		// Create node map
		Map<String, FDG_Node> nodeMap = new HashMap<>();
		// Loop through the file map
		for (Map<String, String> entry : dataMapList) {
			String srcNdLbl = entry.get("name");
			String trgtNdLbl = entry.get("featurecode");
			String strngthVal = entry.get("population");
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
			Double strngthValD = Double.parseDouble(strngthVal);
			strngthValArr[sindx++] = strngthValD;
			// 3. Create Triple Object
			FDG_Triple triple = new FDG_Triple(tripUniqueId++, srcNd, trgtNd, strngthValD);
			tripleList.add(triple);
		}
		res = FdgUtil.getFDGData(tripleList, strngthValArr);
		ObjectNode actualNode = (ObjectNode) responseBean.getPayload().get("fdgData");
		System.out.println(res);
		assertNotEquals(actualNode, res);
	}

	@Test
	public void sendmessagetestExt() throws Exception {
		ResponseBean responseBean;
		responseBean = mrc.sendmessage("I would like a force directed graph visualization for the current table", "1", "Continent", "test");
		responseBean = mrc.sendmessage("Source node is name", "1", "Continent", "test");
		responseBean = mrc.sendmessage("Target node is featurecode", "1", "Continent", "test");
		responseBean = mrc.sendmessage("Strength between the nodes should be represented by population", "1", "Continent", "test");

		System.out.println(responseBean.getPayload().get("fdgData"));

		String[] continents = {"Antarctica", "Oceania", "North America", "Asia", "South Africa", "Latin America", "Europe"};
		String[] populations = {"0", "32000000", "528720588", "3879000000", "922011000", "382000000", "731000000"};
		List<Map<String, String>> dataMapList = new ArrayList<>();
		Map<String, String> row;
		for (int i = 0; i < continents.length; i++) {
			row = new HashMap<>();
			row.put("name", continents[i]);
			row.put("featurecode", "L.CONT");
			row.put("population", populations[i]);
			dataMapList.add(row);
		}

		double[] strngthValArr = new double[dataMapList.size()];
		int sindx = 0;
		int ndUniqueId = 1;
		int tripUniqueId = 1;
		ObjectNode res;
		List<FDG_Triple> tripleList = new ArrayList<>();
		// Create node map
		Map<String, FDG_Node> nodeMap = new HashMap<>();
		// Loop through the file map
		for (Map<String, String> entry : dataMapList) {
			String srcNdLbl = entry.get("name");
			String trgtNdLbl = entry.get("featurecode");
			String strngthVal = entry.get("population");
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
			Double strngthValD = Double.parseDouble(strngthVal);
			strngthValArr[sindx++] = strngthValD;
			// 3. Create Triple Object
			FDG_Triple triple = new FDG_Triple(tripUniqueId++, srcNd, trgtNd, strngthValD);
			tripleList.add(triple);
		}
		res = FdgUtil.getFDGData(tripleList, strngthValArr);
		ObjectNode actualNode = (ObjectNode) responseBean.getPayload().get("fdgData");
		System.out.println(res);
		assertNotEquals(actualNode, res);
	}
}
