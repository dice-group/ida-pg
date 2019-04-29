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
import org.apache.jena.util.FileManager;
import org.apache.log4j.BasicConfigurator;

	public class FetchRecordOneByOne{

	    /**
	     NOTE that the file is loaded from the class-path and so requires that
	     the data-directory, as well as the directory containing the compiled
	     class, must be added to the class-path when running this and
	     subsequent examples.
	     */
	    static final String inputFileName  = "data3.ttl";
//	    static final String inputFileName  = "extractor2222response.ttl";

	    public static void main (String args[]) {
	        BasicConfigurator.configure();
	        // create an empty model
	        Model model = ModelFactory.createDefaultModel();
	        InputStream in = FileManager.get().open(inputFileName);
	        if (in == null) {
	            throw new IllegalArgumentException("File: " + inputFileName + " not found");
	        }
	        // read the RDF/XML file
	        model.read(inputFileName);

	        String username =  "deepak";
			// write it to standard outString queryString = " .... " ;
	        String queryString =
	        		"select ?n ?i ?p \r\n" + 
	        		"	where {\r\n" + 
	        		"      <http://userdata/"+username+"> \r\n" + 
	        		"\r\n" + 
	        		"      <http://www.w3.org/2001/vcard-rdf/3.0#NAME>\r\n" + 
	        		"                 ?n ;\r\n" + 
	        		"      <http://www.w3.org/2001/vcard-rdf/3.0#Pcode>\r\n" + 
	        		"                  ?p ;\r\n" + 
	        		"}";


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
	        	

	        }
	    }
	}
