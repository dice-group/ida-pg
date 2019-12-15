package upb.ida.intent;

import java.util.*;

public class ChatbotContext {
	private Map<String, Object> contextMap;

	public ChatbotContext() {
		resetContext();
	}

	public void resetContext() {
		// Set to defaults
		contextMap = new HashMap<>();
		contextMap.put("currentIntent", Intent.UNKNOWN);
		contextMap.put("currentExecutor", IntentExecutorFactory.getExecutorFor(Intent.UNKNOWN));
		contextMap.put("savedAnswers", new HashMap<String, String>());
		contextMap.put("chatbotResponses", new ArrayList<String>());
		contextMap.put("tips", new ArrayList<String>());
	}

	public void clearChatbotResponses() {
		contextMap.put("chatbotResponses", new ArrayList<>());
	}

	public void setActiveQuestion(Question question) {
		contextMap.put("activeQuestion", question);
	}

	public void setCurrentUserMessage(String message) {
		contextMap.put("currentMessage", message);
	}

	public String getCurrentUserMessage() {
		return ((String) contextMap.get("currentMessage")).trim();
	}

	public String clearUserMessage() {
		return (String) contextMap.remove("currentMessage");
	}

	public IntentExecutor getCurrentExecutor() {
		return (IntentExecutor) contextMap.get("currentExecutor");
	}

	public void setCurrentExecutor(IntentExecutor executor) {
		contextMap.put("currentExecutor", executor);
	}

	public Intent getCurrentIntent() {
		return (Intent) contextMap.get("currentIntent");
	}

	public void setCurrentIntent(Intent intent) {
		contextMap.put("currentIntent", intent);
	}

	public List<String> getChatbotResponses() {
		return (List<String>) contextMap.get("chatbotResponses");
	}

	public Map<String, String> getSavedAnswers() {
		return (Map<String, String>) contextMap.get("savedAnswers");
	}

	public Question getActiveQuestion() {
		return (Question) contextMap.get("activeQuestion");
	}

	public void addChatbotResponse(String response) {
		List<String> chatbotResponses = (List<String>) contextMap.get("chatbotResponses");
		chatbotResponses.add(response);
	}

	public void addTip(String tip) {
		((List<String>) contextMap.get("tips")).add(tip);
	}

	public List<String> getTips() {
		return (List<String>) contextMap.get("tips");
	}

	public void clearTips() {
		contextMap.put("tips", new ArrayList<String>());
	}

	public List<String> getActiveTableColumns() {
		return Arrays.asList("City", "Cappuccino", "Cinema", "Wine", "Gasoline", "Avg Rent", "Avg Disposable Income");
//		return (List<String>) contextMap.get("activeTableColumns");
	}
}
