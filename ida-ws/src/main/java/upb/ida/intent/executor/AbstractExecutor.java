package upb.ida.intent.executor;

import org.apache.commons.text.StringSubstitutor;
import upb.ida.intent.exception.IntentException;
import upb.ida.intent.model.ChatbotContext;
import upb.ida.intent.model.Question;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Contains common framework code to execute most of the common scenarios
 */
public abstract class AbstractExecutor implements IntentExecutor {

	protected List<Question> questions;

	public AbstractExecutor(List<Question> questions) {
		this.questions = questions;
	}

	/**
	 * Must be implemented by concrete subclasses. Final step to be executed after all information required to execute
	 * is gathered.
	 *
	 * @param context current state of NLE
	 * @throws IntentException
	 */
	@Override
	public abstract void execute(ChatbotContext context) throws IntentException;

	/**
	 * Can be implemented to check for pre conditions before executing. Currently unused.
	 *
	 * @param context
	 * @return boolean
	 */
	@Override
	public boolean isExecutable(ChatbotContext context) {
		return false;
	}

	/**
	 * Processes the messages sent by the user. Saves the answer if the question was asked.
	 *
	 * @param context current state of NLE
	 */
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

			Map<String, String> feedbackMap = new HashMap<>();
			for (int i = 0; i < answers.size(); i++) {
				savedAnswers.put(activeQuestion.getAnswerKeys().get(i), answers.get(i));
				feedbackMap.put(activeQuestion.getAnswerKeys().get(i), answers.get(i));
			}

			String feedbackTemplate = activeQuestion.getFeedbackText();
			StringSubstitutor sub = new StringSubstitutor(feedbackMap);
			String feedback = sub.replace(feedbackTemplate);
			if (feedback != null) {
				context.addChatbotResponse(feedback);
			}
		}
	}

	/**
	 * Decides if the executor needs more information before executing the final step
	 *
	 * @param context current state of NLE
	 * @return boolean
	 */
	@Override
	public boolean needsMoreInformation(ChatbotContext context) {
		if(questions == null)
			return false;

		Map<String, String> savedAnswers = context.getSavedAnswers();
		for (Question q : questions) {
			boolean allAnswerKeysSaved = q.getAnswerKeys().stream().allMatch(savedAnswers::containsKey);
			if (!allAnswerKeysSaved)
				return true;
		}
		return false;
	}

	/**
	 * Gets the next question to ask to the user
	 *
	 * @param context current state of NLE
	 * @return next Question instance
	 */
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
