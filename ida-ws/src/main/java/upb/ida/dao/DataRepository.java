package upb.ida.dao;

import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdfconnection.RDFConnectionFuseki;
import org.apache.jena.rdfconnection.RDFConnectionRemoteBuilder;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.*;

public class DataRepository {
	private static String dbhost = System.getenv("FUSEKI_URL");
	//	private String dbhost = "http://fuseki:8082/";
	private String datasetName = "";
	private String dbUrl = "";
	private Model model = null;
	private RDFConnectionFuseki conn = null;

	public DataRepository(String dataset, boolean isTest) {
		datasetName = dataset;
		dbUrl = dbhost + dataset;
		System.out.println("**********************");
		System.out.println(dbUrl);
		System.out.println("**********************");
		if (isTest) {
			try {
				model = ModelFactory.createDefaultModel();
				String path = getClass().getClassLoader().getResource("test.ttl").getFile();
				model.read(path);
			} catch (Exception ex) {
				System.out.println(ex.getMessage());
			}
		} else {
			try {
				RDFConnectionRemoteBuilder builder = RDFConnectionFuseki.create().destination(dbUrl);
				conn = (RDFConnectionFuseki) builder.build();
			} catch (Exception ex) {
				System.out.println(ex.getMessage());
			}
		}
	}

	/**
	 * @param queryString the SPARQL query to be executed on the RDF dataset
	 * @return It takes query string as its parameter and returns the result set after executing the query.
	 */
	public ResultSet getResultFromQuery(String queryString) {
		QueryExecution queryExecution = null;
		if (model == null) {
			try {
				Query query = QueryFactory.create(queryString);
				queryExecution = conn.query(query);
			} catch (Exception ex) {
				System.out.println(ex.getMessage());
			}
		} else {
			Query query = QueryFactory.create(queryString);
			queryExecution = QueryExecutionFactory.create(query, model);
		}
		return queryExecution.execSelect();
	}


	/**
	 * @return
	 * @throws Exception
	 */
	public JSONObject getSSDataSetMD() throws Exception {
		List<String> lstClass = new ArrayList<String>();
		Map<String, Integer> classCountMap = new HashMap<>();
		Set<String> distinctColumns = new TreeSet<>();
		Map<String, ArrayList<String>> columnMap = new HashMap<>();
		Map<String, String> columnCommentMap = new HashMap<>();
		Map<String, String> columnTypeMap = new HashMap<>();
		Map<String, String> columnLabelMap = new HashMap<>();
		String className = "";
		String columnName = "";
		Integer rowCount = 0;
		QuerySolution resource;
		Integer index;
		String filterPrefix = "?s = ";
		StringBuilder filterCondition = new StringBuilder();
		dbUrl = "http://127.0.0.1:3030/ssfuehrer";
		String queryString = RepoConstants.PREFIXES +
			"prefix datagraph: <" + dbUrl + "/data/data>\n" +
			"SELECT ?class (count(?class) as ?count)\n" +
			"FROM datagraph:\n" +
			"WHERE {\n" +
			"  \t?s rdf:type ?class;\n" +
			"    FILTER (?class != owl:NamedIndividual)\n" +
			"}\n" +
			"GROUP BY ?class";
		ResultSet resultSet = getResultFromQuery(queryString);
		while (resultSet.hasNext()) {
			resource = resultSet.next();
			className = resource.get("class").asNode().getURI();
			index = className.lastIndexOf("/");
			className = className.substring(index + 1);
			rowCount = Integer.parseInt(resource.get("count").asNode().getLiteralValue().toString());
			classCountMap.put(className, rowCount);
		}
		queryString = RepoConstants.PREFIXES +
			"prefix datagraph: <" + dbUrl + "/data/data>\n" +
			"SELECT DISTINCT ?class ?pred\n" +
			"FROM datagraph:\n" +
			"WHERE {\n" +
			"  \t?s ?pred ?o;\n" +
			"    ?pred [];\n" +
			"    rdf:type ?class;\n" +
			"    FILTER ( ?class != owl:NamedIndividual && (?pred != rdf:type))\n" +
			"}";
		resultSet = getResultFromQuery(queryString);
		while (resultSet != null && resultSet.hasNext()) {
			resource = resultSet.next();
			className = resource.get("class").asNode().getURI();
			index = className.lastIndexOf("/");
			className = className.substring(index + 1);
			columnName = resource.get("pred").asNode().getURI();
			distinctColumns.add(columnName);
			if (columnMap.get(className) != null) {
				columnMap.get(className).add(columnName);
			} else {
				columnMap.put(className, new ArrayList<String>(Arrays.asList(columnName)));
			}
		}
		Iterator columnsIterator = distinctColumns.iterator();
		while (columnsIterator.hasNext()) {
			filterCondition.append(filterPrefix).append("<").append(columnsIterator.next().toString().replace("/data/", "/ontology/")).append(">");
			if (columnsIterator.hasNext()) {
				filterCondition.append(" || ");
			}
		}
		if (!"".contentEquals(filterCondition)) {
			queryString = RepoConstants.PREFIXES +
				"prefix ontologygraph: <" + dbUrl + "/data/ontology>\n" +
				"SELECT DISTINCT (?s as ?column) ?comment ?type ?label\n" +
				"FROM ontologygraph:\n" +
				"WHERE { \n" +
				"  ?s\n" +
				"  rdfs:range ?type;\n" +
				"  rdfs:label ?label;\n" +
				"  OPTIONAL {\n" +
				"  \t?s rdfs:comment ?comment;\n" +
				"  }" +
				" 	FILTER (" + filterCondition + ")" +
				"}";
			resultSet = getResultFromQuery(queryString);
			String columnType = "";
			String comment = "";
			String label = "";
			while (resultSet != null && resultSet.hasNext()) {
				resource = resultSet.next();
				if (resource.get("type") != null) {
					columnType = resource.get("type").asNode().getURI();
					if (columnType.contains("#")) {
						index = columnType.lastIndexOf("#");
					} else {
						index = columnType.lastIndexOf("/");
					}
					columnType = columnType.substring(index + 1);
				} else {
					columnType = "";
				}
				if (resource.get("comment") != null) {
					comment = resource.get("comment").asNode().getLiteralValue().toString();
				} else {
					comment = "";
				}
				if (resource.get("column") != null) {
					label = resource.get("column").asNode().getURI().toString();
					if (label.contains("#")) {
						index = label.lastIndexOf("#");
					} else {
						index = label.lastIndexOf("/");
					}
					label = label.substring(index + 1);
				} else {
					label = "";
				}
				columnName = resource.get("column").asNode().getURI().replaceAll("ssdal/ontology", "ssdal/data");
				columnCommentMap.put(columnName, comment);
				columnTypeMap.put(columnName, columnType);
				columnLabelMap.put(columnName, label);
			}
		}
		JSONObject datasetInfo = new JSONObject();
		datasetInfo.put("dsName", "SSFuehrer");
		datasetInfo.put("dsDesc", "This dataset contains the data of SS from the historians.");
		JSONArray tables = new JSONArray();
		JSONArray columns;
		JSONObject table;
		JSONObject column;
		String classKey = "";
		for (String cls : classCountMap.keySet()) {
			classKey = cls.replaceAll(" ", "");
			if (columnMap.get(classKey) != null) {
				table = new JSONObject();
				columns = new JSONArray();
				index = 1;
				table.put("displayName", cls);
				table.put("fileName", cls);
				table.put("colCount", columnMap.get(classKey).size());
				table.put("rowCount", classCountMap.get(cls));
				for (String col : columnMap.get(classKey)) {
					column = new JSONObject();
					column.put("colIndex", index++);
					if (columnLabelMap.get(col) != null) {
						columnName = columnLabelMap.get(col);
					} else if (col.contains("#")) {
						columnName = col.substring(col.lastIndexOf("#") + 1);
					} else {
						columnName = col.substring(col.lastIndexOf("/") + 1);
					}
					column.put("colName", columnName);
					if (columnCommentMap.get(col) != null) {
						column.put("colDesc", columnCommentMap.get(col));
					} else {
						if ("label".equals(columnName)) {
							column.put("colDesc", "Label of a SSClass resource");
						}
					}
					if (columnTypeMap.get(col) != null) {
						column.put("colType", columnTypeMap.get(col));
					} else {
						if ("label".equals(columnName)) {
							column.put("colType", "string");
						}
					}
					columns.add(column);
				}
				table.put("fileColMd", columns);
				tables.add(table);
			}
		}
		datasetInfo.put("filesMd", tables);

		return datasetInfo;
	}

	public JSONArray getData(String className) throws Exception {
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
		String queryString = RepoConstants.PREFIXES +
			"prefix datagraph: <" + dbUrl + "/data/data>\n" +
			"SELECT *\n" +
			"FROM datagraph:\n" +
			"WHERE {\n" +
			"	?s a data:" + className + "; \n" +
			"	?p ?o;\n" +
			"   FILTER ( ?p != rdf:type)\n" +
			"}";
		ResultSet resultSet = getResultFromQuery(queryString);
		while (resultSet.hasNext()) {
			QuerySolution resource = resultSet.next();
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
				if(nestedColLst.contains(key)){
					if (rowObject.get(key) == null) {
						rowObject.put(key, value);
					} else {
						rowObject.put(key, "");
						duplicateColumnLst.add(key);
					}
				}else{
					if (rowObject.get(key) == null) {
						foreignRef = getResource(value);
						if(foreignRef == null){
							rowObject.put(key, value);
							nestedColLst.add(key);
						}else{
							for (String k : foreignRef.keySet()) {
								rowObject.put(key + "_" + k, foreignRef.get(k));
							}
						}
					} else {
						rowObject.put(key, "");
						duplicateColumnLst.add(key);
					}
				}
			}
			if (rowObject.get(key) == null) {
				rowObject.put(key, value);
			} else {
				rowObject.put(key, "");
				duplicateColumnLst.add(key);
			}
			rowsMap.put(id, rowObject);
		}
		for (String rowId : rowsMap.keySet()) {
			for(String col: duplicateColumnLst){
				rowsMap.get(rowId).remove(col);
			}
			rows.add(rowsMap.get(rowId));
		}
		return rows;
	}

	public Map<String, String> getResource(String sub) {
		Map<String, String> map = new HashMap<>();
		Map<String, String> childRef = new HashMap<>();
		String key = "";
		String value = "";
		QuerySolution resource;
		dbUrl = "http://127.0.0.1:3030/ssfuehrer";
		String queryString = RepoConstants.PREFIXES +
			"prefix datagraph: <" + dbUrl + "/data/data>\n" +
			"SELECT *\n" +
			"FROM datagraph:\n" +
			"WHERE {\n" +
			"	<" + sub + "> ?p ?o;\n" +
			"   FILTER ( ?p != rdf:type)\n" +
			"}";
		ResultSet resultSet = getResultFromQuery(queryString);
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
				return null;
				/*value = resource.get("o").asNode().getURI();
				childRef = getResource(value);
				for (String k : childRef.keySet()) {
					map.put(key + "_" + k, childRef.get(k));
				}*/
			}
		}
		return map;
	}
}


