package upb.ida.controllers;

import org.apache.jena.fuseki.main.FusekiServer;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.DatasetFactory;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdfconnection.RDFConnectionFuseki;
import org.apache.jena.rdfconnection.RDFConnectionRemoteBuilder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

@RestController
public class UploadDataset {
    String separator = File.separator;
    @PostMapping("/file")
    public String rdfUpload(@RequestParam(value="file") MultipartFile file){
//        String dsURL = "http://localhost:"+3332+"/ds";
//        RDFConnectionRemoteBuilder builder = RDFConnectionFuseki.create().destination(dsURL);

        try {
            Dataset dataset = DatasetFactory.create(System.getProperty("user.dir") + separator +"uploads" + separator + "sampleresult.ttl");
//            conn.put(file);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return "done";
    }
}
