package upb.ida.intent.executor;

import upb.ida.bean.ResponseBean;
import upb.ida.constant.IDALiteral;
import upb.ida.dao.DataRepository;
import upb.ida.intent.AnswerHandlingStrategy;
import upb.ida.intent.exception.IntentException;
import upb.ida.intent.model.ChatbotContext;
import upb.ida.intent.model.Question;
import upb.ida.util.BeanUtil;

import java.util.*;

public class GeoSpatialExecutor extends AbstractExecutor implements IntentExecutor {

	public GeoSpatialExecutor() {
		super(Collections.unmodifiableList(
				Arrays.asList(
						new Question("Which column should I use as latitude?", Collections.singletonList("latitude"), "Using ${latitude} as latitude.", null, AnswerHandlingStrategy.ACTIVE_TABLE_COLUMNS, false),
						new Question("Which column should I use as longitude?", Collections.singletonList("longitude"), "Using ${longitude} as longitude.", null, AnswerHandlingStrategy.ACTIVE_TABLE_COLUMNS, false)
				))
		);
	}

	@Override
	public void execute(ChatbotContext context) throws IntentException {
		Map<String, String> savedAnswers = context.getSavedAnswers();
		String latitude = savedAnswers.get("latitude");
		String longitude = savedAnswers.get("longitude");

		ResponseBean responseBean = BeanUtil.getBean(ResponseBean.class);
		String actvTbl = (String) responseBean.getPayload().get("actvTbl");
		String actvDs = (String) responseBean.getPayload().get("actvDs");
		Map<String, Object> dataMap = responseBean.getPayload();

		ArrayList<HashMap<String, ArrayList<Double>>> response = new ArrayList<>();
		try {
			double maxLat;
			double maxLon;
			DataRepository dataRepository = new DataRepository(false);
			List<Map<String, String>> data = dataRepository.getData(actvTbl, actvDs);
			for (Map<String, String> ele : data) {
				HashMap<String, ArrayList<Double>> row = new HashMap<>();
				ArrayList<Double> coordinates = new ArrayList<>(2);
				double lat = Double.parseDouble(ele.get(latitude));
				double lon = Double.parseDouble(ele.get(longitude));
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
			context.addChatbotResponse("Geo Spatial diagram is now loaded");
			context.resetOnNextRequest();
		} catch (Exception e) {
			e.printStackTrace();
			context.addChatbotResponse("Geo Spatial diagram could not be loaded");
		}


	}
}
