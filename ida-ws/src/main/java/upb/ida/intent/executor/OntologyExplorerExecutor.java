package upb.ida.intent.executor;

import upb.ida.intent.exception.IntentException;
import upb.ida.intent.model.ChatbotContext;

public class OntologyExplorerExecutor extends AbstractExecutor implements IntentExecutor {

	public OntologyExplorerExecutor() {
		super(null);
	}

	@Override
	public boolean execute(ChatbotContext context) throws IntentException {
		// TODO uncomment this after merging ontology explorer code
//		ResponseBean responseBean = BeanUtil.getBean(ResponseBean.class);
//		Map<String, Object> dataMap = responseBean.getPayload();
//		String actvDs = (String) responseBean.getPayload().get("actvDs");
//
//		if (actvDs == null || actvDs.isEmpty()) {
//			context.addChatbotResponse("Please load the dataset before loading the ontology.");
//			return false;
//		}
//		else {
//			DataRepository dataRepository = new DataRepository(false);
//			JSONObject ontologyData = dataRepository.getOntologyData(actvDs);
//			if (ontologyData != null) {
//				dataMap.put("ontologyData", ontologyData);
//				responseBean.setPayload(dataMap);
//				responseBean.setActnCode(IDALiteral.UIA_ONTOLOGY);
//				context.addChatbotResponse("Ontology is now displayed.");
//			} else {
//				context.addChatbotResponse("Ontology does not exist for current dataset.");
//			}
//			return true;
//		}

		return true;
	}
}
