package upb.ida;

import org.apache.jena.fuseki.Fuseki;
import org.apache.jena.fuseki.main.FusekiServer;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.DatasetFactory;
import org.apache.jena.rdfconnection.RDFConnection;
import org.apache.jena.rdfconnection.RDFConnectionFactory;
import org.apache.jena.rdfconnection.RDFConnectionFuseki;
import org.apache.jena.rdfconnection.RDFConnectionRemoteBuilder;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.sparql.core.DatasetGraph;
import org.apache.jena.sparql.core.DatasetGraphFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.xml.crypto.Data;
import java.io.File;
import java.io.IOException;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {

        Dataset dataset = DatasetFactory.createTxnMem();
        FusekiServer fusekiServer = FusekiServer.create()
                .add("/ds", dataset, true)
                .build();
        fusekiServer.start();

        SpringApplication.run(Application.class, args);
    }
}