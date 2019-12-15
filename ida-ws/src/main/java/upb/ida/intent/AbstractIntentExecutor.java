package upb.ida.intent;

import org.apache.commons.text.StringSubstitutor;

import java.io.IOException;
import java.text.ParseException;
import java.util.*;

abstract class AbstractIntentExecutor implements IntentExecutor {

	protected List<Question> questions;

	public AbstractIntentExecutor(List<Question> questions) {
		this.questions = questions;
	}

	@Override
	public abstract void execute(ChatbotContext context) throws IOException, ParseException;

	@Override
	public boolean isExecutable() {
		return false;
	}

	@Override
	public void processResponse(ChatbotContext context) {
		Map<String, String> savedAnswers = context.getSavedAnswers();
		Question activeQuestion = context.getActiveQuestion();

		if (activeQuestion != null) {
			// Extract
			List<String> answers = activeQuestion.getStrategy().extractAnswer(context);

			if (answers == null || answers.isEmpty()) {
				System.out.println("could not process answer");
				return;
			}

			System.out.println(answers);

			Map<String, String> feedbackMap = new HashMap<>();
			for (int i = 0; i < answers.size(); i++) {
				savedAnswers.put(activeQuestion.getAnswerKeys().get(i), answers.get(i));
				feedbackMap.put(activeQuestion.getAnswerKeys().get(i), answers.get(i));
			}

			// Transfer logic to question
			String feedbackTemplate = activeQuestion.getFeedbackText();
			StringSubstitutor sub = new StringSubstitutor(feedbackMap);
			String feedback = sub.replace(feedbackTemplate);
			if (feedback != null) {
				context.addChatbotResponse(feedback);
			}
		}
	}

	@Override
	public boolean needsMoreInformation(ChatbotContext context) {
		Map<String, String> savedAnswers = context.getSavedAnswers();
		for (Question q : questions) {
			boolean allAnswerKeysSaved = q.getAnswerKeys().stream().allMatch(savedAnswers::containsKey);
			if (!allAnswerKeysSaved)
				return true;
		}
		return false;
	}

	@Override
	public Question getNextQuestion(ChatbotContext context) {
		Map<String, String> savedAnswers = context.getSavedAnswers();
		for (Question q : questions) {
			boolean allAnswerKeysSaved = q.getAnswerKeys().stream().allMatch(savedAnswers::containsKey);
			if (!allAnswerKeysSaved) {
				return q;
			}
		}
		return null;
	}

}
