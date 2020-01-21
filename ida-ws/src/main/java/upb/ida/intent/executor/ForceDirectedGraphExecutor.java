package upb.ida.intent.executor;

import upb.ida.bean.ResponseBean;
import upb.ida.constant.IDALiteral;
import upb.ida.fdg.FdgUtil;
import upb.ida.intent.AnswerHandlingStrategy;
import upb.ida.intent.model.ChatbotContext;
import upb.ida.intent.model.Question;
import upb.ida.util.BeanUtil;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

public class ForceDirectedGraphExecutor extends AbstractExecutor implements IntentExecutor {

	public ForceDirectedGraphExecutor() {
		super(Collections.unmodifiableList(
				Arrays.asList(
						new Question("What is the source node?", Collections.singletonList("source"), "Source node is set to ${source}.", null, AnswerHandlingStrategy.ACTIVE_TABLE_COLUMNS, false),
						new Question("What is the target node?", Collections.singletonList("target"), "Target node is set to ${target}.", null, AnswerHandlingStrategy.ACTIVE_TABLE_COLUMNS, false),
						new Question("Which feature should depict the strength between the nodes?", Collections.singletonList("strength"), "Using ${strength} for strength.", null, AnswerHandlingStrategy.ACTIVE_TABLE_COLUMNS, false)
				))
		);
	}

	@Override
	public boolean execute(ChatbotContext context) {

		try {
			Map<String, String> savedAnswers = context.getSavedAnswers();
			String sourceNode = savedAnswers.get("source");
			String targetNode = savedAnswers.get("target");
			String strengthNode = savedAnswers.get("strength");

			ResponseBean responseBean = BeanUtil.getBean(ResponseBean.class);
			FdgUtil FDG_Util = BeanUtil.getBean(FdgUtil.class);
			String actvTbl = (String) responseBean.getPayload().get("actvTbl");
			String actvDs = (String) responseBean.getPayload().get("actvDs");
			Map<String, Object> dataMap = responseBean.getPayload();
			dataMap.put("label", "Fdg Data");
			/**
			 * function call takes file path and arguments as
			 * input to get data for force directed graph
			 */
			dataMap.put("fdgData", FDG_Util.generateFDG(actvTbl, sourceNode.toLowerCase(), targetNode, strengthNode, actvDs));
			responseBean.setPayload(dataMap);
			responseBean.setActnCode(IDALiteral.UIA_FDG);

			context.addChatbotResponse("Force directed graph is now loaded");
			return true;
		} catch (Exception e) {
			context.addChatbotResponse("Force directed graph could not be loaded");
			e.printStackTrace();
			return false;
		}
	}
}
