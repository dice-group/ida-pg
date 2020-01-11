package upb.ida.test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonArray;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.json.simple.JSONArray;

import com.fasterxml.jackson.databind.node.ObjectNode;

import upb.ida.Application;
import upb.ida.bean.ResponseBean;
import upb.ida.dao.SoldierTimeLine;
import upb.ida.fdg.FDG_Node;
import upb.ida.fdg.FDG_Triple;
import upb.ida.fdg.FDG_Util;
import upb.ida.rest.MessageRestController;
@RunWith(SpringRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = {Application.class})
public class MessageRestControllerTest {

	@Autowired
	private MessageRestController mrc;

	@Test
	public void  sendmessagetestPos() throws Exception  {
		ResponseBean responseBean;
		responseBean = mrc.sendmessage("I would like a force directed graph visualization for the current table", "1", "citydistancetest.csv", "city");
		responseBean = mrc.sendmessage("Source node is city1", "1", "citydistancetest.csv", "city");
		responseBean = mrc.sendmessage("Target node is city2", "1", "citydistancetest.csv", "city");
		responseBean = mrc.sendmessage("Strength between the nodes should be represented by distance", "1", "citydistancetest.csv", "city");

		System.out.println(responseBean.getPayload().get("fdgData"));

		Map<String, String> row0= new HashMap<String, String> ();
		row0.put("city1","Berlin");
		row0.put("city2","Buenos Aires");
		row0.put("distance","7402");
		Map<String, String> row1= new HashMap<String, String> ();
		row1.put("city1","Berlin");
		row1.put("city2","Cairo");
		row1.put("distance","1795");
		Map<String, String> row2= new HashMap<String, String> ();
		row2.put("city1","Buenos Aires");
		row2.put("city2","Cairo");
		row2.put("distance","7345");

		List<Map<String, String>> dataMapList = new ArrayList<>();

		dataMapList.add(row0);
		dataMapList.add(row1);
		dataMapList.add(row2);

		double[] strngthValArr = new double[dataMapList.size()];
		int sindx = 0;
		int ndUniqueId = 1;
		int tripUniqueId = 1;
		ObjectNode res = null;
		List<FDG_Triple> tripleList = new ArrayList<>();
		// Create node map
		Map<String, FDG_Node> nodeMap = new HashMap<>();
		// Loop through the file map
				for (Map<String,String> entry : dataMapList) {
					String srcNdLbl = entry.get("city1");
					String trgtNdLbl = entry.get("city2");
					String strngthVal = entry.get("distance");
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
				res = FDG_Util.getFDGData(tripleList, strngthValArr);
				ObjectNode actualNode = (ObjectNode) responseBean.getPayload().get("fdgData");
				System.out.println(res);
				assertEquals(actualNode,res);



	}

	@Test
	public void  sendmessagetestNeg() throws Exception  {
		ResponseBean responseBean;
		responseBean = mrc.sendmessage("I would like a force directed graph visualization for the current table", "1", "citydistancetest.csv", "city");
		responseBean = mrc.sendmessage("Source node is city1", "1", "citydistancetest.csv", "city");
		responseBean = mrc.sendmessage("Target node is city2", "1", "citydistancetest.csv", "city");
		responseBean = mrc.sendmessage("Strength between the nodes should be represented by distance", "1", "citydistancetest.csv", "city");

		System.out.println(responseBean.getPayload().get("fdgData"));

		Map<String, String> row0= new HashMap<String, String> ();
		row0.put("city1","Berlin");
		row0.put("city2","Buenos Aires");
		row0.put("distance","7402");
		Map<String, String> row1= new HashMap<String, String> ();
		row1.put("city1","Berlin");
		row1.put("city2","Cairo");
		row1.put("distance","1795");


		List<Map<String, String>> dataMapList = new ArrayList<>();

		dataMapList.add(row0);
		dataMapList.add(row1);


		double[] strngthValArr = new double[dataMapList.size()];
		int sindx = 0;
		int ndUniqueId = 1;
		int tripUniqueId = 1;
		ObjectNode res = null;
		List<FDG_Triple> tripleList = new ArrayList<>();
		// Create node map
		Map<String, FDG_Node> nodeMap = new HashMap<>();
		// Loop through the file map
				for (Map<String,String> entry : dataMapList) {
					String srcNdLbl = entry.get("city1");
					String trgtNdLbl = entry.get("city2");
					String strngthVal = entry.get("distance");
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
				res = FDG_Util.getFDGData(tripleList, strngthValArr);
				ObjectNode actualNode = (ObjectNode) responseBean.getPayload().get("fdgData");
				System.out.println(res);
				assertNotEquals(actualNode,res);




	}

	@Test
	public void  sendmessagetestExt() throws Exception  {
		ResponseBean responseBean;
		responseBean = mrc.sendmessage("I would like a force directed graph visualization for the current table", "1", "citydistancetest.csv", "city");
		responseBean = mrc.sendmessage("Source node is city1", "1", "citydistancetest.csv", "city");
		responseBean = mrc.sendmessage("Target node is city2", "1", "citydistancetest.csv", "city");
		responseBean = mrc.sendmessage("Strength between the nodes should be represented by distance", "1", "citydistancetest.csv", "city");

		System.out.println(responseBean.getPayload().get("fdgData"));

		Map<String, String> row0= new HashMap<String, String> ();
		row0.put("city1","Berlin");
		row0.put("city2","Buenos Aires");
		row0.put("distance","7402");
		Map<String, String> row1= new HashMap<String, String> ();
		row1.put("city1","Berlin");
		row1.put("city2","Delhi");
		row1.put("distance","1795");
		Map<String, String> row2= new HashMap<String, String> ();
		row2.put("city1","Buenos Aires");
		row2.put("city2","Delhi");
		row2.put("distance","7345");

		List<Map<String, String>> dataMapList = new ArrayList<>();

		dataMapList.add(row0);
		dataMapList.add(row1);
		dataMapList.add(row2);

		double[] strngthValArr = new double[dataMapList.size()];
		int sindx = 0;
		int ndUniqueId = 1;
		int tripUniqueId = 1;
		ObjectNode res = null;
		List<FDG_Triple> tripleList = new ArrayList<>();
		// Create node map
		Map<String, FDG_Node> nodeMap = new HashMap<>();
		// Loop through the file map
				for (Map<String,String> entry : dataMapList) {
					String srcNdLbl = entry.get("city1");
					String trgtNdLbl = entry.get("city2");
					String strngthVal = entry.get("distance");
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
				res = FDG_Util.getFDGData(tripleList, strngthValArr);
				ObjectNode actualNode = (ObjectNode) responseBean.getPayload().get("fdgData");
				System.out.println(res);
				assertNotEquals(actualNode,res);



	}

	@Test
	public void  getSoldierDataTest() throws Exception
	{
		ResponseBean responseBean;
		responseBean = mrc.getSoldierData("47540");
		assertNotEquals(responseBean.getPayload(),null);

		SoldierTimeLine stl = new SoldierTimeLine("ssfuehrer");
		JSONArray rows = new JSONArray();
		rows.add("{\"membershipNumber\":\"279583\",\"hasRegiment\":[{\"applicableFrom_inXSDDate\":\"1938-12-01\",\"regimentInfo_abbreviation\":\"J. Sch. Braunschweig\",\"regimentInfo_label\":\"Junkerschule Braunschweig\",\"regimentInfo_id\":\"131\",\"label\":\"Soldier Regiment: 6138\",\"id\":\"6138\",\"regimentInfo_name\":\"Junkerschule Braunschweig\"}],\"lastName\":\"Nagel\",\"firstName\":\"August\",\"rankInfo\":[{\"applicableFrom_inXSDDate\":\"1937-05-01\",\"hasRank_sortation\":\"12\",\"hasRank_name\":\"Untersturmführer\",\"hasRank_id\":\"12\",\"hasRank_label\":\"Untersturmführer\",\"label\":\"Soldier Rank: 16002\",\"id\":\"16002\"},{\"applicableFrom_inXSDDate\":\"1938-09-11\",\"hasRank_sortation\":\"11\",\"hasRank_name\":\"Obersturmführer\",\"hasRank_id\":\"11\",\"hasRank_label\":\"Obersturmführer\",\"label\":\"Soldier Rank: 16003\",\"id\":\"16003\"}],\"DALVerified\":\"true\",\"literatureInfo\":[{\"hasLiterature_id\":\"1087\",\"hasLiterature_name\":\"5 \\/ DAL 1938-12-01\",\"originalDALPage\":\"266\",\"hasLiterature_label\":\"5 \\/ DAL 1938-12-01\",\"originalDALId\":\"6394\",\"hasLiterature_publicationYear\":\"1938\",\"hasLiterature_citaviId\":\"11920\",\"label\":\"Soldier Literature: 6132\",\"id\":\"6132\"}],\"decorationInfo\":[{\"applicableFrom_inXSDDate\":\"1938-12-01\",\"hasDecoration_name\":\"22 \\/ Lebensborn\",\"hasDecoration_abbreviation\":\"Lebensborn\",\"hasDecoration_label\":\"22 \\/ Lebensborn\",\"label\":\"Soldier Decoration: 24078\",\"id\":\"24078\",\"hasDecoration_id\":\"319\"},{\"applicableFrom_inXSDDate\":\"1938-12-01\",\"hasDecoration_name\":\"14 \\/ Ehrendegen des RFSS\",\"hasDecoration_abbreviation\":\"SS-Degen\",\"hasDecoration_label\":\"14 \\/ Ehrendegen des RFSS\",\"label\":\"Soldier Decoration: 24077\",\"id\":\"24077\",\"hasDecoration_id\":\"311\"}],\"id\":\"47540\",\"label\":\"August, Nagel\",\"birthDate\":\"1901-01-04\",\"NSDAPNumber\":\"4377736\"}");
		stl.preProcessSoldierData(rows);
	}



}
