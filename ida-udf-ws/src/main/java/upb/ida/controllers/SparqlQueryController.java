package upb.ida.controllers;

import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.util.FileManager;

import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;

@RestController
public class SparqlQueryController {

    @GetMapping(value = "/sparql")
    public String getbyArtist(@RequestParam("query") String queryString, @RequestParam("datasetName") String datasetName) {
        // TODO: Error handling e.g. if dataset name is invalid
        Model model = FileManager.get().loadModel(System.getProperty("user.dir") + "/uploads/" + datasetName + ".ttl");
        Query query = QueryFactory.create(queryString);
        QueryExecution qexec = QueryExecutionFactory.create(query, model);
        ResultSet results = qexec.execSelect();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ResultSetFormatter.outputAsJSON(outputStream, results);
        String json = new String(outputStream.toByteArray());
        return json;
    }
}
