package upb.ida.intent;

import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Orchestrator {
	// Externalize
	String restApiUrl = "http://127.0.0.1:5000/classify?text={text}";
	double confidenceThreshold = 0.7;

	private Map<String, Object> context = new HashMap<>();

	//TODO make this singleton
	public Orchestrator() {
		resetContext();
	}

	public List<String> getChatbotResponses() {
		return (List<String>) context.get("chatbotResponses");
	}

	public Map<String, Object> processMessage(String message) {
		this.clearChatbotResponses(); // Move to other method which is called before processMessage
		System.out.println("User: " + message);
		Intent messageIntent = getMessageIntent(message);

		if (messageIntent.equals(Intent.RESTART)) {
			this.resetContext();
			return context;
		}

		if (this.getCurrentIntent().equals(Intent.UNKNOWN)) {
			System.out.println(messageIntent);
			this.setCurrentIntent(messageIntent);
			this.setCurrentExecutor(IntentExecutorFactory.getExecutorFor(messageIntent));
		}

		if (this.getCurrentIntent().equals(Intent.UNKNOWN)) {


		} else {
			IntentExecutor executor = this.getCurrentExecutor();
			executor.processResponse(message, context);

			if (executor.needsMoreInformation(context)) {
				Question nextQuestion = executor.getNextQuestion(context);
				this.addChatbotResponse(nextQuestion.getQuestion());
				this.setActiveQuestion(nextQuestion);
			} else {
				// Perform final action
				this.addChatbotResponse("All information processed");
			}


		}


		return context;
	}

	private void clearChatbotResponses() {
		context.remove("chatbotResponses");
	}

	private void addChatbotResponse(String response) {
		List<String> chatbotResponses = (List<String>) context.get("chatbotResponses");
		if (chatbotResponses == null) {
			chatbotResponses = new ArrayList<>();
			context.put("chatbotResponses", chatbotResponses);
		}
		chatbotResponses.add(response);
	}

	private void setActiveQuestion(Question question) {
		context.put("activeQuestion", question);
	}

	private void resetContext() {
		// Set to defaults
		context = new HashMap<>();
		context.put("currentIntent", Intent.UNKNOWN);
		context.put("currentExecutor", IntentExecutorFactory.getExecutorFor(Intent.UNKNOWN));
		context.put("savedAnswers", new HashMap<String, String>());
	}

	private IntentExecutor getCurrentExecutor() {
		return (IntentExecutor) context.get("currentExecutor");
	}

	private void setCurrentExecutor(IntentExecutor executor) {
		context.put("currentExecutor", executor);
	}

	private Intent getCurrentIntent() {
		return (Intent) context.get("currentIntent");
	}

	private void setCurrentIntent(Intent intent) {
		context.put("currentIntent", intent);
	}

	private Intent getMessageIntent(String message) {

		Map<String, String> params = new HashMap<String, String>();
		params.put("text", message);

		RestTemplate restTemplate = new RestTemplate();

		IntentResponseWrapper result = restTemplate.getForObject(restApiUrl, IntentResponseWrapper.class, params);
		List<IntentClassification> classifiedIntents = result.getIntents();

		boolean ambiguous = classifiedIntents.isEmpty()
				|| classifiedIntents.stream().filter(intentClassification -> intentClassification.getScore() > confidenceThreshold).count() != 1;

		if (ambiguous)
			return Intent.UNKNOWN;

		return Intent.getForKey(classifiedIntents.get(0).getIntent());
	}

	public static void main(String[] args) {
		Orchestrator o = new Orchestrator();

		o.processMessage("bar graph");
		System.out.println("Chatbot: " + o.getChatbotResponses());

		o.processMessage("soldier");
		System.out.println("Chatbot: " + o.getChatbotResponses());

		o.processMessage("regiment");
		System.out.println("Chatbot: " + o.getChatbotResponses());


	}

}
