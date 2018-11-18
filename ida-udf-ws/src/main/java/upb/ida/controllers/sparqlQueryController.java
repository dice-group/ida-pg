package upb.ida.controllers;

import org.apache.jena.base.Sys;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.util.FileManager;

import org.springframework.web.bind.annotation.*;

@RestController
public class sparqlQueryController {
    @GetMapping("/sparkle")
    public String sparqlTest(){
        Model model = FileManager.get().loadModel("/home/programmercore/Documents/PG/ida/ida-udf-ws/docs/sample1.rdf");
        String queryString ="PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>" +
                "PREFIX cd: <http://www.recshop.fake/cd#>" +
                "SELECT * WHERE {" +
                "?person cd:company ?artist" +
                "}";
        Query query = QueryFactory.create(queryString);
        QueryExecution qexec = QueryExecutionFactory.create(query, model);

        try{
            ResultSet results = qexec.execSelect();
            System.out.println(results.hasNext());
            while (results.hasNext()){
                System.out.println(results.next());
            }
//            return ("Done:" + results);
//            while(results.hasNext()){
//                QuerySolution soln = results.nextSolution();
//                Literal name = soln.getLiteral("x");
//                return ("Done: " + name);
//            }
        } finally {
            qexec.close();
        }
        return "Done";
    }
}
