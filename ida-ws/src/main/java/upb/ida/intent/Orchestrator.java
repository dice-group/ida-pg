package upb.ida.intent;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.annotation.SessionScope;

import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SessionScope
@Component
public class Orchestrator {
	// Externalize
	String restApiUrl = "http://127.0.0.1:5000/classify?text={text}";
	double confidenceThreshold = 0.7;

	private ChatbotContext context = new ChatbotContext();

	//TODO make this singleton
	public Orchestrator() {
	}

	private void prepareContext(String userMessage)
	{
		context.setCurrentUserMessage(userMessage);
		context.clearChatbotResponses();
		context.clearTips();
	}

	public ChatbotContext processMessage(String message) throws IOException, ParseException {
		System.out.println("IC: message = " + message);
		this.prepareContext(message);
		Intent messageIntent = getMessageIntent(message);
		System.out.println("IC: message intent = " + messageIntent.toString());


		if (messageIntent.equals(Intent.RESTART)) {
			context.resetContext();
			return context;
		}

		if (context.getCurrentIntent().equals(Intent.UNKNOWN) || messageIntent.equals(Intent.BAR)) {
			System.out.println("IC: new intent = " + messageIntent);
			context.setCurrentIntent(messageIntent);
			context.setCurrentExecutor(IntentExecutorFactory.getExecutorFor(messageIntent));
		}

		System.out.println("IC: current intent = " + context.getCurrentIntent());
		if(!context.getCurrentIntent().equals(Intent.BAR))
			return null;

		if (context.getCurrentIntent().equals(Intent.UNKNOWN)) {


		} else {
			IntentExecutor executor = context.getCurrentExecutor();
			executor.processResponse(context);

			if (executor.needsMoreInformation(context)) {
				Question nextQuestion = executor.getNextQuestion(context);
				context.addChatbotResponse(nextQuestion.getQuestion());
				context.setActiveQuestion(nextQuestion);
			} else {
				// Perform final action
				context.addChatbotResponse("All information processed.");
				executor.execute(context);
			}
		}

		System.out.println("IC: responses = " + context.getChatbotResponses());
		return context;
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

	public static void main(String[] args) throws IOException, ParseException {
//		Orchestrator o = new Orchestrator();

//		o.processMessage("bar graph");
//		System.out.println("Chatbot: " + o.context.getChatbotResponses());
//
//		o.processMessage("soldier");
//		System.out.println("Chatbot: " + o.context.getChatbotResponses());
//
//		o.processMessage("regiment");
//		System.out.println("Chatbot: " + o.context.getChatbotResponses());
//
//		o.processMessage("last 6");
//		System.out.println("Chatbot: " + o.context.getChatbotResponses());

//		ChatbotContext ctx = new ChatbotContext();
//		ctx.setCurrentUserMessage("last 60");
//		System.out.println(AnswerHandlingStrategy.BAR_CHART_SUBSET.extractAnswer(ctx));
//		ctx.setCurrentUserMessage("I want the last 60");
//		System.out.println(AnswerHandlingStrategy.BAR_CHART_SUBSET.extractAnswer(ctx));
//		ctx.setCurrentUserMessage("first 60 rows");
//		System.out.println(AnswerHandlingStrategy.BAR_CHART_SUBSET.extractAnswer(ctx));
//		ctx.setCurrentUserMessage("gimme first 60rows");
//		System.out.println(AnswerHandlingStrategy.BAR_CHART_SUBSET.extractAnswer(ctx));
//		ctx.setCurrentUserMessage("gimme first 60 rows");
//		System.out.println(AnswerHandlingStrategy.BAR_CHART_SUBSET.extractAnswer(ctx));


	}

}
