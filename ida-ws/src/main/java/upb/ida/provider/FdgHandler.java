package upb.ida.provider;

/**
 * fdgHandler is a subroutine that is used to create response for UI
 * of Force Directed Graph by taking inputs from user through rivescript  .
 *
 * @author Faisal
 *
 */
import java.io.IOException;
import java.text.ParseException;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.rivescript.macro.Subroutine;
import upb.ida.bean.ResponseBean;
import upb.ida.constant.IDALiteral;
import upb.ida.fdg.FdgUtil;
import upb.ida.util.FileUtil;

@Component
public class FdgHandler implements Subroutine {
	@Autowired
	private FileUtil DemoMain;
	@Autowired
	private FdgUtil FDG_Util;
	@Autowired
	private ResponseBean responseBean;

	/**
	 *Method to create response for Force Directed Graph visualization
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
			Map<String, Object> dataMap = responseBean.getPayload();
			dataMap.put("label", "Fdg Data");
			/**
			 * function call takes file path and arguments as
			 * input to get data for force directed graph
			 */
			dataMap.put("fdgData", FDG_Util.generateFDG(actvTbl,args[0].toLowerCase(),args[1],args[2], actvDs));
			responseBean.setPayload(dataMap);
			responseBean.setActnCode(IDALiteral.UIA_FDG);
			return "pass";
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "fail";

	}
}
