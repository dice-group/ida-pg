package upb.ida.provider;

import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.rivescript.macro.Subroutine;

import upb.ida.bean.ResponseBean;
import upb.ida.bean.cluster.ParamEntryChecker;
import upb.ida.temp.DemoMain;
import upb.ida.util.SessionUtil;

@Component
public class ClusterDataGetter implements Subroutine {
	@Autowired
	private DemoMain DemoMain;
	@Autowired
	private ResponseBean responseBean;
	@Autowired
	private SessionUtil sessionUtil;
	@Autowired
	private ServletContext context;
	public String call (com.rivescript.RiveScript rs, String[] args) {
		//		String user = rs.currentUser();
	String columns;
		try {
			
			columns=args[0].replaceAll("\\sand\\s"," ");
			List<String> columnsForCluster = Arrays.asList(columns.split("\\s+"));
			String actvTbl = (String) responseBean.getPayload().get("actvTbl");
			String actvDs = (String) responseBean.getPayload().get("actvDs");
			String actvScrId = (String) responseBean.getPayload().get("actvScrId");
			Map<String, Object> dataMap = new HashMap<String, Object>();
			String path = DemoMain.getFilePath(actvDs, actvTbl);

			File file = new File(context.getRealPath(path));
			List<Map<String, String>> lstt = DemoMain.convertToMap(file);
			ObjectMapper mapper = new ObjectMapper();
			ArrayNode nodeArr1 = mapper.createArrayNode();
			List <String> keys = new ArrayList <String> ();
			for(int i=0;i<columnsForCluster.size();i++) {
				keys.add(getMatchingKey(columnsForCluster.get(i), lstt.get(0)));
				
			}

			List<Object> outer_list = new ArrayList<>();
			for (int i = 0; i < lstt.size(); i++) {
				List<Double> inner_list = new ArrayList<>();
				for(int x=0;x<keys.size();x++) {
					inner_list.add(Double.parseDouble(NumberFormat.getNumberInstance(java.util.Locale.US).parse(lstt.get(i).get(keys.get(x))).toString()));
					
				}

                outer_list.add(inner_list);
				
			}
			
			@SuppressWarnings("unchecked")
			Map<String , Object> paramList = (Map<String, Object>) sessionUtil.getSessionMap().get("clusterParams");
			@SuppressWarnings("unused")
			ParamEntryChecker values;
			
				HashMap<String, String> mMap = new HashMap<String, String>();
				Iterator<String> op=paramList.keySet().iterator();
				
				while(op.hasNext()) {
					   String tempKey = op.next();
				       values=(ParamEntryChecker)paramList.get(tempKey);
				       mMap.put(values.getParamName(), values.getParamValue());
				}
				HashMap<String, Object> jsonDataForCluster = new HashMap<String, Object>();
				jsonDataForCluster.put("data", outer_list);
				jsonDataForCluster.put("params", mMap);
				jsonDataForCluster.put("algoname", args[1]);
				nodeArr1.add(mapper.readTree(mapper.writeValueAsString(jsonDataForCluster)));
			
			//nodeArr1.add(mapper.readTree(mapper.writeValueAsString(outer_list)));
			//dataMap.put("baritems",nodeArr1);
		return "pass";
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return "fail";
	
	}
	private String getMatchingKey(String key, Map<String, String> dataMap) {
		Set<String> keySet = dataMap.keySet();
		String res = null;
		for (String entry : keySet) {
			if (key.trim().equalsIgnoreCase(entry)) {
				res = entry;
				break;
			}
		}
		return res;
	}
}


