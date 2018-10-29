package upb.ida.service;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;

import upb.ida.bean.ResponseBean;
import upb.ida.constant.IDALiteral;
import upb.ida.util.FileUtil;

@Service
public class DataService {
	@Autowired
	private ResponseBean responseBean;
	@Autowired
	private FileUtil fileUtil;
	public void getDataTable(String actvDs, String actvTbl) throws JsonProcessingException, IOException {
		// Fetch file path
		String path = fileUtil.getDTFilePath(actvDs, actvTbl);
		// Create file object
		//TODO: Remove this redundant path fetching
		File file = new File(fileUtil.fetchSysFilePath(path));
		// Fetch file content
		List<Map<String, String>> dataTable = fileUtil.convertToMap(file);
		// Set it in payload
		responseBean.getPayload().put("dataTable", dataTable);
		// Set action code
		responseBean.setActnCode(IDALiteral.UIA_DTTABLE);
		// Set Reply
		responseBean.setChatmsg("Requested Table added to the main view.");
	}

}
