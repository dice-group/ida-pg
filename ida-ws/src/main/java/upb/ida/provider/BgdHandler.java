package upb.ida.provider;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.rivescript.macro.Subroutine;

import upb.ida.bean.ResponseBean;
import upb.ida.constant.IDALiteral;
import upb.ida.temp.DemoMain;
@Component
public class BgdHandler implements Subroutine {
	@Autowired
	private DemoMain DemoMain;
	@Autowired
	private ResponseBean responseBean;
	public String call (com.rivescript.RiveScript rs, String[] args) {
		
		//		String user = rs.currentUser();
		try {
			String actvTbl = (String) responseBean.getPayload().get("actvTbl");
			String actvDs = (String) responseBean.getPayload().get("actvDs");
			String actvScrId = (String) responseBean.getPayload().get("actvScrId");
			Map<String, Object> dataMap = new HashMap<String,Object>();
			//dataMap.put("label", "Bar Graph");
			String path = DemoMain.getFilePath(actvDs,actvTbl );
			/*List <String> keys = new ArrayList <String> ();
		    keys.add(args[0]);
		    //dataMap.put("Label", "BgData");
			dataMap.put("xaxisname", args[0]);
			dataMap.put("yaxisname", args[1]);
			dataMap.put("keys", keys);*/
			
			DemoMain.getJsonData(path,args[0],args[1], dataMap);
			Map<String, Object> submap_data = new HashMap<String,Object>();
			submap_data.put("bgData", dataMap);
			submap_data.put("actvScrId", actvScrId);
			responseBean.setPayload(submap_data);
			responseBean.setActnCode(IDALiteral.UIA_BG);
			return "pass";
		} catch (IOException e) {
			e.printStackTrace();
		
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "fail";
	
	}
}
