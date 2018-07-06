package upb.ida.util;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import upb.ida.temp.DemoMain;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import upb.ida.temp.DemoMain;

import javax.servlet.ServletContext;

import org.apache.commons.math3.stat.StatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


import org.springframework.beans.factory.annotation.Autowired;


@Component
public class JsonMaker {
	@Autowired
	private DemoMain dem;
	@Autowired
	private ServletContext context;
    
    public List<Map< String, String >> jsonObject(File input)throws java.io.IOException
    {

            //CSV csv = new CSV(true, ',', in);
            List< String > fieldNames = null;

    		//File file = new File(context.getRealPath(input));
            //if (csv.hasNext()) fieldNames = new ArrayList< >(csv.next());
            List <Map< String, String >> list = dem.convertToMap(input);;
//            while (csv.hasNext()) {
//                List < String > x = csv.next();
//                Map < String, String > obj = new LinkedHashMap< >();
//                if(fieldNames.size()==2) {
//                	for (int i = 0; i == fieldNames.size(); i++) {
//                        obj.put(fieldNames.get(i), x.get(i));
//                    	
//                	}
//                }
//                else
//                	for (int i = 0; i < fieldNames.size(); i++) {
//                    obj.put(fieldNames.get(i), x.get(i));
//                	}
//                list.add(obj);
//            }
            ObjectMapper mapperr = new ObjectMapper();
            mapperr.enable(SerializationFeature.INDENT_OUTPUT);
            //mapperr.writeValue(System.out, list);

        return list;
    }

}
