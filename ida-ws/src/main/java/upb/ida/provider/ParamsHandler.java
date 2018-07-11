package upb.ida.provider;


import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.rivescript.macro.Subroutine;

import upb.ida.bean.cluster.ClusterParam;
import upb.ida.util.DataDumpUtil;

@Component
public class ParamsHandler implements Subroutine {
	
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
            // int x=0;
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


