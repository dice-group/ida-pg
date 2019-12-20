package upb.ida.provider;

import com.rivescript.macro.Subroutine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import upb.ida.bean.ResponseBean;
import upb.ida.constant.IDALiteral;
import upb.ida.dao.DataRepository;
import upb.ida.util.FileUtil;
import java.util.*;

/**
 * GeoDiagramHandler is a subroutine that is used to generate data for
 * Venn diagram .
 *
 * @author Maqbool
 */

@Component
public class GeoDiagramHandler implements Subroutine {
	@Autowired
	private FileUtil DemoMain;
	@Autowired
	private ResponseBean responseBean;

	/**
	 * Method to create response for Geo Spatial Diagram visualization
	 * @param args
	 *
	 * @return  String - pass or fail
	 */

	public String call (com.rivescript.RiveScript rs, String[] args) {

		String actvTbl = (String) responseBean.getPayload().get("actvTbl");
		String actvDs = (String) responseBean.getPayload().get("actvDs");
		Map<String, Object> dataMap = responseBean.getPayload();
		String path = DemoMain.getDTFilePath(actvDs, actvTbl);

		ArrayList<HashMap<String, ArrayList<Double>>> response = new ArrayList<>();
		try {
			double maxLat;
			double maxLon;
			DataRepository dataRepository = new DataRepository(false);
			List<Map<String, String>> data = dataRepository.getData(actvTbl, actvDs);
			for (Map<String, String> ele : data) {
				HashMap<String, ArrayList<Double>> row = new HashMap<>();
				ArrayList<Double> coordinates = new ArrayList<>(2);
				double lat = Double.parseDouble(ele.get(args[0]));
				double lon = Double.parseDouble(ele.get(args[1]));
				maxLat = lat;
				maxLon = lon;

				coordinates.add(lon);
				coordinates.add(lat);
				row.put("COORDINATES", coordinates);
				response.add(row);

				dataMap.put("label", "geo spatial diagram data");
				dataMap.put("lat", maxLat);
				dataMap.put("lon", maxLon);
				responseBean.setActnCode(IDALiteral.UIA_GSDIAGRAM);
				responseBean.setPayload(dataMap);
			}
			dataMap.put("gsDiagramData", response);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "pass";
	}
}
