package upb.ida.rest;

import java.io.ByteArrayOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFormatter;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdfconnection.RDFConnectionFuseki;
import org.apache.jena.rdfconnection.RDFConnectionRemoteBuilder;
import org.apache.jena.update.UpdateAction;
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

import upb.ida.bean.ResponseBean;
import upb.ida.constant.IDALiteral;
import upb.ida.domains.User;
import upb.ida.service.UserService;
//import upb.ida.smtp.EmailForSignup;

@Controller
@CrossOrigin(origins = "*", allowCredentials = "true")
@RequestMapping("/user")
public class UserController {

//	private RDFConnectionRemoteBuilder builder = RDFConnectionFuseki.create().destination("http://127.0.0.1:3030/user");

	@Autowired
	private ResponseBean responseBean;

	@RequestMapping(value = "/list", method = RequestMethod.GET)
	@ResponseBody
	public ResponseBean listUsers() {
		RDFConnectionRemoteBuilder builder = RDFConnectionFuseki.create().destination("http://127.0.0.1:3030/user");
		// List<User> users = userService.listAllUsers();
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


//			// Converting results into JSON
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			ResultSetFormatter.outputAsJSON(outputStream, results);
			String jsonOutput = new String(outputStream.toByteArray());

			System.out.println("jsonoutput:"+jsonOutput);
				
			Map<String, Object> returnMap = new HashMap<String, Object>();
			returnMap.put("users", jsonOutput);
			responseBean.setPayload(returnMap);
			conn.close();
			System.out.println("responseBean:"+responseBean);
		}
		
//    	}

		return responseBean;
	}

	@RequestMapping(value = "/update", method = RequestMethod.POST)
	@ResponseBody
	public ResponseBean updateUser(@RequestBody final User updatedrecord) throws NoSuchAlgorithmException {
		RDFConnectionRemoteBuilder builder = RDFConnectionFuseki.create().destination("http://127.0.0.1:3030/user");
		// User updatedUser = userService.saveOrUpdate(record);
//    	if(updatedUser == null)
//    	{
//    		responseBean.setErrCode(IDALiteral.FAILURE_UPDATEUSER);
//    	}
//    	else 
//    	{
		String password = updatedrecord.getPassword();
		User oldRecord = UserService.getByUsername(updatedrecord.getUsername());

		// check new password needs to be updated or older one needs to be used
		if (password == null || password == "") {
			password = oldRecord.getPassword();
		} else {
			password = hashPassword(updatedrecord.getPassword());

			// In this variation, a connection is built each time.
			try (RDFConnectionFuseki conn = (RDFConnectionFuseki) builder.build()) {

				UpdateRequest request = UpdateFactory.create();

				// The idea is SPARQL is not for relational data! Its for graph data
				// So here we are just deleting the old recorded
			
				String insertString1 =
						" PREFIX dc: <http://www.w3.org/2001/vcard-rdf/3.0#> "          +
						" PREFIX ab: <http://userdata/#" + oldRecord.getUsername() +"> "   +
						" DELETE DATA "                                                 +
						" { ab:			 				   "     +
					//	"dc:username \"" + oldRecord.getUsername() + "\" ; "      +
						"dc:firstname \""  + oldRecord.getFirstname() +  "\" ; "  +
						"dc:lastname \""  + oldRecord.getLastname() +  "\" ; "  +						
						"dc:userrole \""  + oldRecord.getUserRole() +  "\" ; "  +
						"dc:password \""  + oldRecord.getPassword() +  "\" . "+
						" } ";
				request.add(insertString1);
				conn.update(request);
				
				String insertString =
						" PREFIX dc: <http://www.w3.org/2001/vcard-rdf/3.0#> "          +
						" PREFIX ab: <http://userdata/#" + oldRecord.getUsername() +"> "   +
						" INSERT DATA "                                                 +
						" { ab:			 				   "     +
					//	"dc:username \"" + oldRecord.getUsername() + "\" ; "      +
						"dc:firstname \""  + updatedrecord.getFirstname() +  "\" ; "  +
						"dc:lastname \""  + updatedrecord.getLastname() +  "\" ; "  +						
						"dc:userrole \""  + updatedrecord.getUserRole() +  "\" ; "  +
						"dc:password \""  + password +  "\" . "+
						" } ";
	
				request.add(insertString);
				conn.update(request);
				//doing mapping with resposeBean
				Map<String, Object> returnMap = new HashMap<String, Object>();
				returnMap.put("updatedUser", updatedrecord.getUsername());
				responseBean.setPayload(returnMap);
				conn.close();
			}
		}
		return responseBean;
	}

	@RequestMapping(value = "/new", method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.CREATED)
	@ResponseBody
	public ResponseBean createNewUser(@RequestBody final User record) throws Exception {
		
		RDFConnectionRemoteBuilder builder = RDFConnectionFuseki.create().destination("http://127.0.0.1:3030/user");
//		if (record == null) {
//			responseBean.setErrMsg("invalid inputs");
//			return responseBean;
//		}
		
//		if (UserService.getByUsername(record.getUsername()) != null) {
//			responseBean.setErrCode(IDALiteral.FAILURE_USEREXISTS);
//			System.out.println("strt-02.01");
//			return responseBean;
//		}
        if(1==-1) {}
		else {
			
			// In this variation, a connection is built each time.
			try (RDFConnectionFuseki conn = (RDFConnectionFuseki) builder.build()) {
			
				UpdateRequest request = UpdateFactory.create();			
				String insertString =
						" PREFIX dc: <http://www.w3.org/2001/vcard-rdf/3.0#> "          +
						" PREFIX ab: <http://userdata/#" + record.getUsername() +"> "   +
						" INSERT DATA "                                                 +
						" { ab:			 				   "     +
						"dc:username \"" + record.getUsername() + "\" ; "      +
						"dc:firstname \""  + record.getFirstname() +  "\" ; "  +
						"dc:lastname \""  + record.getLastname() +  "\" ; "  +						
						"dc:userrole \""  + record.getUserRole() +  "\" ; "  +
						"dc:password \""  + hashPassword(record.getPassword()) +  "\" . "+
						" } ";
	
				request.add(insertString);
				
				conn.update(request);
				
				Map<String, Object> returnMap = new HashMap<String, Object>();
				returnMap.put("newUser", record.getUsername());
				responseBean.setPayload(returnMap);
				
				conn.close();
			}
		
		}

		return responseBean;
	}

	@RequestMapping(value = "/delete/{usernames:.+}", method = RequestMethod.DELETE)
	@ResponseBody
	public ResponseBean deleteUser(@PathVariable String usernames) {
		RDFConnectionRemoteBuilder builder = RDFConnectionFuseki.create().destination("http://127.0.0.1:3030/user");
		User record = UserService.getByUsername(usernames);
//		if (record.getUsername() == null) {
//			responseBean.setErrMsg("user not found");
//			return responseBean;
//			// no record found with this username
//			// responseBean.setErrCode(IDALiteral.ALREADY_LOGGEDIN);
//		} else {// In this variation, a connection is built each time.
			try (RDFConnectionFuseki conn = (RDFConnectionFuseki) builder.build()) {
				UpdateRequest request = UpdateFactory.create();
			
				String insertString =
						" PREFIX dc: <http://www.w3.org/2001/vcard-rdf/3.0#> "          +
						" PREFIX ab: <http://userdata/#" + record.getUsername() +"> "   +
						" DELETE DATA "                                                 +
						" { ab:			 				   "     +
						"dc:username \"" + record.getUsername() + "\" ; "      +
						"dc:firstname \""  + record.getFirstname() +  "\" ; "  +
						"dc:lastname \""  + record.getLastname() +  "\" ; "  +						
						"dc:userrole \""  + record.getUserRole() +  "\" ; "  +
						"dc:password \""  + record.getPassword() +  "\" . "+
						" } ";
				request.add(insertString);			
				
				conn.update(request);
				responseBean.setErrMsg("User Deleted");
				conn.close();
			}

	//	}

		return responseBean;
	}

	public static User list(String clientUserName) {
		String userName = clientUserName;
		String serviceURI = "http://127.0.0.1:3030/user";
		System.out.println("userName"+userName);
		String query1 = " PREFIX ab: <http://userdata/#"+userName +"> "   +
				 "prefix dc: <http://www.w3.org/2001/vcard-rdf/3.0#>select ?firstname ?lastname ?username ?password  ?userrole where {ab: dc:firstname ?firstname ;dc:lastname ?lastname; dc:password ?password ; dc:userrole ?userrole; dc:username ?username .}";

		User obj = null;

		QueryExecution q = QueryExecutionFactory.sparqlService(serviceURI, query1.trim());
		ResultSet results = q.execSelect();

		while (results.hasNext()) {
			QuerySolution soln = results.nextSolution();
			// assumes that you have an "?x" in your query.
			RDFNode x = soln.get("username");
			RDFNode y = soln.get("firstname");
			RDFNode w = soln.get("lastname");
			RDFNode z = soln.get("password");
			RDFNode u = soln.get("userrole");

			String fetchedUserName = x.toString();
			String fetchedFirstName = y.toString();
			String fetchedPassword = z.toString();
			String fetchedLastName = w.toString();
			String fetchedUserRole = u.toString();

			obj = new User(fetchedUserName, fetchedPassword, fetchedFirstName, fetchedLastName,fetchedUserRole);
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
