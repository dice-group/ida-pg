package upb.ida.provider;


import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.rivescript.macro.Subroutine;

import upb.ida.util.DataDumpUtil;
@Component
public class ClusterConHandler implements Subroutine {
	
	@Autowired
	private DataDumpUtil DataDumpUtil;
	public String call (com.rivescript.RiveScript rs, String[] args) {

		
		//		String user = rs.currentUser();
		try {
//			String actvTbl = (String) responseBean.getPayload().get("actvTbl");
//			String actvDs = (String) responseBean.getPayload().get("actvDs");
//			String actvScrId = (String) responseBean.getPayload().get("actvScrId");
            List<String> algoList=  new ArrayList<>();
            algoList=DataDumpUtil.getClusteringAlgoNames();
            String algoStr= "<br>"+algoList.get(0);
            for (int i = 0; i < algoList.size()-1; i++) {
            	
    			algoStr=algoStr+"<br>"+algoList.get(i+1);
    		}
            
            
			//			Map<String, Object> dataMap = new HashMap<String,Object>();
//			//dataMap.put("label", "Bar Graph");
//			//String path = DemoMain.getFilePath(actvDs,actvTbl );
//			List <String> keys = new ArrayList <String> ();
//		    keys.add(args[0]);
//		    
//			//dataMap.put("baritems", DemoMain.getJsonData(path,args[0],args[1]));
//			Map<String, Object> submap_data = new HashMap<String,Object>();
//			submap_data.put("bgData", dataMap);
//			submap_data.put("actvScrId", actvScrId);
//			responseBean.setPayload(submap_data);
//			responseBean.setActnCode(IDALiteral.UIA_BG);
			return algoStr;
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "fail";
	
	}
}

