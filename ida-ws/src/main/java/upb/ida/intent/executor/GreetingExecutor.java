package upb.ida.intent.executor;

import upb.ida.intent.exception.IntentException;
import upb.ida.intent.model.ChatbotContext;
import upb.ida.intent.model.Question;

public class GreetingExecutor extends AbstractExecutor implements IntentExecutor {

	public GreetingExecutor() {
		super(null);
	}

	@Override
	public Question getNextQuestion(ChatbotContext context) {
		return null;
	}

	@Override
	public void execute(ChatbotContext context) throws IntentException {
		context.addChatbotResponse("Hello! How may I help you?");
		context.resetOnNextRequest();
	}

	@Override
	public boolean needsMoreInformation(ChatbotContext context) {
		return false;
	}
}
