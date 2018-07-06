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
import upb.ida.util.DataDumpUtil;
import upb.ida.bean.cluster.ClusterParam;

@Component
public class UserParamEntry implements Subroutine {
	
	private ResponseBean responseBean;
	@Autowired
	private DataDumpUtil DataDumpUtil;
	public String call (com.rivescript.RiveScript rs, String[] args) {

		
		//		String user = rs.currentUser();
		try {
 
			
          
			return "";
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return "fail";
	
	}
}


