package com.ida.ida_task;


	import java.io.InputStream;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.util.FileManager;
import org.apache.log4j.BasicConfigurator;

	public class FetchAllRecord {

	    static final String inputFileName  = "data3.ttl";
	    public static void main (String args[]) {
	        BasicConfigurator.configure();
	        // create an empty model
	        Model model = ModelFactory.createDefaultModel();
	        //Model model2 = ModelFactory.createDefaultModel();


	        InputStream in = FileManager.get().open(inputFileName);
	        if (in == null) {
	            throw new IllegalArgumentException("File: " + inputFileName + " not found");
	        }

	        // read the RDF/XML file
	        model.read(inputFileName);       
	        // write it to standard outString queryString = " .... " ;
	        String queryString =
	        		"SELECT ?relation ?name ?password\r\n" + 
	        		"WHERE {\r\n" + 
	        		"  ?relation ?name ?password\r\n" + 
	        		"}\r\n" + 
	        		"LIMIT 25";
	        Query query = QueryFactory.create(queryString) ;
	        //String responseIs = null;

	        try (QueryExecution qexec = QueryExecutionFactory.create(query, model))
	        {
	        	
	        	ResultSet results = qexec.execSelect() ;
	            System.out.println(results.toString());
	            while(results.hasNext())
	            {
	            	QuerySolution soln = results.nextSolution();
	            	//Literal name =soln.getLiteral(x)
	            	System.out.println("lets see the result"+soln);
	            }
	        	
	        	
	        	
	        /*	
	        	//this is with construct
	            Model results = qexec.execConstruct() ;
	            StmtIterator iter = results.listStatements();
	            System.out.println(results.toString());
	            while(iter.hasNext())
	            {
	                System.out.println(iter.next());

	            }*/
	        }
//	        System.out.println("   Response after rdf read");
//	     model.write(System.out,"TURTLE");
//	     System.out.println(".............................");
//	     model2.write(System.out,"TURTLE");
	       // System.out.println(responseIs); 

	    }
	}

