package upb.ida.intent;

import org.apache.commons.text.StringSubstitutor;

import java.util.*;

abstract class AbstractIntentExecutor implements IntentExecutor {

	@Override
	public boolean isExecutable() {
		return false;
	}

	@Override
	public abstract void execute();

	@Override
	public void processResponse(String originalMessage, Map<String, Object> context) {
		Map<String, String> savedAnswers = (Map<String, String>) context.get("savedAnswers");
		if (savedAnswers == null) {
			savedAnswers = new HashMap<>();
			context.put("savedAnswers", savedAnswers);
		}

		Question activeQuestion = (Question) context.get("activeQuestion");

		if (activeQuestion != null) {
			savedAnswers.put(activeQuestion.getQuestionKey(), originalMessage);
			String feedbackTemplate = activeQuestion.getFeedbackText();
			StringSubstitutor sub = new StringSubstitutor(Collections.singletonMap(activeQuestion.getQuestionKey(), originalMessage));
			String feedback = sub.replace(feedbackTemplate);
			this.addChatbotResponse(feedback, context);
		}
	}

	protected boolean needsMoreInformation(List<Question> questions, Map<String, Object> context) {
		Map<String, String> savedAnswers = (Map<String, String>) context.get("savedAnswers");
		for (Question q : questions) {
			if (!savedAnswers.containsKey(q.getQuestionKey())) {
				return true;
			}
		}
		return false;
	}

	protected Question getNextQuestion(List<Question> questions, Map<String, Object> context) {
		Map<String, String> savedAnswers = (Map<String, String>) context.get("savedAnswers");
		for (Question q : questions) {
			if (!savedAnswers.containsKey(q.getQuestionKey())) {
				return q;
			}
		}
		return null;
	}


	// TODO Move to refactored context class
	private void addChatbotResponse(String response, Map<String, Object> context) {
		List<String> chatbotResponses = (List<String>) context.get("chatbotResponses");
		if (chatbotResponses == null) {
			chatbotResponses = new ArrayList<>();
			context.put("chatbotResponses", chatbotResponses);
		}
		chatbotResponses.add(response);
	}
}
