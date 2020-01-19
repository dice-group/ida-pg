package upb.ida.dao;

import org.apache.jena.graph.Triple;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.ResultSetFactory;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdfconnection.RDFConnectionFuseki;
import org.apache.jena.rdfconnection.RDFConnectionRemoteBuilder;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class SoldierTimeLine {
	private final String PREFIXES = "PREFIX datagraph: <http://127.0.0.1:3030/ssfuehrer/data/data>\n" +
			"PREFIX data: <https://www.upb.de/historisches-institut/neueste-geschichte/ssdal/data/>\n" +
			"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
			"PREFIX dbo: <http://dbpedia.org/ontology/>\n" +
			"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
			"PREFIX soldierData: <https://www.upb.de/historisches-institut/neueste-geschichte/ssdal/data/soldier/>";
	private static String dbhost = System.getenv("FUSEKI_URL");
	private Model model = null;
	private RDFConnectionFuseki conn = null;
	private Map<String, Map<String, String>> soldierDataMap = new HashMap<String, Map<String, String>>();
	private Map<String, String> soldierDatesMap = new TreeMap<String, String>();
	private Map<String, String> soldierCorrectDatesMap = new HashMap<String, String>();
	private Map<String, String> soldierAllDOBMap = new HashMap<String, String>();
	private Map<String, String> soldierAllRanksMap = new HashMap<String, String>();
	private Map<String, String> soldierAllRegimentsMap = new HashMap<String, String>();
	private Map<String, String> soldierAllDecorationsMap = new HashMap<String, String>();
	private int datesFlag = 0;


	/**
	 * @param queryString the SPARQL query to be executed on the RDF dataset
	 * @return It takes query string as its parameter and returns the result set after executing the query.
	 */
	public ResultSet getResultFromQuery(String queryString, String dataset) {
		QueryExecution queryExecution;
		ResultSet resultSet;
		Query query = QueryFactory.create(queryString);


		/*
		 * No need to create a model from file or make database connection if the query is being run on already existing model. ( multiple queries are run on same model from getData function.)
		 */
		if (model == null) {
			/*
			 *	Create a fuseki model from the file and run the query on that model for test cases.
			 */
			if ("test".equals(dataset)) {
				try {
					model = ModelFactory.createDefaultModel();
					String path = Objects.requireNonNull(getClass().getClassLoader().getResource("dataset/test-soldier.ttl")).getFile();
					model.read(path);
					queryExecution = QueryExecutionFactory.create(query, model);
				} catch (NullPointerException ex) {
					return null;
				}
			} else {
				try {
					RDFConnectionRemoteBuilder builder = RDFConnectionFuseki.create().destination(dbhost + dataset);
					conn = (RDFConnectionFuseki) builder.build();
					queryExecution = conn.query(query);
				} catch (Exception ex) {
					return null;
				} finally {
					conn.close();
				}
			}
		} else {
			queryExecution = QueryExecutionFactory.create(query, model);
		}
		if (queryExecution != null) {
			try {
				resultSet = ResultSetFactory.copyResults(queryExecution.execSelect());
				queryExecution.close();
				return resultSet;
			} catch (Exception e) {
				return null;
			}
		}
		return null;
	}

	public Map<String, Map<String, String>> getData(String soldierId, String dataset) {
		datesFlag = 0;
		soldierDataMap = new HashMap<String, Map<String, String>>();
		soldierDatesMap = new TreeMap<String, String>();
		soldierCorrectDatesMap = new HashMap<String, String>();
		soldierAllDOBMap = new HashMap<String, String>();
		soldierAllRanksMap = new HashMap<String, String>();
		soldierAllRegimentsMap = new HashMap<String, String>();
		soldierAllDecorationsMap = new HashMap<String, String>();
		model = null;
		String qString = "SELECT ?subject ?predicate ?object \n WHERE { ?subject ?predicate ?object }";

		ResultSet resultSet = getResultFromQuery(qString, dataset);
		model = ModelFactory.createDefaultModel();
		QuerySolution s;
		Triple t;

		while (resultSet.hasNext()) {
			s = resultSet.nextSolution();
			t = Triple.create(s.get("subject").asNode(), s.get("predicate").asNode(), s.get("object").asNode());
			model.add(model.asStatement(t));
		}

		String queryString = PREFIXES + "SELECT ?s ?p ?o\n" + "WHERE {\n" + "  ?s ?p ?o\n" + "  FILTER( ?s = soldierData:" + soldierId + " && ?p != rdf:type)\n" + "}";

		JSONArray rows = queryData(queryString, dataset);
		return preProcessSoldierData(rows);
	}

	public JSONArray queryData(String queryString, String dataset) {
		ResultSet resultSet = getResultFromQuery(queryString, dataset);
		QuerySolution resource;
		JSONArray rows = new JSONArray();
		Map<String, JSONObject> rowsMap = new HashMap<>();
		Map<String, String> foreignRef;
		JSONObject rowObject;
		String id = "";
		String key = "";
		String value = "";
		Set<String> duplicateColumnLst = new TreeSet<>();
		Set<String> nestedColLst = new TreeSet<>();
		JSONObject childObj = new JSONObject();
		JSONArray childLst = new JSONArray();

		while (resultSet.hasNext()) {
			resource = resultSet.next();
			id = resource.get("s").asNode().getURI();
			id = id.substring(id.lastIndexOf("/") + 1);
			key = resource.get("p").asNode().getURI();

			if (key.contains("#")) {
				key = key.substring(key.lastIndexOf("#") + 1);
			} else {
				key = key.substring(key.lastIndexOf("/") + 1);
			}

			if (rowsMap.get(id) == null) {
				rowObject = new JSONObject();
			} else {
				rowObject = rowsMap.get(id);
			}

			if (resource.get("o").isLiteral()) {
				value = resource.get("o").asLiteral().getString();
				if (rowObject.get(key) == null) {
					rowObject.put(key, value);
				} else {
					rowObject.put(key, "");
					duplicateColumnLst.add(key);
				}
			} else {
				value = resource.get("o").asNode().getURI();
				if (nestedColLst.contains(key)) {
					if (rowObject.get(key) == null) {
						rowObject.put(key, value);
					} else {
						rowObject.put(key, "");
						duplicateColumnLst.add(key);
					}
				} else {
					foreignRef = getResource(value, dataset);

					for (String k : foreignRef.keySet()) {
						childObj.put(k, foreignRef.get(k));
					}

					if (rowObject.get(key) != null) {
						childLst = (JSONArray) rowObject.get(key);
					}
					childLst.add(childObj);
					rowObject.put(key, childLst);

					childObj = new JSONObject();
					childLst = new JSONArray();
				}
			}
			rowsMap.put(id, rowObject);
		}

		for (String rowId : rowsMap.keySet()) {
			for (String col : duplicateColumnLst) {
				rowsMap.get(rowId).remove(col);
			}
			rows.add(rowsMap.get(rowId));
		}
		return rows;
	}

	public Map<String, Map<String, String>> preProcessSoldierData(JSONArray rows) {
		Map<String, String> tempStringVsStringMap;
		JSONObject eachSoldierData;

		for (int i = 0; i < rows.size(); i++) {
			eachSoldierData = (JSONObject) rows.get(i);

			tempStringVsStringMap = new HashMap<>();

			for (Object key : eachSoldierData.keySet()) {
				if(eachSoldierData.get(key) instanceof String){
					tempStringVsStringMap.put(key.toString(), eachSoldierData.get(key).toString());
				}
			}

			if (eachSoldierData.get("birthDate") != null) {
				soldierAllDOBMap.put(eachSoldierData.get("birthDate").toString() + "__" + datesFlag, " ");
				soldierDatesMap.put(eachSoldierData.get("birthDate").toString() + "__" + datesFlag, "basicInfo__" + String.valueOf(i));
				soldierCorrectDatesMap.put(eachSoldierData.get("birthDate").toString() + "__" + datesFlag, eachSoldierData.get("birthDate").toString());
				datesFlag++;
			}
			soldierDataMap.put("basicInfo__" + i, tempStringVsStringMap);

			extractDecorationData(eachSoldierData);
			extractRegimentData(eachSoldierData);
			extractRankData(eachSoldierData);
		}

		soldierDataMap.put("dates", soldierDatesMap);
		soldierDataMap.put("correctDates", soldierCorrectDatesMap);
		soldierDataMap.put("allDOB", soldierAllDOBMap);
		soldierDataMap.put("allRanks", soldierAllRanksMap);
		soldierDataMap.put("allDecoration", soldierAllDecorationsMap);
		soldierDataMap.put("allRegiment", soldierAllRegimentsMap);

		return soldierDataMap;
	}


	public void extractDecorationData(JSONObject eachSoldierData) {
		JSONObject decorationInfoObject;
		JSONArray decorationInformation;
		Map<String, String> tempStringVsStringMap;

		decorationInformation = (JSONArray) eachSoldierData.get("decorationInfo");

		for (int j = 0; j < decorationInformation.size(); j++) {
			decorationInfoObject = (JSONObject) decorationInformation.get(j);
			tempStringVsStringMap = new HashMap<>();
			for (Object key : decorationInfoObject.keySet()) {
				if(decorationInfoObject.get(key) instanceof String){
					tempStringVsStringMap.put(key.toString(), decorationInfoObject.get(key).toString());
				}
			}
			if (decorationInfoObject.get("applicableFrom_inXSDDate") != null) {
				soldierAllDecorationsMap.put(decorationInfoObject.get("applicableFrom_inXSDDate").toString() + "__" + datesFlag, " ");
				soldierDatesMap.put(decorationInfoObject.get("applicableFrom_inXSDDate").toString() + "__" + datesFlag, "decorationInfo__" + j);
				soldierCorrectDatesMap.put(decorationInfoObject.get("applicableFrom_inXSDDate").toString() + "__" + datesFlag, decorationInfoObject.get("applicableFrom_inXSDDate").toString());
				datesFlag++;
			}
			soldierDataMap.put("decorationInfo__" + j, tempStringVsStringMap);
		}
	}

	public void extractRegimentData(JSONObject eachSoldierData) {
		JSONObject regimentInfoObject;
		JSONArray regimentInformation;
		Map<String, String> tempStringVsStringMap;

		regimentInformation = (JSONArray) eachSoldierData.get("hasRegiment");
		for (int j = 0; j < regimentInformation.size(); j++) {
			regimentInfoObject = (JSONObject) regimentInformation.get(j);

			tempStringVsStringMap = new HashMap<>();
			for (Object key : regimentInfoObject.keySet()) {
				if(regimentInfoObject.get(key) instanceof String){
					tempStringVsStringMap.put(key.toString(), regimentInfoObject.get(key).toString());
				}
			}
			if (regimentInfoObject.get("applicableFrom_inXSDDate") != null) {
				soldierAllRegimentsMap.put(regimentInfoObject.get("applicableFrom_inXSDDate").toString() + "__" + datesFlag, " ");
				soldierDatesMap.put(regimentInfoObject.get("applicableFrom_inXSDDate").toString() + "__" + datesFlag, "regimentInfo__" + j);
				soldierCorrectDatesMap.put(regimentInfoObject.get("applicableFrom_inXSDDate").toString() + "__" + datesFlag, regimentInfoObject.get("applicableFrom_inXSDDate").toString());
				datesFlag++;
			}
			soldierDataMap.put("regimentInfo__" + j, tempStringVsStringMap);
		}
	}

	public void extractRankData(JSONObject eachSoldierData) {
		JSONObject rankInfoObject;
		JSONArray rankInformation;
		Map<String, String> tempStringVsStringMap;

		rankInformation = (JSONArray) eachSoldierData.get("rankInfo");
		for (int j = 0; j < rankInformation.size(); j++) {
			rankInfoObject = (JSONObject) rankInformation.get(j);
			tempStringVsStringMap = new HashMap<>();

			for (Object key : rankInfoObject.keySet()) {
				if(rankInfoObject.get(key) instanceof String){
					tempStringVsStringMap.put(key.toString(), rankInfoObject.get(key).toString());
				}
			}
			if (rankInfoObject.get("applicableFrom_inXSDDate") != null) {
				soldierAllRanksMap.put(rankInfoObject.get("applicableFrom_inXSDDate").toString() + "__" + datesFlag, " ");
				soldierDatesMap.put(rankInfoObject.get("applicableFrom_inXSDDate").toString() + "__" + datesFlag, "rankInfo__" + j);
				soldierCorrectDatesMap.put(rankInfoObject.get("applicableFrom_inXSDDate").toString() + "__" + datesFlag, rankInfoObject.get("applicableFrom_inXSDDate").toString());
				datesFlag++;
			}
			soldierDataMap.put("rankInfo__" + j, tempStringVsStringMap);
		}
	}

	public Map<String, String> getResource(String sub, String dataset) {
		Map<String, String> map = new HashMap<>();
		Map<String, String> childRef = new HashMap<>();
		String key = "";
		String value = "";
		QuerySolution resource;
		String queryString = PREFIXES +
				"SELECT *\n" +
				"WHERE {\n" +
				"	<" + sub + "> ?p ?o;\n" +
				"   FILTER ( ?p != rdf:type)\n" +
				"}";
		ResultSet resultSet = getResultFromQuery(queryString, dataset);
		while (resultSet.hasNext()) {
			resource = resultSet.next();
			key = resource.get("p").asNode().getURI();

			if (key.contains("#")) {
				key = key.substring(key.lastIndexOf("#") + 1);
			} else {
				key = key.substring(key.lastIndexOf("/") + 1);
			}

			if (resource.get("o").isLiteral()) {
				value = resource.get("o").asLiteral().getString();
				map.put(key, value);
			} else {
				value = resource.get("o").asNode().getURI();
				childRef = getResource(value, dataset);
				for (String k : childRef.keySet()) {
					map.put(key + "_" + k, childRef.get(k));
				}
				/*JSONObject childObj = new JSONObject();
				JSONArray childLst = new JSONArray();
				for (String k : childRef.keySet()) {
					childObj.put(k, childRef.get(k));
				}
				childLst.add(childObj);
				map.put(key, childLst.toString());*/
			}
		}
		return map;
	}
}
