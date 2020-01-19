package upb.ida.provider;

import com.rivescript.macro.Subroutine;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import upb.ida.bean.ResponseBean;
import upb.ida.constant.IDALiteral;
import upb.ida.dao.DataRepository;

import java.util.Map;

@Component
public class OntologyExplorerHandler implements Subroutine {
	@Autowired
	private ResponseBean responseBean;

	public String call(com.rivescript.RiveScript rs, String[] args) {

		Map<String, Object> dataMap = responseBean.getPayload();
		String datasetName = (String) dataMap.get("actvDs");

		System.out.println("\n\nDataset name:\t" + datasetName);
		System.out.println("\n\nDataset is empty?\t" + datasetName.isEmpty());

		DataRepository dataRepository = new DataRepository(false);
		responseBean.setActnCode(IDALiteral.UIA_ONTOLOGY);
		try {
			JSONObject ontologyData = dataRepository.getOntologyData(datasetName);
			if (ontologyData != null) {
				dataMap.put("ontologyData", ontologyData);
				responseBean.setPayload(dataMap);
				return IDALiteral.RESP_PASS_ROUTINE;
			} else {
				responseBean.setActnCode(IDALiteral.UIA_NOACTION);
				responseBean.setPayload(null);
				return IDALiteral.RESP_FAIL_ROUTINE;
			}
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
		}
		return IDALiteral.RESP_FAIL_ROUTINE;
	}
}
