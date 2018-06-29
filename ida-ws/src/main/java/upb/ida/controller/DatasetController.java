package upb.ida.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import upb.ida.bean.ResponseBean;
import upb.ida.constant.IDALiteral;
import upb.ida.temp.DemoMain;


@Component
public class DatasetController {
		@Autowired
		private ResponseBean response;
		@Autowired
		private DemoMain dem;

		public ResponseBean selectedFile(String filePath, String x, String y) throws Exception {
				response.setChatmsg("Input Dataset converted to Json for Visualisation and clustering");
				Map<String, Object> dataMap =   new HashMap<String, Object>();
			    List <String> keys = new ArrayList <String> ();
			    keys.add(x);
			    dataMap.put("Label", "BgData");
				//dataMap.put("actvScrId", actvScrId);
			    dataMap.put("xaxisname", x);
				dataMap.put("yaxisname", y);
				dataMap.put("keys", keys);
				dataMap.put("dataset", dem.getJsonData(filePath,x,y));
				response.setPayload(dataMap);
				response.setActnCode(IDALiteral.UIA_BG);
			
			return response;
		}
		
		
	
	

}
