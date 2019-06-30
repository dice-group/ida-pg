package upb.ida.provider;

import java.util.Map;

import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.RDFNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.rivescript.macro.Subroutine;

import upb.ida.bean.ResponseBean;
import upb.ida.constant.IDALiteral;
import upb.ida.fdg.FDG_Util;
import upb.ida.util.FileUtil;

@Component
public class SoldierHandler implements Subroutine {
	@Autowired
	private FileUtil DemoMain;
	@Autowired
	private FDG_Util FDG_Util;
	@Autowired
	private ResponseBean responseBean;

	/**
	 * Method to create response for Force Directed Graph visualization
	 * 
	 * @param rs   - {@link call#rs}
	 * @param args - {@link call#args}
	 * @return String - pass or fail
	 */

	public String call(com.rivescript.RiveScript rs, String[] args) {

		String dbhost = System.getenv("FUSEKI_URL");
		String datasetName = "/soldier";
		String dbUrl = dbhost + datasetName;
		
		String soldierId = args[1];
		// String user = rs.currentUser();
		try {

			String actvTbl = (String) responseBean.getPayload().get("actvTbl");
			String actvDs = (String) responseBean.getPayload().get("actvDs");
			// String actvScrId = (String) responseBean.getPayload().get("actvScrid");
			String path = DemoMain.getDTFilePath(actvDs, actvTbl);
			Map<String, Object> dataMap = responseBean.getPayload();
			dataMap.put("label", "Fdg Data");
			/**
			 * function call takes file path and arguments as input to get data for force
			 * directed graph
			 */
			// System.out.println("actvScrId...gd"+actvTbl);
			System.out.println("actvTbl....SH...." + actvTbl);
			System.out.println("actvDs...SH..." + actvDs);
			System.out.println("args...SH..." + args[1]);
			System.out.println("rs...SH..." + rs);
			System.out.println("path...SH..." + path);

			/// dataMap.put("fdgData",
			/// FDG_Util.generateFDG(path,args[0].toLowerCase(),args[1],args[2]));
			// System.out.println("dataMap....SH..."+dataMap);
			// dataMap.put("actvScrId", actvScrId);
			// responseBean.setPayload(args);\

//			String query2 = " PREFIX ab: <http://userdata/#" + args[1] + "> "
//					+ "prefix dc: <http://www.w3.org/2001/vcard-rdf/3.0#>select ?firstname ?lastname ?username ?password  ?userrole where {ab: dc:firstname ?firstname ;dc:lastname ?lastname; dc:password ?password ; dc:userrole ?userrole; dc:username ?username .}";
//			
			
			String query1 = "prefix soldier: <https://www.upb.de/historisches-institut/neueste-geschichte/ssdal/data/>\r\n" + 
					"prefix dbo: <http://dbpedia.org/ontology/> \r\n" + 
					"select * where { ?s soldier:id 47540; soldier:membershipNumber ?membershipNumber.\r\n" + 
					"optional{?s soldier:NSDAPNumber  ?NSDAPNumber.}\r\n" + 
					"optional{?s soldier:DALVerified  ?DALVerified. }\r\n" + 
					"optional{?s dbo:firstName  ?firstName. }\r\n" + 
					"optional{?s dbo:lastName  ?lastName. }\r\n" + 
					"optional{?s dbo:birthDate  ?birthDate. }\r\n" + 
					"}";
			
			
					
					
		//	User obj = null;
			QueryExecution q = null;
			ResultSet results = null;
			try {
				q = QueryExecutionFactory.sparqlService(dbUrl, query1.trim());
				results = q.execSelect();
				while (results.hasNext()) {
					QuerySolution soln = results.nextSolution();
					// assumes that you have an "?x" in your query.
					RDFNode x = soln.get("membershipNumber");
					RDFNode y = soln.get("firstName");
					RDFNode w = soln.get("lastName");
					RDFNode z = soln.get("NSDAPNumber");
					RDFNode u = soln.get("birthDate");
					RDFNode v = soln.get("DALVerified");

					String membershipNumber = x.toString();
					String firstName = y.toString();
					String NSDAPNumber = z.toString();
					String lastName = w.toString();
					String birthDate = u.toString();
					String DALVerified = v.toString();
					
					System.out.println("membershipNumber." + membershipNumber);
					System.out.println("firstName." + firstName);
					System.out.println("lastName." + lastName);
					System.out.println("NSDAPNumber." + NSDAPNumber);
					System.out.println("birthDate." + birthDate);
					System.out.println("DALVerified." + DALVerified);
					
					
//					obj = new User(fetchedUserName, fetchedPassword, fetchedFirstName, fetchedLastName,
//							fetchedUserRole);
				}
			} finally {
				if (q != null)
					q.close();
			}

			responseBean.setActnCode(IDALiteral.UIA_FDG);
			return "pass";
		} finally {
			System.out.println("in finally...");
		}
//		} catch (ParseException e) {
//			
//			e.printStackTrace();
//		}
		// return "fail";
	}
}
