package upb.ida.ontologyExplorer;

import org.apache.jena.query.ResultSet;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.ResultSetFactory;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdfconnection.RDFConnectionFuseki;
import org.apache.jena.rdfconnection.RDFConnectionRemoteBuilder;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.springframework.stereotype.Component;
import java.util.*;


@Component
public class OntologyExplorer {
	private final String PREFIXES = "PREFIX datagraph: <http://127.0.0.1:3030/ssfuehrer/data/data>\n" +
			"PREFIX data: <https://www.upb.de/historisches-institut/neueste-geschichte/ssdal/data/>\n" +
			"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
			"PREFIX dbo: <http://dbpedia.org/ontology/>\n" +
			"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
			"PREFIX soldierData: <https://www.upb.de/historisches-institut/neueste-geschichte/ssdal/data/soldier/>";
	private static String dbhost = System.getenv("FUSEKI_URL");
	private Model model = null;
	private RDFConnectionFuseki conn = null;

	/**
	 * @param queryString the SPARQL query to be executed on the RDF dataset
	 * @return It takes query string as its parameter and returns the result set after executing the query.
	 */
	public ResultSet getResultFromQuery(String queryString, String dataset) {
		QueryExecution queryExecution;
		ResultSet resultSet = null;
		Query query = QueryFactory.create(queryString);
		boolean connectionCreated = false;

		try
		{
			/*
			 * No need to create a model from file or make database connection if the query is being run on already existing model. ( multiple queries are run on same model from getData function.)
			 */
			if (model == null) {
				/*
				 *	Create a fuseki model from the file and run the query on that model for test cases.
				 */
				if ("test-data".equals(dataset)) {

						model = ModelFactory.createDefaultModel();
						String path = Objects.requireNonNull(getClass().getClassLoader().getResource("dataset/test-soldier.ttl")).getFile();
						model.read(path);
						queryExecution = QueryExecutionFactory.create(query, model);

				} else {
						RDFConnectionRemoteBuilder builder = RDFConnectionFuseki.create().destination(dbhost + dataset);
						conn = (RDFConnectionFuseki) builder.build();
						connectionCreated = true;
						queryExecution = conn.query(query);
				}
			} else {
				queryExecution = QueryExecutionFactory.create(query, model);
			}
			if (queryExecution != null) {
				resultSet = ResultSetFactory.copyResults(queryExecution.execSelect());
				queryExecution.close();
				return resultSet;
			}

		} catch (NullPointerException ex) {
			System.out.println(ex.getMessage());
			return null;
		}finally {
			if(connectionCreated)
			{
				conn.close();
			}
		}

		return resultSet;
	}

	/**
	 * @param filename the file name to be used while executing on the RDF dataset
	 * @return It takes query string as its parameter and returns the result set after executing the query.
	 */
	public String fetchData(String filename) {
		model = null;
		String qString = "SELECT ?subject ?predicate ?object \n WHERE { ?subject ?predicate ?object }";
		String returnValue = "";

		try{
			ResultSet resultSet = getResultFromQuery(qString, filename);
			model = ModelFactory.createDefaultModel();

			RDFDataMgr.loadModel(returnValue, Lang.JSONLD);
		}
		catch (Exception e)
		{
			System.out.println(e);
		}

		return returnValue;
	}
}
