package upb.ida.controllers;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.*;
import java.nio.charset.StandardCharsets;

@RestController
public class CSVAdapterController {

    @PostMapping("/adapter/csv")
    public String convert(@RequestBody String text) {
        InputStream inputStream = new ByteArrayInputStream(text.getBytes(StandardCharsets.UTF_8));

        Model m = ModelFactory.createDefaultModel();
        // (TODO) http://example.com must not be fixed
        m.read(inputStream, "http://example.com", "csv");
        m.setNsPrefix("test", "http://example.com#");
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        // Converting into turtle format
        m.write(stream, "ttl");
        return new String(stream.toByteArray());
    }
}