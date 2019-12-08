package upb.ida.intent;

public class Question {
	private String question;
	private String questionKey;
	private String feedbackText;
	private HandlingStrategy strategy;
	private String answer;

	public enum HandlingStrategy {
		ACTIVE_TABLE_COLUMNS, FIXED_SET
	}

	public Question(String question, String questionKey, String feedbackText, HandlingStrategy strategy) {
		this.question = question;
		this.questionKey = questionKey;
		this.feedbackText = feedbackText;
		this.strategy = strategy;
	}

	public String getQuestion() {
		return question;
	}

	public void setQuestion(String question) {
		this.question = question;
	}

	public String getQuestionKey() {
		return questionKey;
	}

	public void setQuestionKey(String questionKey) {
		this.questionKey = questionKey;
	}

	public String getFeedbackText() {
		return feedbackText;
	}

	public void setFeedbackText(String feedbackText) {
		this.feedbackText = feedbackText;
	}

	public void setStrategy(HandlingStrategy strategy) {
		this.strategy = strategy;
	}

	public String getAnswer() {
		return answer;
	}

	public void setAnswer(String answer) {
		this.answer = answer;
	}

}
