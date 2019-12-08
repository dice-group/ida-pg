package upb.ida.intent;

import java.util.Map;

public interface IntentExecutor {

	boolean isExecutable();

	Question getNextQuestion(Map<String, Object> context);

	void execute();

	boolean needsMoreInformation(Map<String, Object> context);

	String getNextResponse(Map<String, Object> context);

	void processResponse(String originalMessage, Map<String, Object> context);
}
