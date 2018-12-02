package upb.ida;

import org.apache.jena.fuseki.Fuseki;
import org.apache.jena.fuseki.main.FusekiServer;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.DatasetFactory;
import org.apache.jena.rdfconnection.RDFConnectionFuseki;
import org.apache.jena.rdfconnection.RDFConnectionRemoteBuilder;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.sparql.core.DatasetGraph;
import org.apache.jena.sparql.core.DatasetGraphFactory;
import org.apache.jena.system.JenaSystem;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.xml.crypto.Data;
import java.io.File;
import java.io.IOException;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
//        Dataset dataset = DatasetFactory.create(System.getProperty("user.dir") + "/uploads/" + "sampleresult.ttl");
//        FusekiServer fusekiServer = FusekiServer.create().add("/ds", dataset).build();
        JenaSystem.init();
        Dataset dataset = DatasetFactory.createTxnMem();
        FusekiServer fusekiServer = FusekiServer.create()
                .add("/ds", dataset, true)
                .build();
        fusekiServer.start();
//        String dsURL = "http://localhost:"+3332+"/ds";
//        RDFConnectionRemoteBuilder builder = RDFConnectionFuseki.create().destination(dsURL);
//        FusekiServer server = createFusekiServer(3332).build().start();
//        fusekiServer.stop();
//        RDFConnectionRemoteBuilder builder = RDFConnectionFuseki.create().destination("http://localhost/");
//        try {
//            processBuilder.start();
//        } catch (IOException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
        SpringApplication.run(Application.class, args);
    }

    private static FusekiServer.Builder createFusekiServer(int PORT) {
        DatasetGraph dsg = DatasetGraphFactory.createTxnMem();
        return  FusekiServer.create()
                .port(PORT)
                        //.setStaticFileBase("/home/afs/ASF/jena-fuseki-cmds/sparqler")
                        .add("/ds", dsg)
                //.setVerbose(true)
                ;
    }
}