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
import upb.ida.util.DataDumpUtil;
import upb.ida.util.SessionUtil;

@Component
public class CheckParamCollected implements Subroutine {
	@Autowired
	private DataDumpUtil DataDumpUtil;
	@Autowired
	private SessionUtil sessionUtil;
	
	@SuppressWarnings("unchecked")
	public String call (com.rivescript.RiveScript rs, String[] args) {
		try {
			
			Map <String, String >  collected=(Map<String, String>) sessionUtil.getSessionMap().get("colledtedParams");
			Map<String , Object> paramList = (Map<String, Object>) sessionUtil.getSessionMap().get("clusterParams");
			List<String> paramsRemaining =new ArrayList<>();
			Set<String> a=collected.keySet();
			List<ClusterParam> mMap=  DataDumpUtil.getClusterAlgoParams(sessionUtil.getAlgoNameOrignal());
		    Set<String> b = paramList.keySet();
		    Set<String> result = new HashSet<>(a);
		    result.removeAll(b);
		    Set<String> temp = new HashSet<>(b);
		    temp.removeAll(a);
		    result.addAll(temp);
		    for(int w=0;w<mMap.size();w++) {	
		    	Iterator<String> unCommon=result.iterator();
				while(unCommon.hasNext()) {
					String u=unCommon.next();
					String k=mMap.get(w).getName();
					
				if(k.equals(u)) {
			            	if(mMap.get(w).getType().size() > 1 ) {
			            	String list=mMap.get(w).getType().toString();
			            	String p="<br>"+mMap.get(w).getName() + "&nbsp; Type : " + StringUtils.removeStart(StringUtils.removeEnd(list, "}]"), "[{");
			            	paramsRemaining.add(p);
			            	}
			            	else if(mMap.get(w).getType().size() == 1 ) {
			                	String list=mMap.get(w).getType().toString();
			                	String p="<br>"+mMap.get(w).getName() + "&nbsp; Type : " + StringUtils.removeStart(StringUtils.removeEnd(list, "]"), "[");
			                paramsRemaining.add(p);
			            	}}
		           
				}
				}
			
			return StringUtils.removeStart(StringUtils.removeEnd(paramsRemaining.toString(), "]"), "[").replaceAll(",", "")+"<br><br>";
			
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return "fail";
	
	}

	
}


