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
public class ParamsHandler implements Subroutine {
	
	private ResponseBean responseBean;
	@Autowired
	private DataDumpUtil DataDumpUtil;
	public String call (com.rivescript.RiveScript rs, String[] args) {

		
		//		String user = rs.currentUser();
		try {
//			String actvTbl = (String) responseBean.getPayload().get("actvTbl");
//			String actvDs = (String) responseBean.getPayload().get("actvDs");
//			String actvScrId = (String) responseBean.getPayload().get("actvScrId");
            List<ClusterParam> paramList=  new ArrayList<>();
            paramList=DataDumpUtil.getClusterAlgoParams(args[0]);
            int x=0;
            System.out.println(paramList.size());
            String algoStr;
            algoStr="<br>"+paramList.get(0).getName();
            for (int i = 0; i < paramList.size()-1; i++) {
            	
            	algoStr=algoStr+"<br>"+paramList.get(i+1).getName();
            	
            
            }
          
			return algoStr;
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "fail";
	
	}
}


