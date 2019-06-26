package upb.ida.service;

import java.io.File;
import java.io.IOException;
import java.util.*;

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

	public void getDataTableOfSpecificColumns(String actvDs, String actvTbl, List<String> tableColumns) throws JsonProcessingException, IOException {

		String path = fileUtil.getDTFilePath(actvDs, actvTbl);
		if (path == null)
			path = String.format("dataset/%s/%s", actvDs, actvTbl);
		File file = new File(fileUtil.fetchSysFilePath(path));
		List<Map<String, String>> dataTable = fileUtil.convertToMap(file);
		for(Map<String, String> item : dataTable)
		{
			Iterator<Map.Entry<String, String>> entryIt = item.entrySet().iterator();
			while (entryIt.hasNext()) {
				Map.Entry<String, String> entry = entryIt.next();
				String keyVal = entry.getKey().toLowerCase();
				if (!tableColumns.contains(keyVal)) {
					entryIt.remove();
				}
			}
		}
		responseBean.getPayload().put("dataTable", dataTable);
		// Set action code
		responseBean.setActnCode(IDALiteral.UIA_GS);
		// Set Reply
		responseBean.setChatmsg("Requested Table added to the main view.");
	}

}
