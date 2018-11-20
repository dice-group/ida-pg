package upb.ida.controllers;

import no.acando.xmltordf.Builder;
import org.apache.jena.query.Dataset;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.SAXException;
import upb.ida.utils.UploadManager;

import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/adapter/xml")
public class XMLAdapterController {

    @PostMapping("/")
    public String convert(@RequestBody String text) throws IOException, SAXException, ParserConfigurationException {
        // (TODO): Request's Validation
        return new String(xmlToRDF(text.getBytes(StandardCharsets.UTF_8)));
    }

    /**
     * Upload XML file to uploads directory
     * @param file
     * @param fileName User will have to provide this name to query data
     */
    @PostMapping("/file")
    public String singleFileUpload(@RequestParam(value="file") MultipartFile file, @RequestParam(value="fileName") String fileName) {
        // (TODO): File Validation
        try {
            // Reading file's content in bytes
            byte[] bytes = file.getBytes();
            UploadManager.saveFile(fileName, xmlToRDF(bytes));
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