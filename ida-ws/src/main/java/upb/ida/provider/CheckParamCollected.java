package upb.ida.provider;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.rivescript.macro.Subroutine;

import upb.ida.bean.cluster.ClusterParam;
import upb.ida.bean.cluster.ParamEntryChecker;
import upb.ida.util.DataDumpUtil;
import upb.ida.util.SessionUtil;
/**
 * CheckParamCollected is a subroutine that checks if all parameters are collected
 * 
 * @author Faisal
 *
 */
@Component
public class CheckParamCollected implements Subroutine {
	@Autowired
	private DataDumpUtil DataDumpUtil;
	@Autowired
	private SessionUtil sessionUtil;

	@SuppressWarnings("unchecked")
	/**
	 * Method to create response for bar graph visualization
	 * @param rs
	 *            - {@link call#rs}
	 * @param args
	 *            - {@link call#args}
	 * @return - String "List of remaining parameters" or "fail"
	 */
	public String call (com.rivescript.RiveScript rs, String[] args) {
		
		try {
			/**
			 * collecting map of collected parameters from session Map 
			 */
			Map <String, String >  collected=(Map<String, String>) sessionUtil.getSessionMap().get("colledtedParams");
			/**
			 * collecting user's selected parameters map
			 */
			Map<String , Object> paramList = (Map<String, Object>) sessionUtil.getSessionMap().get("clusterParams");
			ParamEntryChecker values;
			 values= (ParamEntryChecker) paramList.get(args[0]);
			if(values.isProvided()) {
			List<String> paramsRemaining =new ArrayList<>();
			Set<String> a=collected.keySet();
			/**
			 * collecting all parameters of the user's selected algorithm 
			 */
			List<ClusterParam> mMap=  DataDumpUtil.getClusterAlgoParams(sessionUtil.getAlgoNameOrignal());
		    
			Set<String> b = paramList.keySet();
		    Set<String> result = new HashSet<>(a);
		    result.removeAll(b);
		    Set<String> temp = new HashSet<>(b);
		    temp.removeAll(a);
		    result.addAll(temp);
		    /**
			 * Extracting parameter names for which value is not collected
			 */
		    for(int w=0;w<mMap.size();w++) {	
		    	Iterator<String> unCommon=result.iterator();
				while(unCommon.hasNext()) {
					String u=unCommon.next();
					String k=mMap.get(w).getName();
					
				if(k.equals(u)) {
			            	if(mMap.get(w).getType().size() > 1 ) {
			            	String list=mMap.get(w).getType().toString();
			            	String p="<br>- "+mMap.get(w).getName() + "&nbsp; Type : " + StringUtils.removeStart(StringUtils.removeEnd(list, "}]"), "[{");
			            	paramsRemaining.add(p);
			            	}
			            	else if(mMap.get(w).getType().size() == 1 ) {
			                	String list=mMap.get(w).getType().toString();
			                	String p="<br>- "+mMap.get(w).getName() + "&nbsp; Type : " + StringUtils.removeStart(StringUtils.removeEnd(list, "]"), "[");
			                paramsRemaining.add(p);
			            	}}
		           
				}
				}
			
			return "The value has been saved.<br>"+StringUtils.removeStart(StringUtils.removeEnd(paramsRemaining.toString(), "]"), "[").replaceAll(",", "")+"<br>";
			}
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return "fail";
	
	}

	
}


