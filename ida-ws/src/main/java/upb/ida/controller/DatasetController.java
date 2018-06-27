package upb.ida.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import upb.ida.bean.ResponseBean;
import upb.ida.constant.IDALiteral;
import upb.ida.temp.DemoMain;


@Component
public class DatasetController {
		@Autowired
		private ResponseBean response;
		@Autowired
		private DemoMain dem;
		
		public ResponseBean selectedFile(String fileName, String x, String y) throws Exception {
			if(fileName.matches("[iI]nput")) {
				response.setChatmsg("Input Dataset converted to Json for Visualisation and clustering");
				Map<String, Object> dataMap =   new HashMap<String, Object>();
				dataMap.put("x_axis", x);
				dataMap.put("y_axis", y);
				dataMap.put("dataset", dem.getJsonData(fileName,x,y));
				response.setPayload(dataMap);
				response.setActnCode(IDALiteral.UIA_BG);
			}
			
			return response;
		}
		
		
	
	

}
