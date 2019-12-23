package upb.ida.intent.executor;

import org.apache.commons.text.StringSubstitutor;
import upb.ida.intent.exception.IntentExecutorException;
import upb.ida.intent.model.ChatbotContext;
import upb.ida.intent.model.Question;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractExecutor implements IntentExecutor {

	protected List<Question> questions;

	public AbstractExecutor(List<Question> questions) {
		this.questions = questions;
	}

	@Override
	public abstract void execute(ChatbotContext context) throws IntentExecutorException;

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
				context.addChatbotResponse("Sorry, your answer could not be processed. Please try again.");
				System.out.println("could not process answer");
				return;
			}

			System.out.println(answers);

			Map<String, String> feedbackMap = new HashMap<>();
			for (int i = 0; i < answers.size(); i++) {
				savedAnswers.put(activeQuestion.getAnswerKeys().get(i), answers.get(i));
				feedbackMap.put(activeQuestion.getAnswerKeys().get(i), answers.get(i));
			}

			// TODO transfer logic to Question
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
