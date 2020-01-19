package upb.ida.intent.executor;

import upb.ida.bean.ResponseBean;
import upb.ida.constant.IDALiteral;
import upb.ida.dao.SoldierTimeLine;
import upb.ida.intent.AnswerHandlingStrategy;
import upb.ida.intent.exception.IntentException;
import upb.ida.intent.model.ChatbotContext;
import upb.ida.intent.model.Question;
import upb.ida.util.BeanUtil;

import java.util.Collections;
import java.util.Map;

public class SoldierCareerTimelineExecutor extends AbstractExecutor implements IntentExecutor {

	public SoldierCareerTimelineExecutor() {
		super(Collections.unmodifiableList(
			Collections.singletonList(
				new Question("What is the soldier id?", Collections.singletonList("soldier-id"), "Showing career soldier for soldier id : ${soldier-id}.", AnswerHandlingStrategy.NUMERIC_VALUE, false)
			))
		);

	}


	@Override
	public void execute(ChatbotContext context) throws IntentException {
		SoldierTimeLine soldierTimeLine = new SoldierTimeLine();
		ResponseBean responseBean = BeanUtil.getBean(ResponseBean.class);
		String actvDs = (String) responseBean.getPayload().get("actvDs");
		String actvTbl = (String) responseBean.getPayload().get("actvTbl");

		if (!actvDs.equalsIgnoreCase(IDALiteral.SS_DATASET) || !actvTbl.equalsIgnoreCase("Soldier")) {
			context.addChatbotResponse("Soldier career timeline visualization only works with SSFuehrer dataset.");
		} else {
			Map<String, String> savedAnswers = context.getSavedAnswers();
			String soldierId = savedAnswers.get("soldier-id");
			Map<String, Map<String, String>> data = soldierTimeLine.getData(soldierId, actvDs);
			responseBean.getPayload().put("soldierTimeLineData", data);
			responseBean.setActnCode(10);
			context.addChatbotResponse("Soldier career timeline is loaded.");
			context.resetOnNextRequest();
		}

	}
}
