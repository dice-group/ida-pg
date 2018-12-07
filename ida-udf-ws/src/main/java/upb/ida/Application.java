package upb.ida;

import org.apache.jena.fuseki.main.FusekiServer;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.DatasetFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

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