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
@Component
public class FdgHandler implements Subroutine {
	@Autowired
	FDG_Util FDG_Util;
	@Autowired
	ResponseBean responseBean;
	public String call (com.rivescript.RiveScript rs, String[] args) {
		
		//		String user = rs.currentUser();
		try {
			Map<String, Object> dataMap = responseBean.getPayload();
			dataMap.put("label", "Fdg Handler");
//			dataMap.put("dsName", message);
			dataMap.put("dataset", FDG_Util.generateFDG("city//citydistance.csv",args[0].toLowerCase(),args[1].toLowerCase(),args[2].toLowerCase()));
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
