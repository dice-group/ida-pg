package upb.ida.controllers;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/adapter/csv")
public class CSVAdapterController {

    @PostMapping("/")
    public String convert(@RequestBody String text) {
        // (TODO): Request's Validation
        return new String(csvToRDF(text.getBytes(StandardCharsets.UTF_8)));
    }

    @PostMapping("/file")
    public String singleFileUpload(@RequestParam(value="file") MultipartFile file) {
        // (TODO): Request's Validation

        try {
            // Reading file's content in bytes
            byte[] bytes = file.getBytes();
            Path path = Paths.get(System.getProperty("user.dir") + "/uploads/", file.getOriginalFilename() + ".ttl");
            Files.write(path, csvToRDF(bytes));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // (TODO) We need proper Response bean
        return "done";
    }

    private byte[] csvToRDF(byte[] bytes) {
        InputStream inputStream = new ByteArrayInputStream(bytes);

        Model m = ModelFactory.createDefaultModel();
        // (TODO) http://example.com must not be fixed
        m.read(inputStream, "http://example.com", "csv");
        m.setNsPrefix("test", "http://example.com#");
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        // Converting into turtle format
        m.write(stream, "ttl");
        return stream.toByteArray();
    }
}