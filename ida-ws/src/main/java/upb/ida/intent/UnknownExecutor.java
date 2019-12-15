package upb.ida.intent;

public class UnknownExecutor extends AbstractIntentExecutor implements IntentExecutor {

	public UnknownExecutor() {
		super(null);
	}

	@Override
	public Question getNextQuestion(ChatbotContext context) {
		return null;
	}

	@Override
	public void execute(ChatbotContext context) {

	}

	@Override
	public boolean needsMoreInformation(ChatbotContext context) {
		return false;
	}

	@Override
	public void processResponse(ChatbotContext context) {
	}
}
