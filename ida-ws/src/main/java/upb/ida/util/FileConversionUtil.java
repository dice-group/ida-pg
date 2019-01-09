package upb.ida.util;

import no.acando.xmltordf.Builder;
import org.apache.jena.query.Dataset;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.*;

public class FileConversionUtil {
    public static byte[] xmlToRDF(byte[] bytes) throws IOException, SAXException, ParserConfigurationException {
        InputStream inputStream = new ByteArrayInputStream(bytes);

        BufferedInputStream in = new BufferedInputStream(inputStream);
        Dataset dataset = Builder.getAdvancedBuilderJena().build().convertToDataset(in);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        //  converting it into turtle format
        dataset.getDefaultModel().write(stream, "ttl");

        return stream.toByteArray();
    }

    public static byte[] csvToRDF(byte[] bytes) {
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
