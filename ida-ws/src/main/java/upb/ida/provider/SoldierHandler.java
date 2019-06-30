package upb.ida.provider;

import java.io.IOException;
/**
 * SoldierHandler is a subroutine that is used to create response for UI
 * of Force Directed Graph by taking inputs from user through rivescript  .
 * 
 * @author Deepak
 *
 */
import java.text.ParseException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.rivescript.macro.Subroutine;

import upb.ida.bean.ResponseBean;
import upb.ida.constant.IDALiteral;
import upb.ida.fdg.FDG_Util;
import upb.ida.util.FileUtil;
@Component
public class SoldierHandler implements Subroutine {
	@Autowired
	private FileUtil DemoMain;
	@Autowired
	private FDG_Util FDG_Util;
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
		
		 String dbhost = System.getenv("FUSEKI_URL");
		 String datasetName = "/user";	
		 String dbUrl = dbhost + datasetName;
		//		String user = rs.currentUser();
		try {
			String actvTbl = (String) responseBean.getPayload().get("actvTbl");
			String actvDs = (String) responseBean.getPayload().get("actvDs");
			//String actvScrId = (String) responseBean.getPayload().get("actvScrid");
			String path = DemoMain.getDTFilePath(actvDs,actvTbl );
			Map<String, Object> dataMap = responseBean.getPayload();
			dataMap.put("label", "Fdg Data");
			/**
			 * function call takes file path and arguments as 
			 * input to get data for force directed graph  
			 */
			//System.out.println("actvScrId...gd"+actvTbl);
			System.out.println("actvTbl....SH...."+actvTbl);
			System.out.println("actvDs...SH..."+actvDs);
			System.out.println("args...SH..."+args[1]);
			System.out.println("rs...SH..."+rs);
			System.out.println("path...SH..."+path);
			
			
			
			
			///dataMap.put("fdgData", FDG_Util.generateFDG(path,args[0].toLowerCase(),args[1],args[2]));
			//System.out.println("dataMap....SH..."+dataMap);
			//dataMap.put("actvScrId", actvScrId);
		//	responseBean.setPayload(args);
			responseBean.setActnCode(IDALiteral.UIA_FDG);
			return "pass";
		} finally {
			System.out.println("in finally...");
		}
//		} catch (ParseException e) {
//			
//			e.printStackTrace();
//		}
	//	return "fail";	
	}
}
