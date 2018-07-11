package upb.ida.provider;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.rivescript.macro.Subroutine;

import upb.ida.bean.cluster.ClusterParam;
import upb.ida.bean.cluster.ParamEntryChecker;
import upb.ida.util.DataDumpUtil;
import upb.ida.util.SessionUtil;
import upb.ida.util.DataDumpUtil;

@Component
public class CheckParamCollected implements Subroutine {
	@Autowired
	private DataDumpUtil DataDumpUtil;
	@Autowired
	private SessionUtil sessionUtil;
	
	public String call (com.rivescript.RiveScript rs, String[] args) {
		try {
			
			String collected = (String) sessionUtil.getSessionMap().get("collectedParams");
			@SuppressWarnings("unchecked")
			Map<String , Object> paramList = (Map<String, Object>) sessionUtil.getSessionMap().get("clusterParams");
			List<String> paramsRemaining =new ArrayList<>();
			List<ClusterParam> listForTypes=  new ArrayList<>();
			String algoName=sessionUtil.getAlgoNameOrignal();
			listForTypes=DataDumpUtil.getClusterAlgoParams(algoName);

			ParamEntryChecker allProvider;
				for(int i=0 ; i < paramList.size();i++) {
					allProvider=(ParamEntryChecker) paramList.get(collected);
				 if(allProvider==null){
					
			            	if(listForTypes.get(i).getType().size() > 1 ) {
			            	String list=listForTypes.get(i).getType().toString();
			            	String p=listForTypes.get(i).getName() + "&nbsp; Type : " + StringUtils.removeStart(StringUtils.removeEnd(list, "}]"), "[{");
			            	paramsRemaining.add(p);
			            	}
			            	else if(listForTypes.get(i).getType().size() == 1 ) {
			                	String list=listForTypes.get(i).getType().toString();
			                	String p=listForTypes.get(i).getName() + "&nbsp; Type : " + StringUtils.removeStart(StringUtils.removeEnd(list, "]"), "[");
			                paramsRemaining.add(p);
			                	
		            	}
					 
				 }
				
				}
			
			return paramsRemaining.toString();
			
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return "fail";
	
	}

	
}


