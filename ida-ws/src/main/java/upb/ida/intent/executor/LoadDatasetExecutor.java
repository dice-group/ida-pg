package upb.ida.intent.executor;

import org.json.simple.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;
import upb.ida.bean.ResponseBean;
import upb.ida.constant.IDALiteral;
import upb.ida.dao.DataRepository;
import upb.ida.intent.SimilarityUtil;
import upb.ida.intent.exception.IntentException;
import upb.ida.intent.model.ChatbotContext;
import upb.ida.intent.model.Question;
import upb.ida.util.BeanUtil;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class LoadDatasetExecutor extends AbstractExecutor implements IntentExecutor {

	private static String FUSEKI_URL = System.getenv("FUSEKI_URL");
	private static String FUSEKI_USER = System.getenv("FUSEKI_USER");
	private static String FUSEKI_PW = System.getenv("FUSEKI_PW");
	private DataRepository dataRepository = new DataRepository(false);

	public LoadDatasetExecutor() {
		super(null);
	}

	@Override
	public Question getNextQuestion(ChatbotContext context) {
		return null;
	}


	@Override
	public boolean needsMoreInformation(ChatbotContext context) {
		return false;
	}

	@Override
	public boolean execute(ChatbotContext context) throws IntentException {
		ResponseBean responseBean = BeanUtil.getBean(ResponseBean.class);
		Map<String, Object> dataMap = responseBean.getPayload();
		String dataset = this.getDatasetFromMessage(context.getCurrentMessage());

		if (dataset == null) {
			responseBean.setActnCode(IDALiteral.UIA_NOACTION);
			responseBean.setPayload(null);
			context.addChatbotResponse("The requested dataset does not exist.");
		} else {
			Map<String, Object> metaData = dataRepository.getDataSetMD(dataset);
			dataMap.put("label", dataset);
			dataMap.put("dsName", dataset);
			dataMap.put("dsMd", metaData);
			responseBean.setPayload(dataMap);
			responseBean.setActnCode(IDALiteral.UIA_LOADDS);
			context.addChatbotResponse("Requested dataset is loaded.");
		}
		return true;
	}

	private String getDatasetFromMessage(String userMessage) {
		List<String> allDatasets = this.getAllDatasets();
		allDatasets = allDatasets.stream().filter(s -> !s.endsWith("-ontology")).collect(Collectors.toList());
		return SimilarityUtil.extractTopKeyword(userMessage, allDatasets);
	}

	private List<String> getAllDatasets() {
		List<String> datasets = new ArrayList<>();

		String plainCreds = FUSEKI_USER + ":" + FUSEKI_PW;
		byte[] plainCredsBytes = plainCreds.getBytes();
		String base64Creds = Base64.getMimeEncoder().encodeToString(plainCredsBytes);

		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Basic " + base64Creds);

		RestTemplate restTemplate = new RestTemplate();

		JSONObject datasetsResponse = restTemplate.exchange
				(FUSEKI_URL + "$/datasets", HttpMethod.GET, new HttpEntity<String>(headers), JSONObject.class).getBody();

		for (Object datasetInfo : ((ArrayList) (datasetsResponse.get("datasets")))) {
			String datasetName = ((Map<String, Object>) datasetInfo).get("ds.name").toString().substring(1);
			datasets.add(datasetName);
		}

		return datasets;
	}

}
