package com.ida.ida_task;

import java.io.IOException;

import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.RDFNode;

public class FusekiConnectionWithJava {
	
	public static void selectAndProcess(String serviceURI, String query) {
		QueryExecution q = QueryExecutionFactory.sparqlService(serviceURI,
				query);
		ResultSet results = q.execSelect();

		while (results.hasNext()) {
			QuerySolution soln = results.nextSolution();
			// assumes that you have an "?x" in your query
			RDFNode x = soln.get("subject");
			RDFNode y = soln.get("predicate");
			RDFNode z = soln.get("object");
			System.out.println("subject"+x);
			System.out.println("predicate"+y);
			System.out.println("object"+z);
		}
	}
	
	public static void main(String[] argv) throws IOException {

		selectAndProcess(
				//test is the name of database
				"http://localhost:3030/test",
				"SELECT ?subject ?predicate ?object\r\n" + 
				"WHERE {\r\n" + 
				"  ?subject ?predicate ?object\r\n" + 
				"}\r\n" + 
				"LIMIT 25");
	}
}
