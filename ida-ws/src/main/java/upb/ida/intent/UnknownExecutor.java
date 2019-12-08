package upb.ida.intent;

import java.util.Map;

public class UnknownExecutor extends AbstractIntentExecutor implements IntentExecutor {

	@Override
	public Question getNextQuestion(Map<String, Object> context) {
		return null;
	}

	@Override
	public void execute() {

	}

	@Override
	public boolean needsMoreInformation(Map<String, Object> context) {
		return false;
	}

	@Override
	public String getNextResponse(Map<String, Object> context) {
		return "How can I help you?";
	}


}
