package upb.ida.intent.executor;

import upb.ida.intent.exception.IntentExecutorException;
import upb.ida.intent.model.ChatbotContext;
import upb.ida.intent.model.Question;

public interface IntentExecutor {

	boolean isExecutable(ChatbotContext context);

	Question getNextQuestion(ChatbotContext context);

	void execute(ChatbotContext context) throws IntentExecutorException;

	boolean needsMoreInformation(ChatbotContext context);

	void processResponse(ChatbotContext context);
}
