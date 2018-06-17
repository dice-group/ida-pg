package upb.ida.controller;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import upb.ida.bean.ResponseBean;
import upb.ida.temp.DemoMain;


@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/csv2json")
public class DatasetController {
		@Autowired
		ResponseBean response;
		@Autowired
		DemoMain dem;
		
		@RequestMapping("/selectedFile")
		public ResponseBean selectedFile(@RequestParam(value = "msg") String msg,@RequestParam(value = "x_axis") String x,@RequestParam(value = "y_axis") String y) throws Exception {
			if(msg.matches("[iI]nput")) {
				response.setChatmsg("Input Dataset converted to Json for Visualisation and clustering");
				Map<String, Object> dataMap =   new HashMap<String, Object>();
				dataMap.put("x_axis", x);
				dataMap.put("y_axis", y);
				dataMap.put("dataset", dem.getJsonData(msg,x,y));
				Map<String, Object> data =   new HashMap<String, Object>();
				System.out.println(dataMap);
						data=dataMap;
				response.setPayload(dataMap);
				response.setActnCode(1);
			}
			
			return response;
		}
	
	

}
