package upb.ida.intent.executor;

import org.json.simple.JSONObject;
import upb.ida.bean.ResponseBean;
import upb.ida.constant.IDALiteral;
import upb.ida.dao.DataRepository;
import upb.ida.intent.exception.IntentException;
import upb.ida.intent.model.ChatbotContext;
import upb.ida.util.BeanUtil;

import java.util.Map;

public class OntologyExplorerExecutor extends AbstractExecutor implements IntentExecutor {

	public OntologyExplorerExecutor() {
		super(null);
	}

	@Override
	public void execute(ChatbotContext context) throws IntentException {
		ResponseBean responseBean = BeanUtil.getBean(ResponseBean.class);
		Map<String, Object> dataMap = responseBean.getPayload();
		String actvDs = (String) responseBean.getPayload().get("actvDs");

		if (actvDs == null || actvDs.isEmpty()) {
			context.addChatbotResponse("Please load the dataset before loading the ontology.");
		}
		else {
			DataRepository dataRepository = new DataRepository(false);
			JSONObject ontologyData = dataRepository.getOntologyData(actvDs);
			if (ontologyData != null) {
				dataMap.put("ontologyData", ontologyData);
				responseBean.setPayload(dataMap);
				responseBean.setActnCode(IDALiteral.UIA_ONTOLOGY);
				context.addChatbotResponse("Ontology is now displayed.");
			} else {
				context.addChatbotResponse("Ontology does not exist for current dataset.");
			}
		}

		context.resetOnNextRequest();
	}
}
