package upb.ida.venndiagram;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;

import upb.ida.util.FileUtil;

import java.util.Collections;
import java.util.LinkedHashMap;

import static java.util.stream.Collectors.*;

/**
 * Exposes util methods to perform FDG related operations
 *
 * @author Maqbool
 *
 */
@Component
public class VENN_Util {
    @Autowired
    private FileUtil dem;
    public static final int MAX_STR = 10;

    public ArrayList<HashMap<String, Object>>  generateVennDiagram (String filePath, String[] args)
            throws JsonProcessingException, IOException, ParseException  {
        System.out.println(Arrays.toString(args));
        File file = new File(dem.fetchSysFilePath(filePath));
        
        ArrayList<String> set1 = dem.readColumnData(dem.convertToMap(file), args[0]); // soldiers
        ArrayList<String> set2 = dem.readColumnData(dem.convertToMap(file), args[2]); // medal
        int limit = Integer.parseInt(args[1]);
        
        

        System.out.println( args[0] + ": total: " + set1.size() + " after: " + new HashSet<String>(set1).size());
        System.out.println( args[2] + ": total: " + set2.size() + " after: " + new HashSet<String>(set2).size());
//        System.out.println(limit);
//        System.out.println(set1);
//        System.out.println(set2);
        
        
        Map<String, Long> set2_stats = set2.stream().collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
        
        set2_stats = set2_stats
                .entrySet()
                .stream()
                .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                .limit(limit)
                .collect(
                        toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2,
                            LinkedHashMap::new));
        
        ArrayList<HashMap<String, Object>> result = new ArrayList<HashMap<String, Object>>();
        System.out.println(set2_stats);
        
       for (String ele : set2_stats.keySet()) {
    	   HashMap<String, Object> circle = new HashMap<String, Object>();
    	   ArrayList<String> setArrayList = new ArrayList<String>();
    	   setArrayList.add(ele);
    	   circle.put("sets", setArrayList);
    	   circle.put("label", ele);
    	   circle.put("size", set2_stats.get(ele));
    	   result.add(circle);
       }
        
        return result;
    }
}
