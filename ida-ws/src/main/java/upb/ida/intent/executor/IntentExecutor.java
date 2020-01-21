package upb.ida.intent.executor;

import upb.ida.intent.exception.IntentException;
import upb.ida.intent.model.ChatbotContext;
import upb.ida.intent.model.Question;

public interface IntentExecutor {

	boolean isExecutable(ChatbotContext context);

	Question getNextQuestion(ChatbotContext context);

	boolean execute(ChatbotContext context) throws IntentException;

	boolean needsMoreInformation(ChatbotContext context);

	void processResponse(ChatbotContext context);
}
