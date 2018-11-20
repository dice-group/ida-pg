package upb.ida.controllers;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import upb.ida.utils.UploadManager;

import java.io.*;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/adapter/csv")
public class CSVAdapterController {

    @PostMapping("/")
    public String convert(@RequestBody String text) {
        // (TODO): Request's Validation
        return new String(csvToRDF(text.getBytes(StandardCharsets.UTF_8)));
    }

    /**
     * Upload CSV file to uploads directory
     * @param file
     * @param fileName User will have to provide this name to query data
     */
    @PostMapping("/file")
    public String singleFileUpload(@RequestParam(value="file") MultipartFile file, @RequestParam(value="fileName") String fileName) {
        // (TODO): File Validation
        try {
            // Reading file's content in bytes
            byte[] bytes = file.getBytes();
            UploadManager.saveFile(fileName, csvToRDF(bytes));
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