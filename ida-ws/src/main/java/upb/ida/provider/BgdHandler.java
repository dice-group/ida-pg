package upb.ida.provider;

import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.rivescript.macro.Subroutine;

import upb.ida.bean.FilterOption;
import upb.ida.bean.ResponseBean;
import upb.ida.constant.IDALiteral;
import upb.ida.temp.DemoMain;

@Component
public class BgdHandler implements Subroutine {
	
	public static final String FIRST_N_REC = "FIRSTN";
	public static final String LAST_N_REC = "LASTN";
	public static final String TOP_N_REC = "TOPN";
	public static final String FROM_TO_REC = "FROMTO";
	
	@Autowired
	private DemoMain DemoMain;
	@Autowired
	private ResponseBean responseBean;

	public String call(com.rivescript.RiveScript rs, String[] args) {

		try {
			// Fetch active dataset details
			String actvTbl = (String) responseBean.getPayload().get("actvTbl");
			String actvDs = (String) responseBean.getPayload().get("actvDs");
			Map<String, Object> dataMap = new HashMap<String, Object>();
			String path = DemoMain.getFilePath(actvDs, actvTbl);
			String xaxisname = args[0];
			String yaxisname = args[1];
			/*String filterType = args[3];
			FilterOption filterOption = getFilterOption(filterType);*/
			// set the bargraph in dataMap
			DemoMain.getJsonData(path, xaxisname, yaxisname, dataMap);
			Map<String, Object> submap_data = responseBean.getPayload();
			submap_data.put("bgData", dataMap);
			responseBean.setActnCode(IDALiteral.UIA_BG);
			return "pass";
		} catch (IOException e) {
			e.printStackTrace();

		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return "fail";

	}
	
	private FilterOption getFilterOption(String filterType) {
		FilterOption filterOption = null;
		if(filterType.equalsIgnoreCase(FIRST_N_REC)) {
			//Get Filter Option for First N records
		} else if(filterType.equalsIgnoreCase(LAST_N_REC)) {
			//Get Filter Option for Last N records
		} else if(filterType.equalsIgnoreCase(TOP_N_REC)) {
			//Get Filter Option for Top N records
		} else if(filterType.equalsIgnoreCase(FROM_TO_REC)) {
			//Get Filter Option for particular sequence of records
		}
		return filterOption;
	}
}
