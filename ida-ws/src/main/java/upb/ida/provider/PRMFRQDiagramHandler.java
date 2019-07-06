package upb.ida.provider;

import com.rivescript.macro.Subroutine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import upb.ida.bean.ResponseBean;
import upb.ida.constant.IDALiteral;
import upb.ida.util.FileUtil;

import java.io.File;
import java.io.IOException;
import java.util.*;

@Component
public class PRMFRQDiagramHandler implements Subroutine{
    @Autowired
    private FileUtil DemoMain;
    @Autowired
    private ResponseBean responseBean;

    public String call (com.rivescript.RiveScript rs, String[] args) {

        String actvTbl = (String) responseBean.getPayload().get("actvTbl");
        String actvDs = (String) responseBean.getPayload().get("actvDs");
        Map<String, Object> dataMap = responseBean.getPayload();
        String path = DemoMain.getDTFilePath(actvDs, actvTbl);

        ArrayList<HashMap<String, ArrayList<Double>>> response = new ArrayList<>();
        try {
            double maxLat;
            double maxLon;
            List<Map<String, String>> data = DemoMain.convertToMap(new File(DemoMain.fetchSysFilePath(path)));
            for (Map<String, String> ele : data) {
                HashMap<String, ArrayList<Double>> row = new HashMap<>();
                ArrayList<Double> coordinates = new ArrayList<>(3);
                double lat = Double.parseDouble(ele.get(args[0]));
                double lon = Double.parseDouble(ele.get(args[1]));
                double promotn_freq = Double.parseDouble(ele.get(args[2]));

				maxLat = lat;
				maxLon = lon;

                coordinates.add(lon);
                coordinates.add(lat);

                for(int i = 0; i < Math.round(promotn_freq); i++){
                    row.put("COORDINATES", coordinates);
                    response.add(row);
                }

                dataMap.put("label", "promotion frequency diagram data");
                dataMap.put("lat", maxLat);
                dataMap.put("lon", maxLon);
                responseBean.setActnCode(IDALiteral.UIA_PRMFRQDIAGRAM);
                responseBean.setPayload(dataMap);
            }
            dataMap.put("prmfrqDiagramData", response);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "pass";
    }
}
