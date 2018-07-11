package upb.ida.provider;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
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

@Component
public class UserParamEntry implements Subroutine {
	
	@Autowired
	private DataDumpUtil DataDumpUtil;
	@Autowired
	private SessionUtil sessionUtil;

	public String call (com.rivescript.RiveScript rs, String[] args) {
		//		String user = rs.currentUser();
		String optionalParams;
		try {
			
			optionalParams=args[0].replaceAll("\\sand\\s"," ");
			List<String> opParams = Arrays.asList(optionalParams.split("\\s+"));
			
			List<ClusterParam> paramList=  new ArrayList<>();
            paramList=DataDumpUtil.getClusterAlgoParams(args[1]);
            List<String> opParams_addable =new ArrayList<>();
            List<String> paramWithTypes =new ArrayList<>();
           
            for(int x=0;x<opParams.size();x++) {
               for(int y = 0;y<paramList.size();y++) {	
               if(paramList.get(y).getName().equals(opParams.get(x))) {	
            	if(paramList.get(y).getType().size() > 1 ) {
            	String list=paramList.get(y).getType().toString();
            	String p=opParams.get(x) + " &nbsp; Type : " + StringUtils.removeStart(StringUtils.removeEnd(list, "}]"), "[{");
            	opParams_addable.add(p);
            	paramWithTypes.add(opParams.get(x));
            	}
            	else if(paramList.get(y).getType().size()==1 ) {
                	String list=paramList.get(y).getType().toString();
                	String p=opParams.get(x) + " &nbsp; Type : " + StringUtils.removeStart(StringUtils.removeEnd(list, "]"), "[");
                	opParams_addable.add(p);
                	paramWithTypes.add(opParams.get(x));

                	}
               }
               }
            }
           
            for (int i = 0; i < paramList.size(); i++) {
                if(paramList.get(i).isOptional()==false) {
	            	if(paramList.get(i).getType().size() > 1 ) {
	            	String list=paramList.get(i).getType().toString();
	            	String p=paramList.get(i).getName() + "&nbsp; Type : " + StringUtils.removeStart(StringUtils.removeEnd(list, "}]"), "[{");
	            	opParams_addable.add(p);
	            	paramWithTypes.add(paramList.get(i).getName());

	            	}
	            	else if(paramList.get(i).getType().size() == 1 ) {
	                	String list=paramList.get(i).getType().toString();
	                	String p=paramList.get(i).getName() + "&nbsp; Type : " + StringUtils.removeStart(StringUtils.removeEnd(list, "]"), "[");
	                	opParams_addable.add(p);
	                	paramWithTypes.add(paramList.get(i).getName());

	                	}
            	}}
            Map<String,ParamEntryChecker> paramMap = new HashMap<>();
            String tempName;
            ParamEntryChecker tempParamEntry;
            
            for (int i = 0; i < paramWithTypes.size(); i++) {
                 tempName= paramWithTypes.get(i);
                 tempParamEntry = new ParamEntryChecker(tempName, null, false , paramWithTypes);
                 paramMap.put(tempName, tempParamEntry);                                 
            }
            //paramMap.put("userParList",new ParamEntryChecker(null, null, false , opParams_addable));
            sessionUtil.getSessionMap().put("clusterParams",paramMap);
            
            String paramReply=null;
            paramReply="<br>"+1+" :- "+opParams_addable.get(0);
            for (int i = 1; i < opParams_addable.size(); i++) {
                          paramReply=paramReply+"<br>"+(i+1)+" :- "+opParams_addable.get(i);                      
           }
			return paramReply +"<br>Enter values for the parameters <br><br>";
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return "fail";
	
	}

	
}


