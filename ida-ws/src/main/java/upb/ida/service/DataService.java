package upb.ida.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import upb.ida.bean.ResponseBean;
import upb.ida.constant.IDALiteral;
import upb.ida.dao.DataRepository;

@Service
public class DataService {
	@Autowired
	private ResponseBean responseBean;

	public void getDataTable(String actvDs, String actvTbl) throws Exception {
		DataRepository dataRepository = new DataRepository(actvDs, false);
		responseBean.getPayload().put("dataTable", dataRepository.getData(actvTbl, actvDs));
		// Set action code
		responseBean.setActnCode(IDALiteral.UIA_DTTABLE);
		// Set Reply
		responseBean.setChatmsg("Requested Table added to the main view.");
	}

}
