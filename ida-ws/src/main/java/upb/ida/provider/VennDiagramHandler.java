package upb.ida.provider;

import java.io.IOException;
/**
 * fdgHandler is a subroutine that is used to create response for UI
 * of Force Directed Graph by taking inputs from user through rivescript  .
 * 
 * @author Maqbool
 *
 */
import java.text.ParseException;
import java.util.Map;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.rivescript.macro.Subroutine;

import upb.ida.bean.ResponseBean;
import upb.ida.constant.IDALiteral;
import upb.ida.fdg.FDG_Util;
import upb.ida.util.FileUtil;
@Component
public class VennDiagramHandler implements Subroutine {
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
		
		//		String user = rs.currentUser();
        String actvTbl = (String) responseBean.getPayload().get("actvTbl");
        String actvDs = (String) responseBean.getPayload().get("actvDs");

        System.out.println(Arrays.toString(args));
        System.out.println(actvTbl + " , " + actvDs);

        String path = DemoMain.getDTFilePath(actvDs, actvTbl);

        System.out.println(Arrays.toString(args));
        System.out.println(actvTbl + " , " + actvDs);
        System.out.println(path);
		try {
			String actvTbl = (String) responseBean.getPayload().get("actvTbl");
			String actvDs = (String) responseBean.getPayload().get("actvDs");
			//String actvScrId = (String) responseBean.getPayload().get("actvScrid");
			String path = DemoMain.getDTFilePath(actvDs,actvTbl );
			/**
			 * function call takes file path and arguments as
			 * input to get data for force directed graph
			 */
			dataMap.put("fdgData", FDG_Util.generateFDG(path,args[0].toLowerCase(),args[1],args[2]));

            Map<String, Object> dataMap = responseBean.getPayload();
            dataMap.put("label", "Fdg Data");
			//dataMap.put("actvScrId", actvScrId);
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
