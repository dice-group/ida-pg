package com.ida.ida_task;


import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.util.FileManager;
import org.apache.jena.vocabulary.VCARD;
import org.apache.log4j.BasicConfigurator;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class ApplicationController {
	
	@RequestMapping("/create")
	public String getQuery3(@RequestBody recordclazz reco ) throws IOException {		
		System.out.println(reco.getUserName());
		System.out.println(reco.getPassword());
		//System.out.println(reco.getQry());
	    
		// some definitions
	//	String userId = reco.getId();
		String userName = reco.getUserName();
		String password = reco.getPassword();
		String personURI = "http://userdata/#" + userName.replaceAll("\\s+", "");
        
        // create an empty model
        Model model = ModelFactory.createDefaultModel();
        //   create the resource 
        //   and add the properties cascading style
        Resource rs 
          = model.createResource(personURI)
        	//	 .addProperty(VCARD.UID, userId)
                 .addProperty(VCARD.NAME, userName)
                 .addProperty(VCARD.Pcode, password);
        //this is working fine
        System.out.println("result set"+rs);
        // now write the model in XML form to a file
        FileOutputStream fout=new FileOutputStream("data3.ttl",true);
      //  Model model1 = ModelFactory.createDefaultModel();        
        		  //model.write(fout,"RDF/XML");
        		  
        	  model.add(model);
        	//  ((Object) model1).build()
        	  model.write(fout,"Turtle");
        	  
            //model.write(System.out, "RDF/XML");
        	  fout.close();
        	  
		return "The userId, userName and Password are:" +userName + password;
	}
	
	// get endpoint and query and return result
	@RequestMapping("/query_2") //query is keyword 
	public String getQuery(@RequestBody recordclazz rec ) {
		
		System.out.println(rec.getQry());
	//	System.out.println(rec.getEndpt());

		// Create Query
		QueryFactory.create(rec.getQry());    

		StringWriter modelAsString = new StringWriter();	

		QueryExecution qexec = QueryExecutionFactory.sparqlService("data3.ttl", rec.getQry());

		Model resultss = qexec.execConstruct();
		resultss.write(modelAsString, "turtle");
		String sparql_result = modelAsString.toString();

		System.out.println("-"+sparql_result);

//		File file = new File("data3.ttl");
//		BufferedWriter writer;
//		try {
//			writer = new BufferedWriter(new FileWriter(file));
//			writer.write(sparql_result);
//			writer.close();
//
//		} catch (IOException e) {		
//			e.printStackTrace();
//		}

		return sparql_result;		 

	}
	
//	@RequestMapping("/fetch-all") //Fetch-all
//	public String getfetch() {
//		Model model = RDFDataMgr.loadModel("data3.ttl") ;
//		RDFDataMgr.read(model, "http://userdata/#" ) ;
//		return model.toString();
//		
//	}
	
	static final String inputFileName = "data3.ttl";
    //static final String johnSmithURI = "http://somewhere/gargdeepak";
    
	@RequestMapping("/fetch") //Fetch one by one
	public String getfetchOneByOne(@RequestBody recordclazz records) {
		
	        BasicConfigurator.configure();
	        // create an empty model
	        Model model = ModelFactory.createDefaultModel();
	        InputStream in = FileManager.get().open(inputFileName);
	        if (in == null) {
	            throw new IllegalArgumentException("File: " + inputFileName + " not found");
	        }
	        // read the RDF/XML file
	        model.read(inputFileName);
	        String username = records.getUser();
	        // write it to standard outString queryString = " .... " ;
	        String queryString =
	        		"select ?name ?id ?password \r\n" + 
	        		"	where {\r\n" + 
	        		"      <http://userdata/#"+username+"> \r\n" + 
	        		"\r\n" + 
	        		"      <http://www.w3.org/2001/vcard-rdf/3.0#NAME>\r\n" + 
	        		"                 ?name ;\r\n" + 
	        		"      <http://www.w3.org/2001/vcard-rdf/3.0#Pcode>\r\n" + 
	        		"                  ?password ;\r\n" + 
	        		"}";


	        Query query = QueryFactory.create(queryString) ;
	        //String responseIs = null;
	        QuerySolution soln;

	        try (QueryExecution qexec = QueryExecutionFactory.create(query, model))
	        {
	        	
	        	ResultSet results = qexec.execSelect() ;
	            System.out.println(results.toString());
	            while(results.hasNext())
	            {
	            	 soln = results.nextSolution();
	            	//Literal name =soln.getLiteral(x)
	            	System.out.println("lets see the result"+soln);

	            }    	

	        }	
	        
		
		return "ohh yeah";
/*		
		
		
        // create an empty model
        Model model = ModelFactory.createDefaultModel();
       
        // use the FileManager to find the input file
        InputStream in = FileManager.get().open(inputFileName);
        if (in == null) {
            throw new IllegalArgumentException( "File: " + inputFileName + " not found");
        }
        
        // read the RDF/XML file
        model.read(new InputStreamReader(in), "");
        
        // retrieve the vcard resource from the model
        Resource vcard = model.getResource(records.getQry());
        
        String sparqlQuery =
        		"select ?n ?i ?p \r\n" + 
        		"	where {\r\n" + 
        		"      <http://userdata/deepak> \r\n" + 
        		"\r\n" + 
        		"      <http://www.w3.org/2001/vcard-rdf/3.0#NAME>\r\n" + 
        		"                 ?n ;\r\n" + 
        		"      <http://www.w3.org/2001/vcard-rdf/3.0#Pcode>\r\n" + 
        		"                  ?p ;\r\n" + 
        		"}";
        Query query = QueryFactory.create(sparqlQuery) ;
        //String responseIs = null;

        try (QueryExecution qexec = QueryExecutionFactory.create(query, model))
        {

            Model results = qexec.execConstruct() ;
            StmtIterator iter = results.listStatements();
            System.out.println(results.toString());
            while(iter.hasNext())
            {
                System.out.println(iter.next());

            }
        }
//        System.out.println("   Response after rdf read");
//     model.write(System.out,"TURTLE");
//     System.out.println(".............................");
//     model2.write(System.out,"TURTLE");
       // System.out.println(responseIs);
        // write it to standard out
        model.write(System.out);
     //   return "The  userName and Password are:" +username + password;
        return null;
        
        
        
     /*   
        // retrieve the value of the N property
        Resource name = (Resource) vcard.getRequiredProperty(VCARD.N)
                                        .getObject(); 
        
        System.out.printf("the value of name variable is ",name);
       //  retrieve the given name property
        String id = vcard.getRequiredProperty(VCARD.UID)
                               .getString();
        String username = vcard.getRequiredProperty(VCARD.NAME)
                			   .getString();
        String password = vcard.getRequiredProperty(VCARD.Pcode)
        					   .getString();
       // System.out.println("The fullname is"  +id +username +password);
        System.out.println("The username and password is:"  +username +password);*/
		
		
	}
		





@RequestMapping("/fetch-all") //Fetch one by one
public String fetchAll(@RequestBody recordclazz records) {
	
        BasicConfigurator.configure();
        // create an empty model
        Model model = ModelFactory.createDefaultModel();
        InputStream in = FileManager.get().open(inputFileName);
        if (in == null) {
            throw new IllegalArgumentException("File: " + inputFileName + " not found");
        }
        // read the RDF/XML file
        model.read(inputFileName);
        String queryString = records.getQry();
        // write it to standard outString queryString = " .... " ;
      /*  String queryString =
        		"select ?name ?id ?password \r\n" + 
        		"	where {\r\n" + 
        		"      <http://userdata/#"+username+"> \r\n" + 
        		"\r\n" + 
        		"      <http://www.w3.org/2001/vcard-rdf/3.0#NAME>\r\n" + 
        		"                 ?name ;\r\n" + 
        		"      <http://www.w3.org/2001/vcard-rdf/3.0#Pcode>\r\n" + 
        		"                  ?password ;\r\n" + 
        		"}";
        		*/


        Query query = QueryFactory.create(queryString) ;
        //String responseIs = null;
        QuerySolution soln;

        try (QueryExecution qexec = QueryExecutionFactory.create(query, model))
        {
        	
        	ResultSet results = qexec.execSelect() ;
            System.out.println(results.toString());
            while(results.hasNext())
            {
            	 soln = results.nextSolution();
            //	Literal name =soln.getLiteral("NAME")
            	System.out.println("lets see the result"+soln);

            }    	

        }	
        
	
	return "ohh yeah";
}
}