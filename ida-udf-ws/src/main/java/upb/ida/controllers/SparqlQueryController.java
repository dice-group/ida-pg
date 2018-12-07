package upb.ida.controllers;

import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdfconnection.RDFConnection;
import org.apache.jena.rdfconnection.RDFConnectionFactory;
import org.apache.jena.system.Txn;
import org.apache.jena.util.FileManager;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@RestController
public class SparqlQueryController {

    @RequestMapping(value = "/")
    public String getbyArtist(@RequestParam("Param") String queryParam){
        queryParam = "SELECT distinct ?c WHERE { ?a test:City ?c . }";
        String queryString = queryParam;
        RDFConnection conn = RDFConnectionFactory.connect("http://localhost:3330/ds/query");
        conn.load(System.getProperty("user.dir") + "/uploads/dataset1.ttl");
        QueryExecution queryExecution = conn.query(queryString);
        ResultSet results = queryExecution.execSelect();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ResultSetFormatter.outputAsJSON(outputStream, results);
        String json = new String(outputStream.toByteArray());
        conn.end();
        return json;
    }
}
