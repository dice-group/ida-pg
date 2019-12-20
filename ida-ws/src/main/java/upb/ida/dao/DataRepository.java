package upb.ida.dao;

import org.apache.jena.graph.Triple;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdfconnection.RDFConnectionFuseki;
import org.apache.jena.rdfconnection.RDFConnectionRemoteBuilder;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.*;

/**
 * Class to make fuseki database calls.
 *
 * @author Nandeesh
 */
public class DataRepository {
	private static String dbhost = System.getenv("FUSEKI_URL");
	private Model model = null;
	private RDFConnectionFuseki conn = null;
	private String dbUrl = "";
	private boolean isTest;

	public DataRepository(String dataset, boolean isTest) {
		dbUrl = dbhost + dataset;
		this.isTest = isTest;
	}

	/**
	 * @param queryString the SPARQL query to be executed on the RDF dataset
	 * @return It takes query string as its parameter and returns the result set after executing the query.
	 */
	public ResultSet getResultFromQuery(String dataset, String queryString) {
		QueryExecution queryExecution = null;
		ResultSet resultSet = null;
		Query query = QueryFactory.create(queryString);

		if (model == null) {
			if (isTest || "test-data".equals(dataset) || "test-ontology".equals(dataset)) {
				try {
					model = ModelFactory.createDefaultModel();
					String path = Objects.requireNonNull(getClass().getClassLoader().getResource("dataset/test.ttl")).getFile();
					model.read(path);
					queryExecution = QueryExecutionFactory.create(query, model);
				} catch (NullPointerException ex) {
					System.out.println(ex.getMessage());
					return null;
				}
			} else {
				try {
					RDFConnectionRemoteBuilder builder = RDFConnectionFuseki.create().destination(dbhost + dataset);
					conn = (RDFConnectionFuseki) builder.build();
					queryExecution = conn.query(query);
				} catch (Exception ex) {
					System.out.println(ex.getMessage());
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


	/**
	 * @return - Metadata of SSFuehrer dataset.
	 */
	public Map<String, Object> getDataSetMD(String dataset) {
		Map<String, Integer> classCountMap = new HashMap<>();
		Set<String> distinctColumns = new TreeSet<>();
		Map<String, ArrayList<String>> columnMap = new HashMap<>();
		Map<String, String> columnCommentMap = new HashMap<>();
		Map<String, String> columnTypeMap = new HashMap<>();
		Map<String, String> columnLabelMap = new HashMap<>();
		Map<String, String> classBaseUrlMap = new HashMap<>();
		String className;
		String columnName;
		int rowCount;
		QuerySolution resource;
		int index;
		String filterPrefix = "?s = ";
		StringBuilder filterCondition = new StringBuilder();
		String queryString = RepoConstants.PREFIXES +
				"SELECT ?class (count(?class) as ?count)\n" +
				"WHERE {\n" +
				"  \t?s rdf:type ?class;\n" +
				"    FILTER (?class != owl:NamedIndividual)\n" +
				"}\n" +
				"GROUP BY ?class";
		ResultSet resultSet = getResultFromQuery(dataset + "-data", queryString);
		if (resultSet == null) {
			return null;
		}
		while (resultSet.hasNext()) {
			resource = resultSet.next();
			className = resource.get("class").asNode().getURI();
			if(className.contains("#")){
				index = className.lastIndexOf("#");
			}else{
				index = className.lastIndexOf("/");
			}
			className = className.substring(index + 1);
			rowCount = Integer.parseInt(resource.get("count").asNode().getLiteralValue().toString());
			classCountMap.put(className, rowCount);
			classBaseUrlMap.put(className, resource.get("class").asNode().getURI());
		}
		queryString = RepoConstants.PREFIXES +
				"SELECT DISTINCT ?class ?pred\n" +
				"WHERE {\n" +
				"  \t?s ?pred ?o;\n" +
				"    ?pred [];\n" +
				"    rdf:type ?class;\n" +
				"    FILTER ( ?class != owl:NamedIndividual && (?pred != rdf:type))\n" +
				"}";
		resultSet = getResultFromQuery(dataset + "-data", queryString);
		while (resultSet != null && resultSet.hasNext()) {
			resource = resultSet.next();
			className = resource.get("class").asNode().getURI();
			if(className.contains("#")){
				index = className.lastIndexOf("#");
			}else{
				index = className.lastIndexOf("/");
			}
			className = className.substring(index + 1);
			columnName = resource.get("pred").asNode().getURI();
			distinctColumns.add(columnName);
			if (columnMap.get(className) != null) {
				columnMap.get(className).add(columnName);
			} else {
				columnMap.put(className, new ArrayList<>(Collections.singletonList(columnName)));
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
					"SELECT DISTINCT (?s as ?column) ?comment ?type ?label\n" +
					"WHERE { \n" +
					"  ?s\n" +
					"  rdfs:range ?type;\n" +
					"  rdfs:label ?label;\n" +
					"  OPTIONAL {\n" +
					"  \t?s rdfs:comment ?comment;\n" +
					"  }" +
					" 	FILTER (" + filterCondition + ")" +
					"}";
			model = null;
			resultSet = getResultFromQuery(dataset + "-ontology", queryString);
			String columnType;
			String comment;
			String label;
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
					label = resource.get("column").asNode().getURI();
					if (label.contains("#")) {
						index = label.lastIndexOf("#");
					} else {
						index = label.lastIndexOf("/");
					}
					label = label.substring(index + 1);
				} else {
					label = "";
				}
				columnName = resource.get("column").asNode().getURI().replaceAll("/ontology/", "/data/");
				columnCommentMap.put(columnName, comment);
				columnTypeMap.put(columnName, columnType);
				columnLabelMap.put(columnName, label);
			}
		}
//		JSONObject datasetInfo = new JSONObject();
		Map<String, Object> dsInfo = new HashMap<>();
		dsInfo.put("dsName", dataset);
		dsInfo.put("dsDesc", "");
//		datasetInfo.put("dsName", dataset);
//		datasetInfo.put("dsDesc", "");
//		JSONArray tables = new JSONArray();
//		JSONArray columns;
		List<Map<String, Object>> columns;
		List<Map<String, Object>> tables = new ArrayList<>();
//		JSONObject table;
		Map<String, Object> table = new HashMap<>();
		Map<String, Object> column = new HashMap<>();
//		JSONObject column;
		String classKey;
		for (String cls : classCountMap.keySet()) {
			classKey = cls.replaceAll(" ", "");
			if (columnMap.get(classKey) != null) {
//				table = new JSONObject();
//				columns = new JSONArray();
				table = new HashMap<>();
				columns = new ArrayList<>();
				index = 1;
				table.put("displayName", cls);
				table.put("fileName", cls);
				table.put("colCount", columnMap.get(classKey).size());
				table.put("rowCount", classCountMap.get(cls));
				table.put("baseUrl", classBaseUrlMap.get(cls));
				for (String col : columnMap.get(classKey)) {
					column = new HashMap<>();
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
							column.put("colDesc", "Label of a " + dataset + " resource");
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
//		datasetInfo.put("filesMd", tables);
		dsInfo.put("filesMd", tables);
		if (conn != null) {
			conn.close();
		}
		return dsInfo;
	}

	/**
	 * @param className - Name of the table.
	 * @return - list of rows in the table in JSON format.
	 */
	public List<Map<String, String>> getData(String className, String dataset) {
		dataset = dataset + "-data";
		List<Map<String, String>> rows = new ArrayList<>();
		Map<String, Map<String, String>> rowsMap = new HashMap<>();
		Map<String, String> incomingEdge;
		Map<String, String> rowObject;
		String id;
		String key;
		String value;
		String classUrl = "";
		int index;
		String qString = "SELECT ?subject ?predicate ?object \n " +
				"WHERE { " +
				"?subject ?predicate ?object " +
				"}";
		ResultSet resultSet = getResultFromQuery(dataset, qString);
		List<Triple> triples = new ArrayList<>();
		model = ModelFactory.createDefaultModel();
		while (resultSet.hasNext()) {
			QuerySolution s = resultSet.nextSolution();
			Triple t = Triple.create(s.get("subject").asNode(), s.get("predicate").asNode(), s.get("object").asNode());
			triples.add(t);
		}
		for (Triple t : triples) {
			model.add(model.asStatement(t));
		}
		String queryString = RepoConstants.PREFIXES +
				"SELECT ?class (count(?class) as ?count)\n" +
				"WHERE {\n" +
				"  \t?s rdf:type ?class;\n" +
				"    FILTER (?class != owl:NamedIndividual)\n" +
				"}\n" +
				"GROUP BY ?class";
		resultSet = getResultFromQuery(dataset , queryString);
		if (resultSet == null) {
			return null;
		}
		while (resultSet.hasNext()) {
			QuerySolution resource = resultSet.next();
			String clsName = resource.get("class").asNode().getURI();
			if(clsName.contains("#")){
				index = clsName.lastIndexOf("#");
			}else{
				index = clsName.lastIndexOf("/");
			}
			if (className.equals(clsName.substring(index + 1))) {
				classUrl = clsName;
				break;
			}
		}
		Set<String> duplicateColumnLst = new TreeSet<>();
		queryString = RepoConstants.PREFIXES +
				"SELECT *\n" +
				"WHERE {\n" +
				"	?s a <" + classUrl + ">; \n" +
				"	?p ?o;\n" +
				"   FILTER ( ?p != rdf:type)\n" +
				"}";
		resultSet = getResultFromQuery(dataset, queryString);
		while (resultSet.hasNext()) {
			QuerySolution resource = resultSet.next();
			id = resource.get("s").asNode().toString();
			key = resource.get("p").asNode().toString();
			if (key.contains("#")) {
				key = key.substring(key.lastIndexOf("#") + 1);
			} else {
				key = key.substring(key.lastIndexOf("/") + 1);
			}
			if (rowsMap.get(id) == null) {
				rowObject = new HashMap<>();
			} else {
				rowObject = rowsMap.get(id);
			}
			if (resource.get("o").isLiteral()) {
				value = resource.get("o").asLiteral().getString();
			} else if (resource.get("o").isURIResource()) {
				value = resource.get("o").asNode().getURI();
				value = getForeignReference(value, dataset);
			} else {
				continue;
			}
			if (rowObject.get(key) == null) {
				if (value.contains("#")) {
					value = value.substring(value.lastIndexOf("#") + 1);
				} else {
					value = value.substring(value.lastIndexOf("/") + 1);
				}
				rowObject.put(key.toLowerCase(), value);
			} else {
				rowObject.put(key.toLowerCase(), "");
				duplicateColumnLst.add(key);
			}
			rowsMap.put(id, rowObject);
		}
		for (String rowId : rowsMap.keySet()) {
			for (String col : duplicateColumnLst) {
				rowsMap.get(rowId).remove(col);
			}
			incomingEdge = getIncomingEdge(rowId, dataset);
			if (incomingEdge != null) {
				rowsMap.get(rowId).putAll(getIncomingEdge(rowId, dataset));
			}
			rows.add(rowsMap.get(rowId));
		}
		if (conn != null) {
			conn.close();
		}
		model = null;
		return rows;
	}

	public String getForeignReference(String sub, String dataset) {
		String value = "";
		String key;
		String val;
		Map<String, String> resourceMap = new HashMap<>();
		QuerySolution resource;
		String queryString = RepoConstants.PREFIXES +
				"SELECT *\n" +
				"WHERE {\n" +
				"	<" + sub + "> ?p ?o;\n" +
				" FILTER( ?o != owl:NamedIndividual)" +
				"}";
		ResultSet resultSet = getResultFromQuery(dataset, queryString);
		while (resultSet.hasNext()) {
			resource = resultSet.next();
			key = resource.get("p").asNode().toString();
			if (key.contains("#")) {
				key = key.substring(key.lastIndexOf("#") + 1);
			} else {
				key = key.substring(key.lastIndexOf("/") + 1);
			}
			if (resource.get("o").isLiteral()) {
				resourceMap.put(key, resource.get("o").asLiteral().getString());
			} else if (resource.get("o").isURIResource()) {
				val = resource.get("o").asNode().getURI();
				val = val.substring(value.lastIndexOf("/") + 1);
				resourceMap.put(key, val);
			} else {
				continue;
			}
		}
		if (resourceMap.get("type") != null && resourceMap.get("type") == "Instant") {
			value = resourceMap.get("inXSDDate");
		} else {
			value = resourceMap.get("label") == null ? sub.substring(sub.lastIndexOf("/") + 1) : resourceMap.get("label");
		}
		return value;
	}

	public Map<String, String> getIncomingEdge(String obj, String dataset) {
		Map<String, String> edge = null;
		String key;
		String val;
		String subject;
		int index;
		QuerySolution resource;
		String queryString = RepoConstants.PREFIXES +
				"SELECT *\n" +
				"WHERE {\n" +
				"?s ?p <" + obj + ">\n" +
				"}";
		ResultSet resultSet = getResultFromQuery(dataset, queryString);
		if (resultSet.hasNext()) {
			resource = resultSet.next();
			subject = resource.get("s").asNode().toString();
			if (subject.contains("#")) {
				index = subject.lastIndexOf("#");
			} else {
				index = subject.lastIndexOf("/");
			}
			val = subject.substring(index + 1);
			subject = subject.substring(0, index);
			if (subject.contains("#")) {
				index = subject.lastIndexOf("#");
			} else {
				index = subject.lastIndexOf("/");
			}
			key = subject.substring(index + 1);
			edge = new HashMap<>();
			edge.put(key.toLowerCase(), val);
		}
		if (resultSet.hasNext()) {
			return null;
		}
		return edge;
	}
}


