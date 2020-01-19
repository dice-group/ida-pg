package upb.ida.dao;

import org.apache.jena.graph.Triple;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.ResultSetFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.DatasetAccessorFactory;
import org.apache.jena.query.DatasetAccessor;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdfconnection.RDFConnectionFuseki;
import org.apache.jena.rdfconnection.RDFConnectionRemoteBuilder;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import upb.ida.constant.IDALiteral;

import java.io.ByteArrayOutputStream;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Class to make fuseki database calls.
 *
 * @author Nandeesh
 */
public class DataRepository {
	private static String dbhost = System.getenv("FUSEKI_URL");
	private Model model = null;
	private RDFConnectionFuseki conn = null;
	private boolean isTest;

	public DataRepository(boolean isTest) {
		this.isTest = isTest;
	}

	/**
	 * @param queryString the SPARQL query to be executed on the RDF dataset
	 * @param dataset     the active dataset on which the query has to be executed
	 * @return It takes query string as its parameter and returns the result set after executing the query.
	 */
	public ResultSet getResultFromQuery(String dataset, String queryString) {
		QueryExecution queryExecution = null;
		ResultSet resultSet;
		Query query = QueryFactory.create(queryString);

		try {
			if (model == null && (isTest || IDALiteral.TEST_DATASET.equals(dataset) || IDALiteral.TEST_ONTOLOGYSET.equals(dataset))) {
				model = ModelFactory.createDefaultModel();
				String path = Objects.requireNonNull(getClass().getClassLoader().getResource("dataset/test.ttl")).getFile();
				model.read(path);
				queryExecution = QueryExecutionFactory.create(query, model);
			} else if (model == null) {
				RDFConnectionRemoteBuilder builder = RDFConnectionFuseki.create().destination(dbhost + dataset);
				conn = (RDFConnectionFuseki) builder.build();
				queryExecution = conn.query(query);
			} else {
				queryExecution = QueryExecutionFactory.create(query, model);
			}
			resultSet = ResultSetFactory.copyResults(queryExecution.execSelect());
			return resultSet;
		} catch (Exception ex) {
			ex.printStackTrace();
			return null; // User will not get any message on UI if not returned null.
		} finally {
			if(queryExecution != null){
				queryExecution.close();
			}
			if(conn != null){
				conn.close();
			}
		}
	}


	/**
	 * @param dataset - Name of the dataset
	 * @return - Metadata of the dataset as a map.
	 */
	public Map<String, Object> getDataSetMD(String dataset) {
		Map<String, Integer> classCountMap = new HashMap<>();
		Set<String> distinctColumns = new TreeSet<>();
		Map<String, ArrayList<String>> columnMap;
		Map<String, String> columnCommentMap = new HashMap<>();
		Map<String, String> columnTypeMap = new HashMap<>();
		Map<String, String> columnLabelMap = new HashMap<>();
		Map<String, String> classBaseUrlMap = new HashMap<>();
		String className;
		int rowCount;
		QuerySolution resource;
		int index;
		try {
			StringBuilder queryString = new StringBuilder().append(IDALiteral.PREFIXES).append(
					"SELECT ?class (count(?class) as ?count) WHERE { ?s rdf:type ?class; FILTER (?class != owl:NamedIndividual) } GROUP BY ?class");
			ResultSet resultSet = getResultFromQuery(dataset, queryString.toString());
			if (resultSet == null) {
				return null;
			}
			while (resultSet.hasNext()) {
				resource = resultSet.next();
				className = resource.get("class").asNode().getURI();
				if (className.contains("#")) {
					index = className.lastIndexOf("#");
				} else {
					index = className.lastIndexOf("/");
				}
				className = className.substring(index + 1);
				rowCount = Integer.parseInt(resource.get("count").asNode().getLiteralValue().toString());
				classCountMap.put(className, rowCount);
				classBaseUrlMap.put(className, resource.get("class").asNode().getURI());
			}
			columnMap = getClassColumnMap(dataset, distinctColumns);
			Map<String, ArrayList<String>> columnInfoMap = getColumnsInformation(dataset, distinctColumns);
			ArrayList<String> columnInfo;
			for (String colName : columnInfoMap.keySet()) {
				columnInfo = columnInfoMap.get(colName);
				columnCommentMap.put(colName, columnInfo.get(0));
				columnTypeMap.put(colName, columnInfo.get(1));
				columnLabelMap.put(colName, columnInfo.get(2));
			}
			Map<String, Object> dsInfo = new HashMap<>();
			dsInfo.put("dsName", dataset);
			dsInfo.put("dsDesc", "");
			List<Map<String, Object>> tables = new ArrayList<>();
			Map<String, Object> table;
			String classKey;
			for (String cls : classCountMap.keySet()) {
				classKey = cls.replaceAll(" ", "");
				if (columnMap.get(classKey) != null) {
					table = new HashMap<>();
					table.put("displayName", cls);
					table.put("fileName", cls);
					table.put("colCount", columnMap.get(classKey).size());
					table.put("rowCount", classCountMap.get(cls));
					table.put("baseUrl", classBaseUrlMap.get(cls));
					table.put("fileColMd", createColumnsMetadata(columnMap, classKey, dataset, columnCommentMap, columnLabelMap, columnTypeMap));
					tables.add(table);
				}
			}
			dsInfo.put("filesMd", tables);
			return dsInfo;
		} catch (Exception ex) {
			ex.printStackTrace();
			return null; // User will not get any message on UI if not returned null.
		} finally {
			if (conn != null) {
				conn.close();
			}
		}
	}

	/**
	 * @param className - Name of the table.
	 * @return - list of rows in the table in JSON format.
	 */
	public List<Map<String, String>> getData(String className, String dataset) {
		Map<String, Map<String, String>> rowsMap = new HashMap<>();
		Map<String, String> rowObject;
		String id;
		String key;
		String value;
		String classUrl;
		setupDataSetModel(dataset);
		classUrl = getClassUrl(className, dataset);
		if (classUrl == null) {
			return null;
		}
		Set<String> duplicateColumnLst = new TreeSet<>();
		/*
		 * Get all triples of the class and all other related triples.
		 */
		StringBuilder queryString = new StringBuilder().append(IDALiteral.PREFIXES).append(
				"SELECT * WHERE { ?s a <").append(classUrl).append(">; ?p ?o; FILTER ( ?p != rdf:type) }");
		ResultSet resultSet = getResultFromQuery(dataset, queryString.toString());
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
			value = getObjectValueOfResource(resource, dataset);
			if (value == null) {
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
		List<Map<String, String>> rows = createRowsForClass(rowsMap, duplicateColumnLst, dataset);
		if (conn != null) {
			conn.close();
		}
		model = null;
		return rows;
	}

	/**
	 * @param sub     - subject of the resource of which the date/label has to be fetched.
	 * @param dataset - active dataset from which the triple has to be fetched
	 * @return returns a) Date if the type of resource is date.
	 * b) Label if not a date and has a label.
	 * c) Id of the resource otherwise.
	 */
	public String getForeignReference(String sub, String dataset) {
		String value = "";
		String key;
		String val;
		Map<String, String> resourceMap = new HashMap<>();
		QuerySolution resource;
		StringBuilder queryString = new StringBuilder(IDALiteral.PREFIXES).append(
				"SELECT * WHERE { <").append(sub).append("> ?p ?o; FILTER( ?o != owl:NamedIndividual) }");
		ResultSet resultSet = getResultFromQuery(dataset, queryString.toString());
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
			}
		}
		if (resourceMap.get("type") != null && "Instant".equals(resourceMap.get("type"))) {
			value = resourceMap.get("inXSDDate");
		} else {
			value = resourceMap.get("label") == null ? sub.substring(sub.lastIndexOf("/") + 1) : resourceMap.get("label");
		}
		return value;
	}

	/**
	 * @param obj     - string which has to be the object of a triple(Incoming edge).
	 * @param dataset - active dataset from which the triple has to be fetched
	 * @return - returns a < predicate, object > pair with object value equal to value of obj if only one such pair exists and null otherwise.
	 */
	public Map<String, String> getIncomingEdge(String obj, String dataset) {
		Map<String, String> edge = null;
		String key;
		String val;
		String subject;
		int index;
		QuerySolution resource;
		StringBuilder queryString = new StringBuilder(IDALiteral.PREFIXES).append("SELECT * WHERE { ?s ?p <").append(obj).append("> }");
		ResultSet resultSet = getResultFromQuery(dataset, queryString.toString());
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

	public Set<String> getColumnsList(String className, String dataset) {
		String columnName;
		QuerySolution resource;
		Set<String> distinctColumns = new TreeSet<>();
		String classUrl = getClassUrl(className, dataset);
		StringBuilder queryString = new StringBuilder().append(IDALiteral.PREFIXES).append("SELECT DISTINCT ?class ?pred WHERE { ?s ?pred ?o; ?pred []; rdf:type ?class; FILTER( ?class != owl:NamedIndividual && (?pred != rdf:type) && ?class = <").append(classUrl).append("> ) }");
		ResultSet resultSet = getResultFromQuery(dataset, queryString.toString());
		while (resultSet != null && resultSet.hasNext()) {
			resource = resultSet.next();
			columnName = resource.get("pred").asNode().getURI();
			if (columnName.contains("#")) {
				columnName = columnName.substring(columnName.lastIndexOf("#") + 1);
			} else {
				columnName = columnName.substring(columnName.lastIndexOf("/") + 1);
			}
			distinctColumns.add(columnName);
		}
		return distinctColumns;
	}

	public String getClassUrl(String className, String dataset) {
		int index;
		StringBuilder queryString = new StringBuilder().append(IDALiteral.PREFIXES).append("SELECT ?class WHERE { ?s rdf:type ?class; FILTER (?class != owl:NamedIndividual) } GROUP BY ?class");
		ResultSet resultSet = getResultFromQuery(dataset, queryString.toString());
		if (resultSet == null) {
			return null;
		}
		while (resultSet.hasNext()) {
			QuerySolution resource = resultSet.next();
			String clsName = resource.get("class").asNode().getURI();
			if (clsName.contains("#")) {
				index = clsName.lastIndexOf("#");
			} else {
				index = clsName.lastIndexOf("/");
			}
			if (className.equals(clsName.substring(index + 1))) {
				return clsName;
			}
		}
		return null;
	}

	/**
	 * @param dataset - Name of the dataset for which the apache jena model has to created and assigned to class variable.
	 */
	private void setupDataSetModel(String dataset) {
		StringBuilder qString = new StringBuilder("SELECT ?subject ?predicate ?object  WHERE {  ?subject ?predicate ?object  }");
		ResultSet resultSet = getResultFromQuery(dataset, qString.toString());
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
	}

	/**
	 * @param resource - Resource of which, value of the object has to be fetched.
	 * @param dataset  - Name of the dataset
	 * @return - Returns a string containing the object value of a resource.
	 */
	private String getObjectValueOfResource(QuerySolution resource, String dataset) {
		String value = null;
		if (resource.get("o").isLiteral()) {
			value = resource.get("o").asLiteral().getString();
		} else if (resource.get("o").isURIResource()) {
			value = resource.get("o").asNode().getURI();
			value = getForeignReference(value, dataset);
		}
		return value;
	}

	private List<Map<String, String>> createRowsForClass(Map<String, Map<String, String>> rowsMap, Set<String> duplicateColumnLst, String dataset) {
		List<Map<String, String>> rows = new ArrayList<>();
		Map<String, String> incomingEdge;
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
		return rows;
	}

	/**
	 * @param dataset         Name of the dataset
	 * @param distinctColumns - Empty set to have the list of distinct columns in the dataset.
	 * @return - Map of class name and the list of columns of the class.
	 */
	private Map<String, ArrayList<String>> getClassColumnMap(String dataset, Set<String> distinctColumns) {
		Map<String, ArrayList<String>> columnMap = new HashMap<>();
		StringBuilder queryString = new StringBuilder(IDALiteral.PREFIXES).append("SELECT DISTINCT ?class ?pred WHERE { ?s ?pred ?o; ?pred []; rdf:type ?class; FILTER ( ?class != owl:NamedIndividual && (?pred != rdf:type)) }");
		ResultSet resultSet = getResultFromQuery(dataset, queryString.toString());
		QuerySolution resource;
		String className;
		int index;
		String columnName;

		while (resultSet != null && resultSet.hasNext()) {
			resource = resultSet.next();
			className = resource.get("class").asNode().getURI();
			if (className.contains("#")) {
				index = className.lastIndexOf("#");
			} else {
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
		return columnMap;
	}

	/**
	 * @param dataset         - Name of the dataset. test
	 * @param distinctColumns - Set of column names.
	 * @return - Map of column name as a key and its info (comment, data type & label) as value.
	 */
	private Map<String, ArrayList<String>> getColumnsInformation(String dataset, Set<String> distinctColumns) {
		Map<String, ArrayList<String>> columnsInfoMap = new HashMap<>();
		ArrayList<String> columnInfo;
		boolean isFirst = true;
		StringBuilder filterPrefix = new StringBuilder("?s = ");
		StringBuilder filterCondition = new StringBuilder();
		QuerySolution resource;
		int index;
		String columnName;
		for (String col : distinctColumns) {
			if (!isFirst) {
				filterCondition.append(" || ");
			} else {
				isFirst = false;
			}
			filterCondition.append(filterPrefix).append("<").append(col.replace("/data/", "/ontology/")).append(">");
		}
		if (!"".contentEquals(filterCondition)) {
			StringBuilder queryString = new StringBuilder()
					.append(IDALiteral.PREFIXES)
					.append("SELECT DISTINCT (?s as ?column) ?comment ?type ?label WHERE {  ?s rdfs:range ?type; rdfs:label ?label; OPTIONAL { ?s rdfs:comment ?comment; } FILTER (")
					.append(filterCondition).append(") }");
			model = null;
			ResultSet resultSet = getResultFromQuery(dataset + "-ontology", queryString.toString());
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
				columnInfo = new ArrayList<>();
				columnInfo.add(comment);
				columnInfo.add(columnType);
				columnInfo.add(label);
				columnsInfoMap.put(columnName, columnInfo);
			}
		}
		return columnsInfoMap;
	}

	private List<Map<String, Object>> createColumnsMetadata(Map<String, ArrayList<String>> columnMap, String classKey, String dataset, Map<String, String> columnCommentMap, Map<String, String> columnLabelMap, Map<String, String> columnTypeMap) {
		List<Map<String, Object>> columns = new ArrayList<>();
		Map<String, Object> column;
		int index = 1;
		String columnName;
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
		return columns;
	}

	public JSONObject getOntologyData(String datasetName) {
		if(!IDALiteral.SS_DATASET.equals(datasetName)){
			return null;
		}
		try{
			DatasetAccessor accessor = DatasetAccessorFactory.createHTTP(dbhost + datasetName + "-ontology");
			model = accessor.getModel();
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			RDFDataMgr.write(outputStream, model, Lang.JSONLD);
			JSONParser parser = new JSONParser();
			return (JSONObject) parser.parse(new String(outputStream.toByteArray()));
		}catch (Exception ex){
			ex.printStackTrace();
		}
		return null;
	}
}
