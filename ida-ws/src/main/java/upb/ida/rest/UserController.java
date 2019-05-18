package upb.ida.rest;

import java.io.ByteArrayOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

// username is unique;
@RestController
public class UserController {
    private RDFConnectionRemoteBuilder builder = RDFConnectionFuseki.create()
            .destination("http://localhost:3030/user");

    @RequestMapping("/new")
    public Boolean insert(@RequestBody UserData record) throws NoSuchAlgorithmException {
    	
    	String name = record.getName();
    	String userName = record.getUsername();
		String password = record.getPassword();
		String newHashPass =  hashPassword(password);
		password = newHashPass;
		System.out.println("SHA-256 HASH:"+ password);
		
//		System.out.println("name,    "+name);
//		System.out.println("username   "+userName);
//		System.out.println("password    "+password);
        // In this variation, a connection is built each time.
        try ( RDFConnectionFuseki conn = (RDFConnectionFuseki)builder.build() ) {
        	
            UpdateRequest request = UpdateFactory.create();
            request.add("PREFIX dc: <http://www.w3.org/2001/vcard-rdf/3.0#>\r\n" + 
            		"PREFIX ab:<http://userdata/#>\r\n" + 
            		"INSERT DATA{ab:"+userName+" dc:name \""+name+"\"; dc:username \""+userName+"\" ; dc:password \""+password+"\" .}");            	
            conn.update(request);
            System.out.println(request);
        }
        return true;
    }

    @RequestMapping("/delete")
    public Boolean delete(@RequestBody UserData record) throws NoSuchAlgorithmException {
    	
    	String name = record.getName();
    	String userName = record.getUsername();
		String password = record.getPassword();
		String newHashPass =  hashPassword(password);
		password = newHashPass;

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
    public Boolean update(@RequestBody UserData record) throws NoSuchAlgorithmException {    	

    	String name = record.getName();
    	String userName = record.getUsername(); //username is unique
		String password = record.getPassword();
		String newHashPass =  hashPassword(password);
		password = newHashPass;
    	
    	
        // In this variation, a connection is built each time.
        try ( RDFConnectionFuseki conn = (RDFConnectionFuseki)builder.build() ) {
            UpdateRequest request = UpdateFactory.create();

            // The idea is SPARQL is not for relational data! Its for graph data
            // So here we are just deleting the old recorded and inserting new one
            
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
            		"INSERT DATA{ab:"+name+" dc:name \""+name+"\"; dc:username \""+userName+"\" ; dc:password \""+password+"\" .}");            	
            
            conn.update(request);
        }
        return true;
    }

    @GetMapping("/selecta")
    public String select() {
        // In this variation, a connection is built each time.
        try ( RDFConnectionFuseki conn = (RDFConnectionFuseki)builder.build() ) {
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
    public String select2(@RequestBody UserData record) {
    //	String name = record.getName();
    	String userName = record.getUsername();
     // In this variation, a connection is built each time.
    	System.out.println("name is:"+userName);
    
        try ( RDFConnectionFuseki conn = (RDFConnectionFuseki)builder.build() ) {
            QueryExecution qExec = conn.query("prefix ab:<http://userdata/#"+userName+"> \r\n" + 
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
    
    
//}

//public class HashingPassword {

	//public static void main(String[] args) throws NoSuchAlgorithmException {
	public static  String hashPassword(String Pass) throws NoSuchAlgorithmException {
		System.out.println("Hello world");
		
	//	String data = "hello world";
		String data = Pass;
		
		String algorithm = "SHA-256"; //MD5
		generateHash(data, algorithm);
		System.out.println("SHA-256 HASH:"+ generateHash(data, algorithm));
		return generateHash(data, algorithm);

	}

	private static String generateHash(String data, String algorithm) throws NoSuchAlgorithmException {
		MessageDigest digest = MessageDigest.getInstance(algorithm);
		digest.reset();
		byte[] hash = digest.digest(data.getBytes());
		return bytesToStringHex(hash);
		
	}
	private final static char[] hexArray = "0123456789ABCDEF".toCharArray();

	private static String bytesToStringHex(byte[] bytes) {
		char[] hexChars = new char[bytes.length*2];
		for(int j=0;j<bytes.length; j++) {
			int v = bytes[j] & 0xFF;
			hexChars[j * 2] = hexArray[v >>> 4];
			hexChars[j * 2 + 1 ] = hexArray[v & 0x0F];
		}
		
		
		return new String(hexChars);
	}

}


