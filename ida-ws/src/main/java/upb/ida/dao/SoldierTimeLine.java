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

import java.util.*;

public class SoldierTimeLine
{
	private static String dbhost = "http://127.0.0.1:3030/";
	//private String dbhost = "http://fuseki:8082/";
	//private String datasetName = "";
	private String dbUrl = "";
	private Model model = null;
	private RDFConnectionFuseki conn = null;
	private final String PREFIXES = "PREFIX datagraph: <http://127.0.0.1:3030/ssfuehrer/data/data>\n" +
			"PREFIX data: <https://www.upb.de/historisches-institut/neueste-geschichte/ssdal/data/>\n" +
			"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
			"PREFIX dbo: <http://dbpedia.org/ontology/>\n" +
			"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
			"PREFIX soldierData: <https://www.upb.de/historisches-institut/neueste-geschichte/ssdal/data/soldier/>";

	public SoldierTimeLine(String dataset, boolean isTest)
	{
		//datasetName = dataset;
		if (System.getenv("FUSEKI_URL") != null)
		{
			dbhost = System.getenv("FUSEKI_URL");
		}

		dbUrl = dbhost + dataset;
		/*System.out.println("**********************");
		System.out.println(dbUrl);
		System.out.println("**********************");
		*/
		if (isTest)
		{
			try
			{
				model = ModelFactory.createDefaultModel();
				String path = getClass().getClassLoader().getResource("test.ttl").getFile();
				model.read(path);
			}
			catch (Exception ex)
			{
				System.out.println(ex.getMessage());
			}
		}
		else
		{
			try
			{
				RDFConnectionRemoteBuilder builder = RDFConnectionFuseki.create().destination(dbUrl);
				conn = (RDFConnectionFuseki) builder.build();
			}
			catch (Exception ex)
			{
				System.out.println(ex.getMessage());
			}
		}
	}

	/**
	 * @param queryString the SPARQL query to be executed on the RDF dataset
	 * @return It takes query string as its parameter and returns the result set after executing the query.
	 */
	public ResultSet getResultFromQuery(String queryString)
	{
		ResultSet returnVaule;
		QueryExecution queryExecution = null;

		if (model == null)
		{
			try
			{
				RDFConnectionRemoteBuilder builder = RDFConnectionFuseki.create().destination(dbUrl);
				conn = (RDFConnectionFuseki) builder.build();
				Query query = QueryFactory.create(queryString);
				queryExecution = conn.query(query);
				conn.close();
			}
			catch (Exception ex)
			{
				System.out.println(ex.getMessage());
			}
		}
		else
		{
			Query query = QueryFactory.create(queryString);
			queryExecution = QueryExecutionFactory.create(query, model);
		}

		returnVaule = ResultSetFactory.copyResults(queryExecution.execSelect());
		queryExecution.close();

		return returnVaule;
	}

	public Map<String,Map<String,String>> getData(String soldierId)
	{
		JSONArray rows = new JSONArray();
		Map<String, JSONObject> rowsMap = new HashMap<>();
		Map<String, String> foreignRef;
		JSONObject rowObject;
		String id = "";
		String key = "";
		String value = "";
		Set<String> duplicateColumnLst = new TreeSet<>();
		Set<String> nestedColLst = new TreeSet<>();

		dbUrl = "http://127.0.0.1:3030/ssfuehrer";

		String qString = "PREFIX datagraph: <http://127.0.0.1:3030/ssfuehrer/data/data>\nSELECT ?subject ?predicate ?object \n FROM datagraph: \n WHERE { ?subject ?predicate ?object }";

		ResultSet resultSet = getResultFromQuery(qString);
		List<Triple> triples = new ArrayList<>();

		model = ModelFactory.createDefaultModel();
		QuerySolution s;
		Triple t;

		while (resultSet.hasNext())
		{
			s = resultSet.nextSolution();
			t = Triple.create(s.get("subject").asNode(), s.get("predicate").asNode(), s.get("object").asNode());
			triples.add(t);
		}

		for (Triple tri : triples)
		{
			model.add(model.asStatement(tri));
		}

		String queryString = PREFIXES +
				"SELECT ?s ?p ?o\n" +
//				"FROM datagraph:\n" +
				"WHERE {\n" +
				"  ?s ?p ?o\n" +
				"  FILTER( ?s = soldierData:" + soldierId + " && ?p != rdf:type)\n" +
				"}";

		resultSet = getResultFromQuery(queryString);
		QuerySolution resource;
		JSONObject childObj = new JSONObject();
		JSONArray childLst = new JSONArray();

		while (resultSet.hasNext())
		{
			resource = resultSet.next();
			id = resource.get("s").asNode().getURI();
			id = id.substring(id.lastIndexOf("/") + 1);
			key = resource.get("p").asNode().getURI();

			if (key.contains("#"))
			{
				key = key.substring(key.lastIndexOf("#") + 1);
			}
			else
			{
				key = key.substring(key.lastIndexOf("/") + 1);
			}

			if (rowsMap.get(id) == null)
			{
				rowObject = new JSONObject();
			}
			else
			{
				rowObject = rowsMap.get(id);
			}

			if (resource.get("o").isLiteral())
			{
				value = resource.get("o").asLiteral().getString();

				if (rowObject.get(key) == null)
				{
					rowObject.put(key, value);
				}
				else
				{
					rowObject.put(key, "");
					duplicateColumnLst.add(key);
				}
			}
			else
			{
				value = resource.get("o").asNode().getURI();
				if (nestedColLst.contains(key))
				{
					if (rowObject.get(key) == null)
					{
						rowObject.put(key, value);
					}
					else
					{
						rowObject.put(key, "");
						duplicateColumnLst.add(key);
					}
				}
				else
				{
					foreignRef = getResource(value);

					for (String k : foreignRef.keySet())
					{
						childObj.put(k, foreignRef.get(k));
					}

					if (rowObject.get(key) != null)
					{
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

		for (String rowId : rowsMap.keySet())
		{
			for (String col : duplicateColumnLst)
			{
				rowsMap.get(rowId).remove(col);
			}

			rows.add(rowsMap.get(rowId));
		}

		//Map<String,Map<String,String>> returingData =  preProcessSoldierData(rows);

		Map<String,Map<String,String>> soldierDataMap = new HashMap<String,Map<String,String>>();
		Map<String,String> tempStringVsStringMap;
		Map<String,String> soldierDatesMap = new TreeMap<String,String>();
		Map<String,String> soldierCorrectDatesMap = new HashMap<String,String>();
		Map<String,String> soldierAllDOBMap = new HashMap<String,String>();
		Map<String,String> soldierAllRanksMap = new HashMap<String,String>();
		Map<String,String> soldierAllRegimentsMap = new HashMap<String,String>();
		Map<String,String> soldierAllDecorationsMap = new HashMap<String,String>();

		int datesFlag = 0;
		JSONObject eachSoldierData,decorationInfoObject,regimentInfoObject,rankInfoObject;
		JSONArray decorationInformation,regimentInformation,rankInformation;

		for (int i = 0; i < rows.size(); i++)
		{
			eachSoldierData = (JSONObject) rows.get(i);

			tempStringVsStringMap = new HashMap<String,String>();
			tempStringVsStringMap.put("membershipNumber",eachSoldierData.get("membershipNumber").toString());
			tempStringVsStringMap.put("DALVerified",eachSoldierData.get("DALVerified").toString());
			tempStringVsStringMap.put("NSDAPNumber",eachSoldierData.get("NSDAPNumber").toString());
			tempStringVsStringMap.put("birthDate",eachSoldierData.get("birthDate").toString());
			tempStringVsStringMap.put("firstName",eachSoldierData.get("firstName").toString());
			tempStringVsStringMap.put("id",eachSoldierData.get("id").toString());
			tempStringVsStringMap.put("label",eachSoldierData.get("label").toString());

			soldierAllDOBMap.put(eachSoldierData.get("birthDate").toString()+"__"+ String.valueOf(datesFlag)," ");

			soldierDatesMap.put(eachSoldierData.get("birthDate").toString()+"__"+ String.valueOf(datesFlag),"basicInfo__"+String.valueOf(i));
			soldierCorrectDatesMap.put(eachSoldierData.get("birthDate").toString()+"__"+ String.valueOf(datesFlag),eachSoldierData.get("birthDate").toString());
			datesFlag ++;
			soldierDataMap.put("basicInfo__"+String.valueOf(i),new HashMap<String, String>());
			soldierDataMap.put("basicInfo__"+String.valueOf(i),tempStringVsStringMap);


			decorationInformation = (JSONArray) eachSoldierData.get("decorationInfo");

			for(int j=0;j < decorationInformation.size();j++)
			{
				decorationInfoObject = (JSONObject) decorationInformation.get(j);
				tempStringVsStringMap = new HashMap<String,String>();

				tempStringVsStringMap.put("label",decorationInfoObject.get("label").toString());
				tempStringVsStringMap.put("id",decorationInfoObject.get("id").toString());
				tempStringVsStringMap.put("applicableFrom_inXSDDate",decorationInfoObject.get("applicableFrom_inXSDDate").toString());
				tempStringVsStringMap.put("hasDecoration_name",decorationInfoObject.get("hasDecoration_name").toString());
				tempStringVsStringMap.put("hasDecoration_abbreviation",decorationInfoObject.get("hasDecoration_abbreviation").toString());
				tempStringVsStringMap.put("hasDecoration_label",decorationInfoObject.get("hasDecoration_label").toString());
				tempStringVsStringMap.put("hasDecoration_id",decorationInfoObject.get("id").toString());

				soldierAllDecorationsMap.put(decorationInfoObject.get("applicableFrom_inXSDDate").toString()+"__"+ String.valueOf(datesFlag)," ");

				soldierDatesMap.put(decorationInfoObject.get("applicableFrom_inXSDDate").toString()+"__"+ String.valueOf(datesFlag),"decorationInfo__"+String.valueOf(j));
				soldierCorrectDatesMap.put(decorationInfoObject.get("applicableFrom_inXSDDate").toString()+"__"+ String.valueOf(datesFlag),decorationInfoObject.get("applicableFrom_inXSDDate").toString());
				datesFlag ++;
				soldierDataMap.put("decorationInfo__"+String.valueOf(j),new HashMap<String, String>());
				soldierDataMap.put("decorationInfo__"+String.valueOf(j),tempStringVsStringMap);
				//tempStringVsStringMap.clear();
			}

			regimentInformation = (JSONArray) eachSoldierData.get("hasRegiment");
			for(int j=0;j < regimentInformation.size();j++)
			{
				regimentInfoObject = (JSONObject) regimentInformation.get(j);

				tempStringVsStringMap = new HashMap<String,String>();
				tempStringVsStringMap.put("label",regimentInfoObject.get("label").toString());
				tempStringVsStringMap.put("id",regimentInfoObject.get("id").toString());
				tempStringVsStringMap.put("applicableFrom_inXSDDate",regimentInfoObject.get("applicableFrom_inXSDDate").toString());
				tempStringVsStringMap.put("regimentInfo_abbreviation",regimentInfoObject.get("regimentInfo_abbreviation").toString());
				tempStringVsStringMap.put("regimentInfo_id",regimentInfoObject.get("regimentInfo_id").toString());
				tempStringVsStringMap.put("regimentInfo_label",regimentInfoObject.get("regimentInfo_label").toString());
				tempStringVsStringMap.put("regimentInfo_name",regimentInfoObject.get("regimentInfo_name").toString());

				soldierAllRegimentsMap.put(regimentInfoObject.get("applicableFrom_inXSDDate").toString()+"__"+ String.valueOf(datesFlag)," ");

				soldierDatesMap.put(regimentInfoObject.get("applicableFrom_inXSDDate").toString()+"__"+ String.valueOf(datesFlag),"regimentInfo__"+j);
				soldierCorrectDatesMap.put(regimentInfoObject.get("applicableFrom_inXSDDate").toString()+"__"+ String.valueOf(datesFlag),regimentInfoObject.get("applicableFrom_inXSDDate").toString());
				datesFlag ++;
				soldierDataMap.put("regimentInfo__"+j,new HashMap<String, String>());
				soldierDataMap.put("regimentInfo__"+j,tempStringVsStringMap);
				//tempStringVsStringMap.clear();

			}

			/*JSONArray literatureInformation = (JSONArray) eachSoldierData.get("literatureInfo");
			for(int j=0;j < literatureInformation.size();j++)
			{
				JSONObject literatureInfoObject = (JSONObject) literatureInformation.get(j);

				tempStringVsStringMap.put("label",literatureInfoObject.get("label").toString());
				tempStringVsStringMap.put("id",literatureInfoObject.get("id").toString());
				tempStringVsStringMap.put("hasLiterature_citaviId",literatureInfoObject.get("hasLiterature_citaviId").toString());
				tempStringVsStringMap.put("regimentInfo_abbreviation",literatureInfoObject.get("regimentInfo_abbreviation").toString());
				tempStringVsStringMap.put("regimentInfo_id",literatureInfoObject.get("regimentInfo_id").toString());
				tempStringVsStringMap.put("regimentInfo_label",literatureInfoObject.get("regimentInfo_label").toString());
				tempStringVsStringMap.put("regimentInfo_name",literatureInfoObject.get("regimentInfo_name").toString());

				soldierDatesMap.put(literatureInfoObject.get("applicableFrom_inXSDDate").toString(),"regimentInfo"+j);

				soldierDataMap.put("regimentInfo"+j,new HashMap<String, String>());
				soldierDataMap.put("regimentInfo"+j,tempStringVsStringMap);
				tempStringVsStringMap.clear();

			}
			*/

			rankInformation = (JSONArray) eachSoldierData.get("rankInfo");
			for(int j=0;j < rankInformation.size();j++)
			{
				rankInfoObject = (JSONObject) rankInformation.get(j);
				tempStringVsStringMap = new HashMap<String,String>();

				tempStringVsStringMap.put("label",rankInfoObject.get("label").toString());
				tempStringVsStringMap.put("id",rankInfoObject.get("id").toString());
				tempStringVsStringMap.put("applicableFrom_inXSDDate",rankInfoObject.get("applicableFrom_inXSDDate").toString());
				tempStringVsStringMap.put("hasRank_id",rankInfoObject.get("hasRank_id").toString());
				tempStringVsStringMap.put("hasRank_label",rankInfoObject.get("hasRank_label").toString());
				tempStringVsStringMap.put("hasRank_name",rankInfoObject.get("hasRank_name").toString());
				tempStringVsStringMap.put("hasRank_sortation",rankInfoObject.get("hasRank_sortation").toString());

				soldierAllRanksMap.put(rankInfoObject.get("applicableFrom_inXSDDate").toString()+"__"+ String.valueOf(datesFlag)," ");

				soldierDatesMap.put(rankInfoObject.get("applicableFrom_inXSDDate").toString()+"__"+ String.valueOf(datesFlag),"rankInfo__"+String.valueOf(j));
				soldierCorrectDatesMap.put(rankInfoObject.get("applicableFrom_inXSDDate").toString()+"__"+ String.valueOf(datesFlag),rankInfoObject.get("applicableFrom_inXSDDate").toString());

				datesFlag ++;
				soldierDataMap.put("rankInfo__"+String.valueOf(j),new HashMap<String, String>());
				soldierDataMap.put("rankInfo__"+String.valueOf(j),tempStringVsStringMap);
				//tempStringVsStringMap.clear();
			}
		}

		soldierDataMap.put("dates",new TreeMap<String, String>());
		soldierDataMap.put("dates",soldierDatesMap);

		soldierDataMap.put("correctDates",new HashMap<String, String>());
		soldierDataMap.put("correctDates",soldierCorrectDatesMap);


		soldierDataMap.put("allDOB",new HashMap<String, String>());
		soldierDataMap.put("allDOB",soldierAllDOBMap);

		soldierDataMap.put("allRanks",new HashMap<String, String>());
		soldierDataMap.put("allRanks",soldierAllRanksMap);

		soldierDataMap.put("allDecoration",new HashMap<String, String>());
		soldierDataMap.put("allDecoration",soldierAllDecorationsMap);

		soldierDataMap.put("allRegiment",new HashMap<String, String>());
		soldierDataMap.put("allRegiment",soldierAllRegimentsMap);

		return soldierDataMap;
	}

	public Map<String, String> getResource(String sub)
	{
		Map<String, String> map = new HashMap<>();
		Map<String, String> childRef = new HashMap<>();
		String key = "";
		String value = "";
		QuerySolution resource;
		dbUrl = "http://127.0.0.1:3030/ssfuehrer";
		String queryString = PREFIXES +
				"prefix datagraph: <" + dbUrl + "/data/data>\n" +
				"SELECT *\n" +
//				"FROM datagraph:\n" +
				"WHERE {\n" +
				"	<" + sub + "> ?p ?o;\n" +
				"   FILTER ( ?p != rdf:type)\n" +
				"}";
		ResultSet resultSet = getResultFromQuery(queryString);
		while (resultSet.hasNext())
		{
			resource = resultSet.next();
			key = resource.get("p").asNode().getURI();

			if (key.contains("#"))
			{
				key = key.substring(key.lastIndexOf("#") + 1);
			}
			else
			{
				key = key.substring(key.lastIndexOf("/") + 1);
			}

			if (resource.get("o").isLiteral())
			{
				value = resource.get("o").asLiteral().getString();
				map.put(key, value);
			}
			else
			{
				value = resource.get("o").asNode().getURI();
				childRef = getResource(value);
				for (String k : childRef.keySet())
				{
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

