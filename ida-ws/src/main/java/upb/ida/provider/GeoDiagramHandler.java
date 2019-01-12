package upb.ida.provider;

import com.rivescript.macro.Subroutine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import upb.ida.bean.ResponseBean;
import upb.ida.constant.IDALiteral;
import upb.ida.util.FileUtil;

import java.util.Map;

/**
 * GeoDiagramHandler is a subroutine that is used to generate data for
 * Venn diagram .
 *
 * @author Maqbool
 */

@Component
public class GeoDiagramHandler implements Subroutine {
	@Autowired
	private FileUtil DemoMain;
	@Autowired
	private ResponseBean responseBean;
	
	/**
	 * Method to create response for Geo Spatial Diagram visualization
	 * @param rs
	 *            - {@link call#rs}
	 * @param args
	 *            - {@link call#args}
	 * @return  String - pass or fail
	 */
	
	public String call (com.rivescript.RiveScript rs, String[] args) {

		String actvTbl = (String) responseBean.getPayload().get("actvTbl");
		String actvDs = (String) responseBean.getPayload().get("actvDs");
//		String path = DemoMain.getDTFilePath(actvDs, actvTbl);
		Map<String, Object> dataMap = responseBean.getPayload();
//
//		dataMap.put("gsDiagramData", VENN_Util.generateVennDiagram(path, args));
		dataMap.put("label", "geo spatial diagram data");
		responseBean.setPayload(dataMap);
		responseBean.setActnCode(IDALiteral.UIA_GSDIAGRAM);
		return "pass";
	}
}
