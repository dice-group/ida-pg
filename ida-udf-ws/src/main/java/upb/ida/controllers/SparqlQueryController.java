package upb.ida.controllers;

import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.util.FileManager;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@RestController
public class SparqlQueryController {

    protected static String fusekiQueryPath = "http://localhost:3330/ds/query";
    String outputStatement = null;

    @RequestMapping(value = "/queryParam")
    public String getbyArtist(@RequestParam("Param") String queryParam, @RequestParam("output") String output){
        FileManager.get().loadModel("/home/programmercore/Documents/PG/ida/ida-udf-ws/uploads/sampleresult.ttl");

        String queryString = "PREFIX test:  <http://example.com#>" + queryParam;
        QueryExecution qexec = QueryExecutionFactory.sparqlService(fusekiQueryPath, queryString);
        ResultSet results = qexec.execSelect();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//        String outputStatement = null;

        ResultSetFormatter.outputAsJSON(outputStream, results);
//        String json = new String(outputStream.toByteArray());
//        return json;
        if(output.toLowerCase() == "json"){

            ResultSetFormatter.outputAsJSON(outputStream, results);
            String json = new String(outputStream.toByteArray());

            outputStatement = json;

        } else if(output.toLowerCase() == "csv"){

            ResultSetFormatter.outputAsCSV(outputStream, results);
            String json = new String(outputStream.toByteArray());

            outputStatement = json;

        } else if(output.toLowerCase() == "xml"){

            ResultSetFormatter.outputAsXML(outputStream, results);
            String json = new String(outputStream.toByteArray());

            outputStatement = json;

        } else {
            outputStatement = "Format not Supported!";
        }
        return outputStatement;
    }

//    @PostMapping("/fileUpload")
//    public static void uploadFile(@RequestParam(value = "ttlfile") File file) throws IOException {
//        Model model = ModelFactory.createDefaultModel();
//        try (FileInputStream in = new FileInputStream(file)) {
//            model.read(in, null, "RDF/XML");
//        }
//
//        // upload the resulting model
//        DatasetAccessor accessor = DatasetAccessorFactory
//                .createHTTP(serviceURI);
//        accessor.putModel(m);
//    }
}
