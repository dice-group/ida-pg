package upb.ida.provider;


import java.util.ArrayList;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.rivescript.macro.Subroutine;

import upb.ida.util.DataDumpUtil;
/**
 * ClusterConHandler is a subroutine that fetches 
 * all available clustering algorithms from scikit datadump.
 * 
 * @author Faisal
 *
 */
@Component
public class ClusterConHandler implements Subroutine {
	
	@Autowired
	private DataDumpUtil DataDumpUtil;
	
	/**
	 * Method to get algorithms for clustering. 
	 * @param rs
	 *            - {@link call#rs}
	 * @param args
	 *            - {@link call#args}
	 * @return - String "all algorithms for clustering" or "fail"
	 */
	public String call (com.rivescript.RiveScript rs, String[] args) {
		
		try {

            List<String> algoList=  new ArrayList<>();
            algoList=DataDumpUtil.getClusteringAlgoNames();
            String algoStr;
            algoStr= "<br> - "+algoList.get(0);

            for (int i = 0; i < algoList.size()-1; i++) {
    			
            	algoStr=algoStr+"<br> - "+algoList.get(i+1);
    		}
 
			return "<br> [ Hint ] "+algoStr;
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "fail";
	
	}
}

