package upb.ida.rest;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

import org.apache.jena.query.*;
import org.apache.jena.util.FileManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.multipart.MultipartFile;
import upb.ida.bean.ResponseBean;
import org.apache.jena.rdf.model.Model;
import upb.ida.service.DataService;
import upb.ida.service.RiveScriptService;
import upb.ida.util.SessionUtil;
import upb.ida.util.UploadManager;
import upb.ida.util.FileConversionUtil;

import static upb.ida.constant.IDALiteral.DS_PATH;

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
	@Autowired
	private SessionUtil sessionUtil;
	
	private int id = 0;

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
	public ResponseBean convert(@RequestParam(value="file") MultipartFile file, @RequestParam(value="fileName") String fileName) throws IOException, Exception{
//		TODO: ID unique implementation required

		if(file.getContentType().equals("text/xml") && UploadManager.getFileExtension(file.getOriginalFilename()).equals("xml")){
			byte[] bytes = file.getBytes();
			UploadManager.saveFile(fileName.toLowerCase(), FileConversionUtil.xmlToRDF(bytes));
		}
		else if(file.getContentType().equals("text/csv") && UploadManager.getFileExtension(file.getOriginalFilename()).equals("csv")){
			byte[] bytes = file.getBytes();
			UploadManager.saveFile(fileName.toLowerCase(), FileConversionUtil.csvToRDF(bytes));
		}
//		Map<String, Object> fileMap = new HashMap<>();
		Map<Integer, String> idMap = new HashMap<>();
		idMap.put(id, DS_PATH + fileName + ".ttl");
//		fileMap.put("FileMap", idMap);
		id++;
//		response.setPayload(fileMap);
		sessionUtil.getSessionMap().put("FileMap", idMap);
		response.setChatmsg("Please download the file at: <a href=" + DS_PATH + fileName + ".ttl" + ">" + fileName + "</a>");
		return response;
	}

	@GetMapping(value = "/sparql")
	public String getbyArtist(@RequestParam("query") String queryString, @RequestParam("datasetName") String datasetName) {
		// TODO: Error handling e.g. if dataset name is invalid

		Model model = FileManager.get().loadModel(DS_PATH + datasetName + ".ttl");
		Query query = QueryFactory.create(queryString);
		QueryExecution qexec = QueryExecutionFactory.create(query, model);

		ResultSet results = qexec.execSelect();

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		ResultSetFormatter.outputAsJSON(outputStream, results);
		String json = new String(outputStream.toByteArray());

		return json;
	}

}
