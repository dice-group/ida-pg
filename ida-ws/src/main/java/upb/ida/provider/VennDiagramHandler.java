package upb.ida.provider;

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
import upb.ida.venndiagram.VennUtil;
import upb.ida.util.FileUtil;
@Component
public class VennDiagramHandler implements Subroutine {
	@Autowired
	private FileUtil DemoMain;
	@Autowired
	private VennUtil vennUtil;
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

			dataMap.put("vennDiagramData", vennUtil.generateVennDiagram(actvTbl, args, actvDs));
            dataMap.put("label", "venn diagram data");
			responseBean.setPayload(dataMap);
			responseBean.setActnCode(IDALiteral.UIA_VENNDIAGRAM);
			return IDALiteral.RESP_PASS_ROUTINE;
		} catch (NumberFormatException e) {
			e.printStackTrace();
			responseBean.setChatmsg(e.getMessage());
			responseBean.setErrCode(1);
		}
		return IDALiteral.RESP_FAIL_ROUTINE;
	}
}
