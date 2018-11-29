package upb.ida.service;

import java.util.List;
import java.util.Map;

import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFormatter;
import org.springframework.stereotype.Service;

//import upb.ida.constant.IDALiteral;

@Service
public class RdfDataService {
	
	
	@SuppressWarnings("unchecked")
	public List<Map<String, String>> getData(Map<String, Object> req) {
		//Fetch data from rdf end point
		String sparql = (String) req.get("query");
		QueryExecution qe = QueryExecutionFactory.sparqlService(
                "http://localhost:3030/query", sparql);
        ResultSet results = qe.execSelect();
     // write to a ByteArrayOutputStream
//        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//
//        ResultSetFormatter.outputAsJSON(outputStream, results);
//
//        // and turn that into a String
//        String json = new String(outputStream.toByteArray());
        ResultSetFormatter.out(System.out, results);
        List<Map<String, String>> data = (List<Map<String, String>>) results;
        return data;
	}

}
