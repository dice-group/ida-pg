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
 *
 */
public class DataRepository {
	private static String dbhost = System.getenv("FUSEKI_URL");
	private Model model = null;
	private RDFConnectionFuseki conn = null;
	private String dbUrl = "";
	private boolean isTest;

	public DataRepository(String dataset, boolean isTest) {
		if (dbhost == null) {
			dbhost = "http://127.0.0.1:3030/";
		}
		dbUrl = dbhost + dataset;
		this.isTest = isTest;
		if (isTest) {
			try {
				model = ModelFactory.createDefaultModel();
				String path = Objects.requireNonNull(getClass().getClassLoader().getResource("test.ttl")).getFile();
				model.read(path);
			} catch (NullPointerException ex) {
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
				RDFConnectionRemoteBuilder builder = RDFConnectionFuseki.create().destination(dbUrl);
				conn = (RDFConnectionFuseki) builder.build();
				Query query = QueryFactory.create(queryString);
				queryExecution = conn.query(query);
			} catch (Exception ex) {
				System.out.println(ex.getMessage());
			}
		} else {
			Query query = QueryFactory.create(queryString);
			queryExecution = QueryExecutionFactory.create(query, model);
		}
		if(queryExecution != null) {
			return queryExecution.execSelect();
		}
		return null;
	}


	/**
	 * @return - Metadata of SSFuehrer dataset.
	 */
	public JSONObject getSSDataSetMD() {
		Map<String, Integer> classCountMap = new HashMap<>();
		Set<String> distinctColumns = new TreeSet<>();
		Map<String, ArrayList<String>> columnMap = new HashMap<>();
		Map<String, String> columnCommentMap = new HashMap<>();
		Map<String, String> columnTypeMap = new HashMap<>();
		Map<String, String> columnLabelMap = new HashMap<>();
		String className;
		String columnName;
		int rowCount;
		QuerySolution resource;
		int index;
		String filterPrefix = "?s = ";
		StringBuilder filterCondition = new StringBuilder();
		String baseUrl = "http://127.0.0.1:3030/ssfuehrer";
		String queryString = RepoConstants.PREFIXES +
				"prefix datagraph: <" + baseUrl + "/data/data>\n" +
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
				"prefix datagraph: <" + baseUrl + "/data/data>\n" +
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
					"prefix ontologygraph: <" + baseUrl + "/data/ontology>\n" +
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
		String classKey;
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

	/**
	 *
	 * @param className - Name of the table.
	 * @return - list of rows in the table in JSON format.
	 */
	public List<Map<String, String>> getData(String className) {
		List<Map<String, String>> rows = new ArrayList<>();
		Map<String, Map<String, String>> rowsMap = new HashMap<>();
		Map<String, String> incomingEdge;
		Map<String, String> rowObject;
		String id;
		String key;
		String value;
		String qString = "PREFIX datagraph: <http://127.0.0.1:3030/ssfuehrer/data/data>\nSELECT ?subject ?predicate ?object \n FROM datagraph: \n WHERE { ?subject ?predicate ?object }";
		ResultSet resultSet = getResultFromQuery(qString);
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
		Set<String> duplicateColumnLst = new TreeSet<>();
		String dataGraph = "http://127.0.0.1:3030/ssfuehrer";
		String queryString = RepoConstants.PREFIXES +
				"prefix datagraph: <" + dataGraph + "/data/data>\n" +
				"SELECT *\n" +
				"WHERE {\n" +
				"	?s a data:" + className + "; \n" +
				"	?p ?o;\n" +
				"   FILTER ( ?p != rdf:type)\n" +
				"}";
		resultSet = getResultFromQuery(queryString);
		while (resultSet.hasNext()) {
			QuerySolution resource = resultSet.next();
			id = resource.get("s").asNode().getURI();
			key = resource.get("p").asNode().getURI();
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
			} else {
				value = resource.get("o").asNode().getURI();
				value = getForeignReference(value);
			}
			if (rowObject.get(key) == null) {
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
			incomingEdge = getIncomingEdge(rowId);
			if (incomingEdge != null) {
				rowsMap.get(rowId).putAll(getIncomingEdge(rowId));
			}
			rows.add(rowsMap.get(rowId));
		}
		model = null;
		return rows;
	}

	public String getForeignReference(String sub) {
		String value = "";
		String key;
		String val;
		Map<String, String> resourceMap = new HashMap<>();
		QuerySolution resource;
		String baseUrl = "http://127.0.0.1:3030/ssfuehrer";
		String queryString = RepoConstants.PREFIXES +
				"prefix datagraph: <" + baseUrl + "/data/data>\n" +
				"SELECT *\n" +
				"WHERE {\n" +
				"	<" + sub + "> ?p ?o;\n" +
				" FILTER( ?o != owl:NamedIndividual)" +
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
				resourceMap.put(key, resource.get("o").asLiteral().getString());
			} else {
				val = resource.get("o").asNode().getURI();
				val = val.substring(value.lastIndexOf("/") + 1);
				resourceMap.put(key, val);
			}
		}
		if (resourceMap.get("type") != null) {
			if (resourceMap.get("type").contains("ssdal/data")) {
				value = resourceMap.get("label") == null ? sub.substring(sub.lastIndexOf("/") + 1) : resourceMap.get("label");
			} else {
				value = resourceMap.get("inXSDDate");
			}
		}
		return value;
	}

	public Map<String, String> getIncomingEdge(String obj) {
		Map<String, String> edge = null;
		String key;
		String val;
		String subject;
		QuerySolution resource;
		String baseUrl = "http://127.0.0.1:3030/ssfuehrer";
		String queryString = RepoConstants.PREFIXES +
				"prefix datagraph: <" + baseUrl + "/data/data>\n" +
				"SELECT *\n" +
				"WHERE {\n" +
				"?s ?p <" + obj + ">\n" +
				"}";
		ResultSet resultSet = getResultFromQuery(queryString);
		if (resultSet.hasNext()) {
			resource = resultSet.next();
			subject = resource.get("s").asNode().getURI();
			val = subject.substring(subject.lastIndexOf("/") + 1);
			subject = subject.substring(0, subject.lastIndexOf("/"));
			key = subject.substring(subject.lastIndexOf("/") + 1);
			edge = new HashMap<>();
			edge.put(key.toLowerCase(), val);
		}
		if (resultSet.hasNext()) {
			return null;
		}
		return edge;
	}
}


