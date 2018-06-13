package upb.ida.util;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class getAxisJson {
    String x;
    String y;

    //To initialize variables with parameterized constructor
    public getAxisJson(String x, String y) {
        this.x = x;
        this.y=y;
    }
    public Object[] NewJsonObjct(String x,String y, List<Map< String, String >> lstt)throws java.io.IOException {
        ArrayList listt = new ArrayList();
        ArrayList  x_axis= new ArrayList();
        for (int i = 0; i < lstt.size(); i++) {
            HashMap mMap = new HashMap();
            if (lstt.get(i).containsKey(x) && lstt.get(i).containsKey(y)) ;


            mMap = new HashMap(); // create a new one!
            mMap.put(x, lstt.get(i).get(x));
            mMap.put(y, lstt.get(i).get(y));
            listt.add(mMap);
        }
         String jjson = new Gson().toJson(listt);
         Object ar[] = new String[2];
         x_axis.add(x);
         String xaxis = new Gson().toJson(x_axis);
         ar[0]=xaxis;
         ar[1]=jjson;

         return ar;
    }



}