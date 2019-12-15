package upb.ida.intent;

import java.io.IOException;
import java.text.ParseException;

public interface IntentExecutor {

	boolean isExecutable();

	Question getNextQuestion(ChatbotContext context);

	void execute(ChatbotContext context) throws IOException, ParseException;

	boolean needsMoreInformation(ChatbotContext context);

	void processResponse(ChatbotContext context);
}
