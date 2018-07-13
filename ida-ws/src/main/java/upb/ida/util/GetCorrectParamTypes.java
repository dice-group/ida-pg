package upb.ida.util;

import java.util.HashMap;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import upb.ida.bean.cluster.ClusterParam;
import upb.ida.bean.cluster.ParamEntryChecker;
/**
 * Class to expose method to get paramter types 
 * and change type of values in input paramList
 * 
 * @author Faisal
 *
 */
@Component
public class GetCorrectParamTypes {
	/**
	 * Method correctTypeValues  to get data with correct types
	 * @param paramList
	 *            - {@link correctTypeValues#rs}
	 * @param algoName
	 *            - {@link correctTypeValues#algoName}
	 * @param resList
	 *            - {@link correctTypeValues#resList}
	 *            
	 * @return - Map of parameter values with corrected types
	 */
	public HashMap<String, Object> correctTypeValues(Map<String , Object> paramList,String algoName,List<ClusterParam> resList){
		
		ParamEntryChecker values;
		
			HashMap<String, Object> mMap = new HashMap<String, Object>();
			Iterator<String> op=paramList.keySet().iterator();

	        int paramValInt = 0;
            boolean bool;
            
            float paramValFloat= 0;
	    	while(op.hasNext()) {
				   String tempKey = op.next();
			       values=(ParamEntryChecker)paramList.get(tempKey);
			       for(int i = 0 ;i < resList.size() ;i++) {
		            	
			    	   if(values.getParamName().equals(resList.get(i).getName())) {
			    		  

			    		   if(resList.get(i).getType().get(0).equals("int")) {
			    			   paramValInt=Integer.parseInt(values.getParamValue());
			    			   mMap.put(values.getParamName(), paramValInt);
			    			   
			    		   }
			    		   else if(resList.get(i).getType().get(0).equals("float")) {
			    			   paramValFloat = Float.parseFloat(values.getParamValue());
			    			   mMap.put(values.getParamName(), paramValFloat);
			    			   
			    		   }
			    		   else if(resList.get(i).getType().get(0).equals("boolean")) {
			    			   if(values.getParamValue().equals("true")||values.getParamValue().equals("True"))  {
				    			   	bool=true;
				    			   	mMap.put(values.getParamName(), bool);
				    			   	}
					    			if(values.getParamValue().equals("false")||values.getParamValue().equals("False"))  {
					    			bool=false;
					    			mMap.put(values.getParamName(), bool);
					    			}
			    			   
			    		   }
			    		   else if(values.getParamValue().equals("true")||values.getParamValue().equals("True"))  {
			    			   	bool=true;
			    			   	mMap.put(values.getParamName(), bool);
			    			   	}
			    		   else if(values.getParamValue().equals("false")||values.getParamValue().equals("False"))  {
				    			bool=false;
				    			mMap.put(values.getParamName(), bool);
				    			} 
				    		
			    	       else {
			    			   mMap.put(values.getParamName(), values.getParamValue());
			    			   
			    		   }
	    		   }
	   		   }
	   	   }
			return mMap;
	}

}
