package upb.ida.provider;


import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.rivescript.macro.Subroutine;

import upb.ida.util.DataDumpUtil;
@Component
public class ClusterConHandler implements Subroutine {
	
	@Autowired
	private DataDumpUtil DataDumpUtil;
	public String call (com.rivescript.RiveScript rs, String[] args) {
		
		try {

            List<String> algoList=  new ArrayList<>();
            algoList=DataDumpUtil.getClusteringAlgoNames();
            String algoStr=new String();
            algoStr= "<br>"+algoList.get(0);
            for (int i = 0; i < algoList.size()-1; i++) {
    			
            	algoStr=algoStr+"<br>"+algoList.get(i+1);
    		}
 
			return algoStr;
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "fail";
	
	}
}

