package upb.ida.controllers;

import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdfconnection.RDFConnection;
import org.apache.jena.rdfconnection.RDFConnectionFactory;
import org.apache.jena.util.FileManager;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@RestController
public class SparqlQueryController {

//    protected static String fusekiQueryPath = "http://localhost:3330/ds/query";
//    String outputStatement = null;

    @RequestMapping(value = "/queryParam")
    public String getbyArtist(@RequestParam("Param") String queryParam, @RequestParam("file") String datasetName){
        Model model = FileManager.get().loadModel(System.getProperty("user.dir") + "/uploads/" + datasetName + ".ttl");
        String queryString = queryParam;
        Query query = QueryFactory.create(queryString);
        QueryExecution qexec = QueryExecutionFactory.create(query, model);
        ResultSet results = qexec.execSelect();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ResultSetFormatter.outputAsJSON(outputStream, results);
        String json = new String(outputStream.toByteArray());
        return json;
    }
}
