package upb.ida.rest;

import upb.ida.bean.ResponseBean;
import upb.ida.domains.User;
import upb.ida.constant.IDALiteral;
import upb.ida.service.UserService;
//import upb.ida.smtp.EmailForSignup;

import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFormatter;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdfconnection.RDFConnectionFuseki;
import org.apache.jena.rdfconnection.RDFConnectionRemoteBuilder;
import org.apache.jena.update.UpdateFactory;
import org.apache.jena.update.UpdateRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.ByteArrayOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@CrossOrigin(origins = "*", allowCredentials = "true")
@RequestMapping("/user")
public class UserController {
	
	private RDFConnectionRemoteBuilder builder = RDFConnectionFuseki.create().destination("http://127.0.0.1:3030/user");


	@Autowired
    private UserService userService;
	
	@Autowired
	private ResponseBean responseBean;

    @RequestMapping(value="/list", method=RequestMethod.GET)
    @ResponseBody
    public ResponseBean listUsers(){
    //	List<User> users = userService.listAllUsers();
//    	if(users == null)
//    	{
//    		responseBean.setErrCode(IDALiteral.FAILURE_USERLIST);
//    	}
//    	else 
//    	{
    	try (RDFConnectionFuseki conn = (RDFConnectionFuseki) builder.build()) {
			QueryExecution qExec = conn.query("prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> "
					+ "prefix owl: <http://www.w3.org/2002/07/owl#> "
					+ "SELECT ?subject ?predicate ?object WHERE {?subject ?predicate ?object }");
			ResultSet results = qExec.execSelect();
			
	    		Map<String, Object> returnMap = new HashMap<String, Object>();
	    		returnMap.put("users", results);
	    		responseBean.setPayload(returnMap);
			 
//			// Converting results into JSON
//			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//			ResultSetFormatter.outputAsJSON(outputStream, rs);
//			return new String(outputStream.toByteArray());
		}
//    	}
        return responseBean; 
    }

    @RequestMapping(value="/update", method=RequestMethod.POST)
    @ResponseBody
    public ResponseBean updateUser(@RequestBody final User record) throws NoSuchAlgorithmException{
    //	User updatedUser = userService.saveOrUpdate(record);
//    	if(updatedUser == null)
//    	{
//    		responseBean.setErrCode(IDALiteral.FAILURE_UPDATEUSER);
//    	}
//    	else 
//    	{
    	String newPassword = record.getNewpassword();
		String firstname = record.getFirstname();
		String lastname = record.getLastname();
		String userName = record.getUsername();
		String password = record.getPassword();
		String newHashPass = hashPassword(password);
		String newPasswordHash = hashPassword(newPassword);
		password = newHashPass;
		newPassword = newPasswordHash;
		
		
		try (RDFConnectionFuseki conn = (RDFConnectionFuseki) builder.build()) {

			UpdateRequest request = UpdateFactory.create();

			// The idea is SPARQL is not for relational data! Its for graph data
			// So here we are just deleting the old recorded and inserting new one

			request.add("PREFIX dc: <http://www.w3.org/2001/vcard-rdf/3.0#>\r\n" + "PREFIX ab:<http://userdata/#>\r\n"
					+ "DELETE DATA\r\n" + "{\r\n" + "  ab:" + userName + "    dc:firstname \"" + firstname + "\" ;\r\n"
					+ "dc:password \"" + password + "\" ;\r\n" + "dc:lastname\"" + lastname + "\"; \r\n" + "}");
			request.add("PREFIX dc: <http://www.w3.org/2001/vcard-rdf/3.0#>\r\n" + "PREFIX ab:<http://userdata/#>\r\n"
					+ "INSERT DATA{ab:" + userName + " dc:firstname \"" + firstname + "\" ; \r\n" + "dc:lastname\"" +lastname + "\"; dc:password \"" + newPassword
					+ "\" .}");
			conn.update(request);
		}
		
    		Map<String, Object> returnMap = new HashMap<String, Object>();
    		returnMap.put("updatedUser", record);
    		responseBean.setPayload(returnMap);
 //   	}
        return responseBean; 
    }

    @RequestMapping(value="/new", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public ResponseBean createNewUser(@RequestBody final User record) throws Exception {
    	
//    	if(userService.getByUsername(record.getUsername()) != null)
//    	{
//    		responseBean.setErrCode(IDALiteral.FAILURE_USEREXISTS);
//    		return responseBean;
//    	}
    //	User newUser = userService.saveOrUpdate(record);
//    	if(record == null)
//    	{
//    		responseBean.setErrCode(IDALiteral.FAILURE_NEWUSER);
//    	}
    	//else 
    //	{
    		String firstname = record.getFirstname();
    		String lastname = record.getLastname();
    		String userName = record.getUsername();
    		String password = record.getPassword();
    		String newHashPass = hashPassword(password);
    		password = newHashPass;
    		System.out.println("SHA-256 HASH:" + password);
    		
    		try (RDFConnectionFuseki conn = (RDFConnectionFuseki) builder.build()) {

    			UpdateRequest request = UpdateFactory.create();
    			request.add("PREFIX dc: <http://www.w3.org/2001/vcard-rdf/3.0#>\r\n" + "PREFIX ab:<http://userdata/#>\r\n"
    					+ "INSERT DATA{ab:" + userName + " dc:firstname \"" + firstname + "\"; dc:lastname \"" + lastname + "\";  dc:username \"" + userName
    					+ "\" ; dc:password \"" + password + "\";  .}");
    			conn.update(request);
    			System.out.println(request);
    		}
    		
    		Map<String, Object> returnMap = new HashMap<String, Object>();
    		returnMap.put("newUser", record);
    		responseBean.setPayload(returnMap);
//    		TODO: This email functionality is commented just for now. (Tested and working)
//    		try{
//    			EmailForSignup.sendEmail(newUser.getUsername());
//    		}catch(Exception ex)
//    		{
//    			responseBean.setErrCode(IDALiteral.FAILURE_EMAILSENT);
//    			responseBean.setErrMsg(ex.getMessage());
//    		}
    //	}
        return responseBean; 
    }

    @RequestMapping(value="/delete/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    public ResponseBean deleteUser(@PathVariable String id){
    	if (userService.getById(Long.valueOf(id)) == null)
    	{
    		responseBean.setErrCode(IDALiteral.ALREADY_LOGGEDIN);
    	}else
    	{
    		userService.delete(Long.valueOf(id));
    		responseBean.setErrMsg("User Deleted");
    	}
    	
    	return responseBean;
    }
    
    
    public static User list(String clientUserName) {
		String userName = clientUserName;
		String serviceURI = "http://localhost:3030/user";
		String query1	=	"prefix ab:<http://userdata/#" + userName + "> \r\n"
				+ "prefix cd: <http://www.w3.org/2001/vcard-rdf/3.0#>\r\n" + "select ?name ?username ?password \r\n"
				+ "	where {ab: cd:name ?name ; cd:password ?password ; cd:username ?username .}\r\n" + "";
		String fetchedUserName = null;
		String fetchedFirstName = null;
		String fetchedLastName = null;
		String fetchedPassword = null;
		User obj = null;
		
		QueryExecution q = QueryExecutionFactory.sparqlService(serviceURI, query1);
		ResultSet results = q.execSelect();

		while (results.hasNext()) {
			QuerySolution soln = results.nextSolution();
			// assumes that you have an "?x" in your query
			RDFNode x = soln.get("username");
			RDFNode y = soln.get("firstname");
			RDFNode w = soln.get("lastname");
			RDFNode z = soln.get("password");
			System.out.println("username"+x);
			System.out.println("firstname"+y);
			System.out.println("lastname"+w);
			System.out.println("password"+z);
			
			fetchedUserName = x.toString();
			fetchedFirstName = y.toString();
			fetchedPassword = z.toString();
			fetchedLastName = w.toString();
			
			obj = new User(fetchedUserName,fetchedFirstName,fetchedLastName,fetchedPassword);
		}
		
		return obj;
	}
    
    
    
    
    
    
    
    
	// password hashing

	public static String hashPassword(String Pass) throws NoSuchAlgorithmException {

		String data = Pass;

		String algorithm = "SHA-256"; 
		generateHash(data, algorithm);
		// System.out.println("SHA-256 HASH:"+ generateHash(data, algorithm));
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
		char[] hexChars = new char[bytes.length * 2];
		for (int j = 0; j < bytes.length; j++) {
			int v = bytes[j] & 0xFF;
			hexChars[j * 2] = hexArray[v >>> 4];
			hexChars[j * 2 + 1] = hexArray[v & 0x0F];
		}

		return new String(hexChars);
	}

}
