package upb.ida.intent.model;

import upb.ida.intent.AnswerHandlingStrategy;

import java.util.List;

public class Question {
	private String question;
	private List<String> answerKeys;
	private String feedbackText;
	private String helpText;
	private AnswerHandlingStrategy strategy;
	private boolean optional;

	public Question(String question, List<String> answerKeys, String feedbackText, String helpText, AnswerHandlingStrategy strategy, boolean optional) {
		this.question = question;
		this.answerKeys = answerKeys;
		this.feedbackText = feedbackText;
		this.helpText = helpText;
		this.strategy = strategy;
		this.optional = optional;
	}

	public Question(String question, List<String> answerKeys, String feedbackText, AnswerHandlingStrategy strategy, boolean optional) {
		this.question = question;
		this.answerKeys = answerKeys;
		this.feedbackText = feedbackText;
		this.strategy = strategy;
		this.optional = optional;
	}

	public String getQuestion() {
		return question;
	}

	public void setQuestion(String question) {
		this.question = question;
	}

	public List<String> getAnswerKeys() {
		return answerKeys;
	}

	public void setAnswerKeys(List<String> answerKeys) {
		this.answerKeys = answerKeys;
	}

	public String getFeedbackText() {
		return feedbackText;
	}

	public void setFeedbackText(String feedbackText) {
		this.feedbackText = feedbackText;
	}

	public void setStrategy(AnswerHandlingStrategy strategy) {
		this.strategy = strategy;
	}

	public String getHelpText() {
		return helpText;
	}

	public void setHelpText(String helpText) {
		this.helpText = helpText;
	}

	public AnswerHandlingStrategy getStrategy() {
		return strategy;
	}

	public boolean isOptional() {
		return optional;
	}

	public void setOptional(boolean optional) {
		this.optional = optional;
	}
}
