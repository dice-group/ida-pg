package upb.ida.temp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.commons.lang3.ArrayUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.CSVWriter;

public class SsdalGeoMapper {

	public static void mergeCoordinatesFile(File dnstTbl, File jsonMap, File outputFile)
			throws JsonProcessingException, FileNotFoundException, IOException {
		// read jsonmap into object
		ObjectMapper mapper = new ObjectMapper();
		ObjectReader reader = mapper.reader();
		ObjectNode jsonObj = (ObjectNode) reader.readTree(new FileInputStream(jsonMap));
		// create dir for output file
		outputFile.getParentFile().mkdirs();
		// open csv writer for output file
		CSVWriter csvWriter = new CSVWriter(new FileWriter(outputFile));
		// create csvreaderbuilder for dnstTbl
		CSVReaderBuilder rBuilder = new CSVReaderBuilder(new FileReader(dnstTbl));
		// build the reader
		CSVReader csvReader = rBuilder.build();
		// read first line
		String[] headers = csvReader.readNext();
		// add coordinate header
		String[] newHeaders = ArrayUtils.addAll(headers, "lat", "lon");
		// write the header
		csvWriter.writeNext(newHeaders);
		String lat = null;
		String lon = null;
		// start reading line by line
		for (String[] line; (line = csvReader.readNext()) != null;) {
			// extract id
			String id = line[0];
			// fetch the coordinate from json map based on id
			JsonNode node = jsonObj.get(id);
			// create and write the row
			JsonNode coordNode = node.get("coordinate");
			if (!(coordNode instanceof NullNode) && coordNode.size()>0) {
				ArrayNode coordinate = (ArrayNode) coordNode;
				lat = coordinate.get(0).asText();
				lon = coordinate.get(1).asText();
			} else {
				lat = lon = "";
			}
			line = ArrayUtils.addAll(line, lat, lon);
			csvWriter.writeNext(line);
		}
		// close the reader and writer
		csvReader.close();
		csvWriter.close();
	}

	/**
	 * Method to demonstrate example usage
	 * 
	 * @param args
	 * @throws JsonProcessingException
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static void main(String[] args) throws JsonProcessingException, FileNotFoundException, IOException {
		File dnstTbl = new File("D:\\Nikit\\ProjectGroup_WS\\Datasets\\ss-dienstalterliste\\ss-geo-dnst.csv");
		File jsonMap = new File("D:\\Nikit\\ProjectGroup_WS\\Datasets\\ss-dienstalterliste\\ss-geo-dnst-map.json");
		File outputFile = new File("D:\\Nikit\\ProjectGroup_WS\\Datasets\\ss-dienstalterliste\\ss-geo-dnst-coord.csv");
		mergeCoordinatesFile(dnstTbl, jsonMap, outputFile);
	}

}
