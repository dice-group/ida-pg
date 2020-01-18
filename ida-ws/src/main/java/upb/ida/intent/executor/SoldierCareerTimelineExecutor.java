package upb.ida.intent.executor;

import upb.ida.intent.AnswerHandlingStrategy;
import upb.ida.intent.exception.IntentException;
import upb.ida.intent.model.ChatbotContext;
import upb.ida.intent.model.Question;

import java.util.Collections;

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
//		SoldierTimeLine soldierTimeLine = new SoldierTimeLine("ssfuehrer", false);
//		ResponseBean responseBean = BeanUtil.getBean(ResponseBean.class);
//
//		Map<String, String> savedAnswers = context.getSavedAnswers();
//		String soldierId = savedAnswers.get("soldier-id");
//		Map<String, Map<String, String>> data = soldierTimeLine.getData(soldierId);
//		responseBean.getPayload().put("soldierTimeLineData", data);
//		responseBean.setActnCode(10);
//		context.resetOnNextRequest();
	}
}
