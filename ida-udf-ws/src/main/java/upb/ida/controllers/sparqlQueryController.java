package upb.ida.controllers;

import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.util.FileManager;

import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;

@RestController
public class sparqlQueryController {

    @RequestMapping(value = "/queryParam")
    public String getbyArtist(@RequestParam("Param") String queryParam){
        Model model = FileManager.get().loadModel("/home/programmercore/Documents/PG/ida/ida-udf-ws/docs/sample1.rdf");
        String queryString ="PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>" +
                "PREFIX cd: <http://www.recshop.fake/cd#> " + queryParam;

        Query query = QueryFactory.create(queryString);
        QueryExecution qexec = QueryExecutionFactory.create(query, model);
        ResultSet results = qexec.execSelect();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ResultSetFormatter.outputAsJSON(outputStream, results);

        String json = new String(outputStream.toByteArray());
        return (json);
    }
}