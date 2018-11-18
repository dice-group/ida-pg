package upb.ida.controllers;

import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.util.FileManager;

import org.springframework.web.bind.annotation.*;

@RestController
public class sparqlQueryController {
    @GetMapping("/sparkle")
    public String sparqlTest(){
        Model model = FileManager.get().loadModel("/home/programmercore/Documents/PG/ida/ida-udf-ws/docs/sample_person.ttl");
        String queryString ="SELECT ?title{" +
                "<http://dbpedia.org/resource/Karel_Matěj_Čapek-Chod> <http://dbpedia.org/ontology/birthPlace> ?title" +
                "}";
        Query query = QueryFactory.create(queryString);
        QueryExecution qexec = QueryExecutionFactory.create(query, model);

        try{
            ResultSet results = qexec.execSelect();
            while(results.hasNext()){
                QuerySolution soln = results.nextSolution();
                Literal name = soln.getLiteral("x");
                return ("Done: " + name);
//                return name;
            }
        } finally {
            qexec.close();
        }
        return null;
    }
}
