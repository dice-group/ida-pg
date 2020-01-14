package upb.ida.ontologyexplorer;

import org.apache.jena.graph.Triple;
import org.apache.jena.query.*;
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
			QuerySolution s;
			Triple t;
			while (resultSet.hasNext()) {
				s = resultSet.nextSolution();
				t = Triple.create(s.get("subject").asNode(), s.get("predicate").asNode(), s.get("object").asNode());
				model.add(model.asStatement(t));
			}
			RDFDataMgr.loadModel(returnValue, Lang.JSONLD);
		}
		catch (Exception e)
		{
			System.out.println(e);
		}

		return returnValue;
	}
}
