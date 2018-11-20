package upb.ida.controllers;

import no.acando.xmltordf.Builder;
import org.apache.jena.query.Dataset;
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
@RequestMapping("/adapter/xml")
public class XMLAdapterController {

    @PostMapping("/")
    public String convert(@RequestBody String text) throws IOException, SAXException, ParserConfigurationException {
        // (TODO): Request's Validation
        byte bytes[] = text.getBytes(StandardCharsets.UTF_8);
        return new String(xmlToRDF(bytes));
    }

    @PostMapping("/file")
    public String singleFileUpload(@RequestParam(value="file") MultipartFile file) {
        // (TODO): Request's Validation

        try {
            // Reading file's content in bytes
            byte[] bytes = file.getBytes();
            Path path = Paths.get(System.getProperty("user.dir") + "/uploads/", file.getOriginalFilename() + ".ttl");
            Files.write(path, xmlToRDF(bytes));
        } catch (IOException | SAXException | ParserConfigurationException e) {
            e.printStackTrace();
        }

        // (TODO) We need proper Response bean
        return "done";
    }

    private byte[] xmlToRDF(byte[] bytes) throws IOException, SAXException, ParserConfigurationException {
        InputStream inputStream = new ByteArrayInputStream(bytes);

        BufferedInputStream in = new BufferedInputStream(inputStream);
        Dataset dataset = Builder.getAdvancedBuilderJena().build().convertToDataset(in);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        //  converting it into turtle format
        dataset.getDefaultModel().write(stream, "ttl");

        return stream.toByteArray();
    }
}