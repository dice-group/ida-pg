package upb.ida.provider;

import java.io.IOException;
import java.text.ParseException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.rivescript.macro.Subroutine;

import upb.ida.bean.ResponseBean;
import upb.ida.constant.IDALiteral;
import upb.ida.fdg.FDG_Util;
import upb.ida.temp.DemoMain;
@Component
public class FdgHandler implements Subroutine {
	@Autowired
	DemoMain DemoMain;
	@Autowired
	FDG_Util FDG_Util;
	@Autowired
	ResponseBean responseBean;
	public String call (com.rivescript.RiveScript rs, String[] args) {
		
		//		String user = rs.currentUser();
		try {
			String actvTbl = (String) responseBean.getPayload().get("actvTbl");
			String actvDs = (String) responseBean.getPayload().get("actvDs");
			String path = DemoMain.getFilePath(actvDs,actvTbl );
			Map<String, Object> dataMap = responseBean.getPayload();
			dataMap.put("label", "Fdg Handler");
//			dataMap.put("dsName", message);
			dataMap.put("dataset", FDG_Util.generateFDG(path,args[0].toLowerCase(),args[1],args[2]));
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
