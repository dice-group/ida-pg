package upb.ida.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListMapsForFilteringCheck {
	
	
	public List<Map<String,String>> generateMapsforTopAsc(){
		
		List<Map<String, String>> forTopHeightAsc = new ArrayList<>();
  	
	HashMap<String, String> mapE1 = new HashMap<String, String>();
	mapE1.put("name", "andrew");
	mapE1.put("height", "144");
	mapE1.put("weight", "56");
    mapE1.put("sibling","yes");
    
	HashMap<String, String> mapE2 = new HashMap<String, String>();
	mapE2.put("name", "dave");
	mapE2.put("height","154");
	mapE2.put("weight", "23");
	 mapE2.put("sibling","no");

	HashMap<String, String> mapE3 = new HashMap<String, String>();
	mapE3.put("name", "lisa");
	mapE3.put("height", "165");
	mapE3.put("weight", "46");
	 mapE3.put("sibling","yes");
	 
	forTopHeightAsc.add(mapE1);
	forTopHeightAsc.add(mapE2);
	forTopHeightAsc.add(mapE3);
	
	return forTopHeightAsc;
	}
	
	public List<Map<String,String>> generateMapsforTopDes(){	

		List<Map<String, String>> forTopHeightDes = new ArrayList<>();
  	
	HashMap<String, String> mapE3 = new HashMap<String, String>();
	mapE3.put("name", "andrew");
	mapE3.put("height", "144");
	mapE3.put("weight", "56");
    mapE3.put("sibling","yes");
    
	HashMap<String, String> mapE2 = new HashMap<String, String>();
	mapE2.put("name", "dave");
	mapE2.put("height","154");
	mapE2.put("weight", "23");
	 mapE2.put("sibling","no");

	HashMap<String, String> mapE1 = new HashMap<String, String>();
	mapE1.put("name", "lisa");
	mapE1.put("height", "165");
	mapE1.put("weight", "46");
	 mapE1.put("sibling","yes");
	 
	forTopHeightDes.add(mapE1);
	forTopHeightDes.add(mapE2);
	forTopHeightDes.add(mapE3);
	
	return forTopHeightDes;
	
	}
}