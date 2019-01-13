package upb.ida.provider;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.util.FileManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.rivescript.macro.Subroutine;
import com.rivescript.util.StringUtils;

import upb.ida.bean.ResponseBean;
import upb.ida.constant.IDALiteral;
import upb.ida.util.FileUtil;
import upb.ida.util.SessionUtil;

import static upb.ida.constant.IDALiteral.DS_PATH;

/**
 * LoadDataContent is a subroutine that loads the data
 * 
 * @author Nikit
 *
 */
@Component
public class LoadDsMetadata implements Subroutine {
	@Autowired
	private FileUtil fileUtil;
	@Autowired
	private ResponseBean responseBean;
	@Autowired
	SessionUtil sessionUtil;
	/**
	 * Method to create response for loading the data set
	 * 
	 * @param rs
	 *            - {@link call#rs}
	 * @param args
	 *            - {@link call#args}
	 * 
	 * @return String - pass or fail
	 */
	public String call(com.rivescript.RiveScript rs, String[] args) {
		String message = StringUtils.join(args, " ").trim();
		if (fileUtil.datasetExists(message)) {
			try {
				Map<String, Object> dataMap = responseBean.getPayload();
				Model model = FileManager.get().loadModel(DS_PATH + message + ".ttl");
				Map<String, Object> dsMap = new HashMap<>();
				if(dsMap.containsKey(message)){
					dsMap.put(message, model);
					sessionUtil.getSessionMap().put("DSModel", dsMap);
				}
				dataMap.put("label", message);
				dataMap.put("dsName", message);
				dataMap.put("dsMd", fileUtil.getDatasetMetaData(message));
				responseBean.setPayload(dataMap);
				responseBean.setActnCode(IDALiteral.UIA_LOADDS);
				return IDALiteral.RESP_PASS_ROUTINE;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return IDALiteral.RESP_FAIL_ROUTINE;

	}
}
