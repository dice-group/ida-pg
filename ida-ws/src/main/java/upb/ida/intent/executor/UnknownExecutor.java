package upb.ida.intent.executor;

import upb.ida.intent.exception.IntentException;
import upb.ida.intent.model.ChatbotContext;
import upb.ida.intent.model.Question;

public class UnknownExecutor extends AbstractExecutor implements IntentExecutor {

	public UnknownExecutor() {
		super(null);
	}

	@Override
	public Question getNextQuestion(ChatbotContext context) {
		return null;
	}

	@Override
	public boolean execute(ChatbotContext context) throws IntentException {
		context.addChatbotResponse("Sorry, I could not understand your message. Please try again.");
		return true;
	}

	@Override
	public boolean needsMoreInformation(ChatbotContext context) {
		return false;
	}

}
