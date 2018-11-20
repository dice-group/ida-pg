package upb.ida.controllers;

import no.acando.xmltordf.Builder;
import org.apache.jena.query.Dataset;
import org.xml.sax.SAXException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.nio.charset.StandardCharsets;

@RestController
public class XMLAdapterController {

    @PostMapping("/adapter/xml")
    public String convert(@RequestBody String text) throws IOException, SAXException, ParserConfigurationException {
        InputStream inputStream = new ByteArrayInputStream(text.getBytes(StandardCharsets.UTF_8));

        BufferedInputStream in = new BufferedInputStream(inputStream);
        Dataset dataset = Builder.getAdvancedBuilderJena().build().convertToDataset(in);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        //  converting it into turtle format
        dataset.getDefaultModel().write(stream, "ttl");

        return new String(stream.toByteArray());
    }
}