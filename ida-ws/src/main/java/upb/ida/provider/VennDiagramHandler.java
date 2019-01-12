package upb.ida.provider;

import java.io.IOException;
/**
 * vennDiagramHandler is a subroutine that is used to generate data for
 * Venn diagram .
 * 
 * @author Maqbool
 *
 */
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.rivescript.macro.Subroutine;

import upb.ida.bean.ResponseBean;
import upb.ida.constant.IDALiteral;
import upb.ida.venndiagram.VENN_Util;
import upb.ida.util.FileUtil;
@Component
public class VennDiagramHandler implements Subroutine {
	@Autowired
	private FileUtil DemoMain;
	@Autowired
	private VENN_Util VENN_Util;
	@Autowired
	private ResponseBean responseBean;
	
	/**
	 * Method to create response for Venn Diagram visualization
	 * @param rs
	 *            - {@link call#rs}
	 * @param args
	 *            - {@link call#args}
	 * @return  String - pass or fail
	 */
	
	public String call (com.rivescript.RiveScript rs, String[] args) {
		try {
			String actvTbl = (String) responseBean.getPayload().get("actvTbl");
			String actvDs = (String) responseBean.getPayload().get("actvDs");
			String path = DemoMain.getDTFilePath(actvDs, actvTbl);
			Map<String, Object> dataMap = responseBean.getPayload();
			
			dataMap.put("vennDiagramData", VENN_Util.generateVennDiagram(path, args));
            dataMap.put("label", "venn diagram data");
			responseBean.setPayload(dataMap);
			responseBean.setActnCode(IDALiteral.UIA_VENNDIAGRAM);
			return "pass";
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "fail";
	}
}
