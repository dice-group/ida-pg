package com.ida.ida_task;
import java.io.ByteArrayOutputStream;

import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFormatter;
import org.apache.jena.rdfconnection.RDFConnectionFuseki;
import org.apache.jena.rdfconnection.RDFConnectionRemoteBuilder;
import org.apache.jena.update.UpdateFactory;
import org.apache.jena.update.UpdateRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
    private RDFConnectionRemoteBuilder builder = RDFConnectionFuseki.create()
            .destination("http://localhost:3030/test");

    // @GetMapping("/inserta")
    @RequestMapping("/insert")
    public Boolean insert(@RequestBody UserForm reco) {
    	
    	String name = reco.getName();
    	String userName = reco.getUsername();
		String password = reco.getPassword();
		
//		System.out.println("name,    "+name);
//		System.out.println("username   "+userName);
//		System.out.println("password    "+password);
        // In this variation, a connection is built each time.
        try ( RDFConnectionFuseki conn = (RDFConnectionFuseki)builder.build() ) {
        	
            UpdateRequest request = UpdateFactory.create();
            request.add("PREFIX dc: <http://www.w3.org/2001/vcard-rdf/3.0#>\r\n" + 
            		"PREFIX ab:<http://userdata/#>\r\n" + 
            		//"INSERT DATA{ab:suraj dc:name \"susadfsdfasdfraj\" ;dc:username \"suraj@gmasdfil.com\";dc:password \"32sdf1654\" .}");
            		"INSERT DATA{ab:"+userName+" dc:name \""+name+"\"; dc:username \""+userName+"\" ; dc:password \""+password+"\" .}");            	
            conn.update(request);
            System.out.println(request);
        }
        return true;
    }

    @RequestMapping("/delete")
    public Boolean delete(@RequestBody UserForm reco) {
    	
    	String name = reco.getName();
    	String userName = reco.getUsername();
		String password = reco.getPassword();

        // In this variation, a connection is built each time.
        try ( RDFConnectionFuseki conn = (RDFConnectionFuseki)builder.build() ) {
            UpdateRequest request = UpdateFactory.create();
            
            request.add("PREFIX dc: <http://www.w3.org/2001/vcard-rdf/3.0#>\r\n" + 
            		"PREFIX ab:<http://userdata/#>\r\n" + 
            		"DELETE DATA\r\n" + 
            		"{\r\n" + 
            		"  ab:"+userName+" dc:name \""+name+"\" ;\r\n" + 
            		"                  dc:password \""+password+"\" ;\r\n" + 
            		"    			   dc:username \""+userName+"\".\r\n" + 
            		"}");
           
            conn.update(request);
        }
        return true;
    }

    @RequestMapping("/update")
    public Boolean update(@RequestBody UserForm reco) {
    	

    	String name = reco.getName();
    	String userName = reco.getUsername();
		String password = reco.getPassword();
    	
    	
        // In this variation, a connection is built each time.
        try ( RDFConnectionFuseki conn = (RDFConnectionFuseki)builder.build() ) {
            UpdateRequest request = UpdateFactory.create();

            // The idea is SPARQL is not for relational data! Its for graph data
            // So here we are just deleting the old record and inserting new one
            
            request.add("PREFIX dc: <http://www.w3.org/2001/vcard-rdf/3.0#>\r\n" + 
            		"PREFIX ab:<http://userdata/#>\r\n" + 
            		"DELETE DATA\r\n" + 
            		"{\r\n" + 
            		"  ab:"+userName+"    dc:name \""+name+"\" ;\r\n" + 
            		"                 dc:password \""+password+"\" ;\r\n" + 
            		"    			  dc:username \""+userName+"\".\r\n" + 
            		"}");
            request.add("PREFIX dc: <http://www.w3.org/2001/vcard-rdf/3.0#>\r\n" + 
            		"PREFIX ab:<http://userdata/#>\r\n" + 
            		//"INSERT DATA{ab:suraj dc:name \"susadfsdfasdfraj\" ;dc:username \"suraj@gmasdfil.com\";dc:password \"32sdf1654\" .}");
            		"INSERT DATA{ab:"+name+" dc:name \""+name+"\"; dc:username \""+userName+"\" ; dc:password \""+password+"\" .}");            	
            
          //  request.add("PREFIX example: <http://example/> DELETE DATA { example:anne example:age 30 }; INSERT DATA { example:anne example:age 12 . };");
            conn.update(request);
        }
        return true;
    }

    @GetMapping("/selecta")
    public String select() {
        // In this variation, a connection is built each time.
        try ( RDFConnectionFuseki conn = (RDFConnectionFuseki)builder.build() ) {
        	// QueryExecution qExec = conn.query("prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> prefix owl: <http://www.w3.org/2002/07/owl#> SELECT ?subject ?predicate ?object WHERE {?subject ?predicate ?object }");
            QueryExecution qExec = conn.query("prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> "
            		+ "prefix owl: <http://www.w3.org/2002/07/owl#> "
            		+ "SELECT ?subject ?predicate ?object WHERE {?subject ?predicate ?object }");
            ResultSet rs = qExec.execSelect();

            // Converting results into JSON
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ResultSetFormatter.outputAsJSON(outputStream, rs);
            return new String(outputStream.toByteArray());
        }
    }
    
    @RequestMapping("/selectb") //one by one
    public String select2(@RequestBody UserForm reco) {
    	String name = reco.getName();
    //	String userName = reco.getUsername();
     // In this variation, a connection is built each time.
    	System.out.println("name is:"+name);
    
        try ( RDFConnectionFuseki conn = (RDFConnectionFuseki)builder.build() ) {
        	// QueryExecution qExec = conn.query("prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> prefix owl: <http://www.w3.org/2002/07/owl#> SELECT ?subject ?predicate ?object WHERE {?subject ?predicate ?object }");
            QueryExecution qExec = conn.query("prefix ab:<http://userdata/#"+name+"> \r\n" + 
            		"prefix cd: <http://www.w3.org/2001/vcard-rdf/3.0#>\r\n" + 
            		"select ?name ?username ?password \r\n" + 
            		"	where {ab: cd:name ?name ; cd:password ?password ; cd:username ?username .}\r\n" + 
            		"");
            
            ResultSet rs = qExec.execSelect();
            System.out.println("result is:"+rs);

            // Converting results into JSON
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ResultSetFormatter.outputAsJSON(outputStream, rs);
            String value = new String(outputStream.toByteArray());         
            
            return value;
            //return new String(outputStream.toByteArray());
        }
    }
    
    
}
