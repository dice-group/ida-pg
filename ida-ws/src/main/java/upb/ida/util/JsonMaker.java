package upb.ida.util;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class JsonMaker {
    
    public List<Map< String, String >> jsonObject(InputStream in)throws java.io.IOException
    {

            CSV csv = new CSV(true, ',', in);
            List< String > fieldNames = null;
            if (csv.hasNext()) fieldNames = new ArrayList< >(csv.next());
            List <Map< String, String >> list = new ArrayList < > ();
            while (csv.hasNext()) {
                List < String > x = csv.next();
                Map < String, String > obj = new LinkedHashMap< >();
                for (int i = 0; i < fieldNames.size(); i++) {
                    obj.put(fieldNames.get(i), x.get(i));
                }
                list.add(obj);
            }
            ObjectMapper mapperr = new ObjectMapper();
            mapperr.enable(SerializationFeature.INDENT_OUTPUT);
            //mapperr.writeValue(System.out, list);

        return list;
    }

}
