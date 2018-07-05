package upb.ida.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import upb.ida.bean.FilterOption;

@Component
public class FilterUtil {
	
	public List<Map<String, String>> getFilteredData(List<Map<String, String>> data, FilterOption filterOption){
		List<Map<String, String>> resList = new ArrayList<>();
		// TODO: Write logic to perform filtering on input data based on Filter Option
		return resList;
	}
	
	//TODO: Write a method to generate FilterOptions for First N record
	//TODO: Write a method to generate FilterOptions for Last N record
	//TODO: Write a method to generate FilterOptions for from n to n1 record
	//TODO: Write a method to generate FilterOptions for sorted top N records
}
