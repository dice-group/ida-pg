package upb.ida.provider;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.rivescript.macro.Subroutine;

import upb.ida.bean.cluster.ParamEntryChecker;
import upb.ida.util.SessionUtil;

import upb.ida.bean.cluster.CollectedParams;;

@Component
public class UserParamValueCollector implements Subroutine {
	
	@Autowired
	private SessionUtil sessionUtil;
	public String call (com.rivescript.RiveScript rs, String[] args) {
		//		String user = rs.currentUser();
	
		try {
			
			@SuppressWarnings("unchecked")
			Map<String , Object> paramList = (Map<String, Object>) sessionUtil.getSessionMap().get("clusterParams");

			ParamEntryChecker values;
			ParamEntryChecker allProvider;
			paramList.get(args[0]);
			List<CollectedParams> pCollected ;
            
			Iterator<String> op=paramList.keySet().iterator();
			int checker=0;
			if(checker!=paramList.size()) {		
				 if(paramList.containsKey(args[0]));{
					 values= (ParamEntryChecker) paramList.get(args[0]);
				 
				 if (values.isProvided()) {
				
					 return "fail";
				 
				 }
				 else if (!values.isProvided()) {
				
					 values.setParamName(args[0]);
					 values.setParamValue(args[1]);
					 values.setProvided(true);
					// pCollected=pCollected.add(args[0]);
					 sessionUtil.getSessionMap().put("collectedParams",args[0]);
					 while(op.hasNext()) {
							String tempKey = op.next();
						       allProvider=(ParamEntryChecker)paramList.get(tempKey);
						       if(allProvider.isProvided()){
						    	   checker=checker +1;
						    	   }
					
						}
					 if(checker==paramList.size()) {
						 return "fail";
						}
					 
					 else {
					 
						 return  "pass";}
					 
				 }
				}
				 
				
				
			}
			

			
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return "fail";
	
	}
}


