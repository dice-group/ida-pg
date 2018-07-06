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
 
			List<ClusterParam> paramList=  new ArrayList<>();
            paramList=DataDumpUtil.getClusterAlgoParams(args[0]);
            int x=0;
            String algoStr = null;
            if(paramList.get(0).isOptional()==false) {

            algoStr="<br>"+paramList.get(0).getName();
            }
            else
            {
            	algoStr="<br>"+paramList.get(0).getName()+"[Optional]";
            }
            
            for (int i = 0; i < paramList.size()-1; i++) {
            	if(paramList.get(i+1).isOptional()) {
            	algoStr=algoStr+"<br>"+paramList.get(i+1).getName()+"[Optional]";
            	}
            	else {
            		algoStr=algoStr+"<br>"+paramList.get(i+1).getName();
            		
            	}
            }
          
			return algoStr;
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return "fail";
	
	}
}


