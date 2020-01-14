package upb.ida.dao;

import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.ResultSetFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSetFormatter;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdfconnection.RDFConnectionFuseki;
import org.apache.jena.rdfconnection.RDFConnectionRemoteBuilder;
import org.apache.jena.update.UpdateAction;
import org.apache.jena.update.UpdateFactory;
import org.apache.jena.update.UpdateRequest;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Component;
import upb.ida.constant.IDALiteral;
import upb.ida.domains.User;

import java.io.ByteArrayOutputStream;
import java.util.Objects;

@Component
public class UserRepository {
	private static String dbhost = System.getenv("FUSEKI_URL");

	private static String hashPassword(String passWord) {
		return BCrypt.hashpw(passWord, BCrypt.gensalt(12));
	}

	public ResultSet getResultFromQuery(String dataset, String queryString) {
		QueryExecution queryExecution = null;
		ResultSet resultSet;
		Model model;
		RDFConnectionFuseki conn = null;

		try {
			if (IDALiteral.TEST_USER_DATASET.equals(dataset)) {
				model = ModelFactory.createDefaultModel();
				String path = Objects.requireNonNull(getClass().getClassLoader().getResource("dataset/testuser.ttl")).getFile();
				model.read(path);
				queryExecution = QueryExecutionFactory.create(queryString, model);
			} else {
				RDFConnectionRemoteBuilder builder = RDFConnectionFuseki.create().destination(dbhost + dataset);
				conn = (RDFConnectionFuseki) builder.build();
				queryExecution = conn.query(queryString);
			}
			resultSet = ResultSetFactory.copyResults(queryExecution.execSelect());
			return resultSet;
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		} finally {
			if (queryExecution != null) {
				queryExecution.close();
			}
			if (conn != null) {
				conn.close();
			}
		}
	}

	private boolean updateQuery(String dataset, String queryString) {
		Model model;
		RDFConnectionFuseki conn = null;
		try {
			UpdateRequest request = UpdateFactory.create();
			request.add(queryString);
			if (IDALiteral.TEST_USER_DATASET.equals(dataset)) {
				model = ModelFactory.createDefaultModel();
				String path = Objects.requireNonNull(getClass().getClassLoader().getResource("dataset/testuser.ttl")).getFile();
				model.read(path);
				UpdateAction.execute(request, model);
			} else {
				RDFConnectionRemoteBuilder builder = RDFConnectionFuseki.create().destination(dbhost + dataset);
				conn = (RDFConnectionFuseki) builder.build();
				conn.update(request);
			}
			return true;
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		} finally {
			if (conn != null) {
				conn.close();
			}
		}
	}

	public String listAllUsers(String datasetName) {
		String queryString = "prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> prefix owl: <http://www.w3.org/2002/07/owl#> SELECT ?subject ?predicate ?object WHERE {?subject ?predicate ?object }";
		ResultSet resultSet = getResultFromQuery(datasetName, queryString);
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		ResultSetFormatter.outputAsJSON(outputStream, resultSet);
		return new String(outputStream.toByteArray());
	}

	public String updateUser(String datasetName, User user, String updateAction, boolean hasPassword) {
		StringBuilder queryString = new StringBuilder();
		queryString.append("PREFIX ab: <http://userdata/#").append(user.getUsername()).append(">");
		queryString.append("PREFIX dc: <http://www.w3.org/2001/vcard-rdf/3.0#> ");
		queryString.append(updateAction);
		queryString.append(" { ab:");
		queryString.append(" dc:username \"");
		queryString.append(user.getUsername());
		queryString.append("\" ; dc:firstname \"");
		queryString.append(user.getFirstname());
		queryString.append("\" ; dc:lastname \"");
		queryString.append(user.getLastname());
		queryString.append("\" ; dc:userrole \"");
		queryString.append(user.getUserRole());
		queryString.append("\" ; dc:password \"");
		if(hasPassword){
			queryString.append(hashPassword(user.getPassword()));
		}else{
			queryString.append(user.getPassword());
		}
		queryString.append("\" .  } ");
		if (updateQuery(datasetName, queryString.toString())) {
			return IDALiteral.RESP_PASS_ROUTINE;
		} else {
			return IDALiteral.RESP_FAIL_ROUTINE;
		}
	}

	public User getUserByUsername(String datasetName, String userName) {
		StringBuilder queryString = new StringBuilder();
		queryString.append("prefix dc: <http://www.w3.org/2001/vcard-rdf/3.0#> select ?firstname ?lastname ?username ?password  ?userrole where { <http://userdata/#");
		queryString.append(userName);
		queryString.append("> dc:firstname ?firstname ;dc:lastname ?lastname; dc:password ?password ; dc:userrole ?userrole; dc:username ?username .}");
		ResultSet resultSet = getResultFromQuery(datasetName, queryString.toString());
		if (resultSet != null && resultSet.hasNext()) {
			QuerySolution soln = resultSet.next();
			String fetchedUserName = soln.get("username").asLiteral().getString();
			String fetchedFirstName = soln.get("firstname").asLiteral().getString();
			String fetchedPassword = soln.get("password").asLiteral().getString();
			String fetchedLastName = soln.get("lastname").asLiteral().getString();
			String fetchedUserRole = soln.get("userrole").asLiteral().getString();
			return new User(fetchedUserName, fetchedPassword, fetchedFirstName, fetchedLastName, fetchedUserRole);
		}else {
			return null;
		}
	}

}
