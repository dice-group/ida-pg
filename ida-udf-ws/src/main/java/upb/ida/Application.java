package upb.ida;

import org.apache.jena.query.Dataset;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.io.IOException;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        String separator = File.separator;
        String command = "java -jar .."+separator+".."+separator+".."+separator+".."+separator+".."+separator+
        "apache-jena-fuseki-3.9.0"+separator+"fuseki-server.jar --update --mem /ds";
//        Dataset dataset =
//
//        RDFConnectionFuseki server = RDFConnectionFuseki.create().build().putDataset();
        File directory = new File(".");
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.directory(directory);


        try {
            processBuilder.start();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        SpringApplication.run(Application.class, args);
    }
}