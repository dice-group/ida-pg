package upb.ida.rest;

import java.io.*;
import java.nio.file.attribute.AclEntry;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.deser.BuilderBasedDeserializer;
import no.acando.xmltordf.Builder;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

//import org.springframework.web.method.annotation.ModelFactory;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.SAXException;
import upb.ida.bean.ResponseBean;
import org.apache.jena.query.Dataset;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import upb.ida.service.DataService;
import upb.ida.service.RiveScriptService;
import upb.ida.util.UploadManager;
import upb.ida.util.FileConversionUtil;

import javax.xml.crypto.Data;
import javax.xml.parsers.ParserConfigurationException;

import static upb.ida.util.FileConversionUtil.csvToRDF;

/**
 * Exposes RESTful RPCs for the IDA Chatbot
 * 
 * @author Nikit
 *
 */
@RestController
@CrossOrigin(origins = "*", allowCredentials = "true")
@RequestMapping("/message")
public class MessageRestController {
	@Autowired
	private ResponseBean response;
	@Autowired
	private RiveScriptService rsService;
	@Autowired 
	private DataService dataService;
	@RequestMapping("/sayhello")
	public String greeting(@RequestParam(value = "name", defaultValue = "World") String name) {
		return "Hello " + name + "!";
	}
	/**
	 * Method to accept queries for the chatbot
	 * @param msg - query in natural language
	 * @param actvScrId - id of the active screen
	 * @param actvTbl - name of the active data table on screen
	 * @param actvDs - name of the active dataset on screen
	 * @return - instance of {@link ResponseBean}
	 * @throws Exception
	 */
	@RequestMapping("/sendmessage")
	public ResponseBean sendmessage(@RequestParam(value = "msg") String msg,
			@RequestParam(value = "actvScrId") String actvScrId, @RequestParam(value = "actvTbl") String actvTbl,
			@RequestParam(value = "actvDs") String actvDs) throws Exception {

		Map<String, Object> dataMap = new HashMap<>();
		dataMap.put("actvScrId", actvScrId);
		dataMap.put("actvTbl", actvTbl);
		dataMap.put("actvDs", actvDs);
		response.setPayload(dataMap);
		String reply = rsService.getRSResponse(msg);

		response.setChatmsg(reply);
		return response;
	}
	
	@RequestMapping("/getdatatable")
	public ResponseBean getDataTable(@RequestParam(value = "actvScrId") String actvScrId, @RequestParam(value = "actvTbl") String actvTbl,
			@RequestParam(value = "actvDs") String actvDs) throws Exception {

		Map<String, Object> dataMap = new HashMap<>();
		dataMap.put("actvScrId", actvScrId);
		dataMap.put("actvTbl", actvTbl);
		dataMap.put("actvDs", actvDs);
		response.setPayload(dataMap);
		dataService.getDataTable(actvDs, actvTbl);
		return response;
	}

	@PostMapping("/file")
	public String convert(@RequestParam(value="file") MultipartFile file, @RequestParam(value="fileName") String fileName) throws IOException, Exception{
		String status;

		if(file.getContentType().equals("text/xml") && UploadManager.getFileExtension(file.getOriginalFilename()).equals("xml")){
			byte[] bytes = file.getBytes();
			UploadManager.saveFile(fileName, FileConversionUtil.xmlToRDF(bytes));
			status = "pass";
		}
		else if(file.getContentType().equals("text/csv") && UploadManager.getFileExtension(file.getOriginalFilename()).equals("csv")){
			byte[] bytes = file.getBytes();
			UploadManager.saveFile(fileName, FileConversionUtil.csvToRDF(bytes));
			status = "pass";
		}
		else
			status = "fail";

		return status;
	}

}