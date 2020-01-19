package upb.ida.intent.model;

import upb.ida.bean.ResponseBean;
import upb.ida.dao.DataRepository;
import upb.ida.intent.IntentExecutorFactoryHelper;
import upb.ida.intent.exception.IntentException;
import upb.ida.intent.executor.IntentExecutor;
import upb.ida.util.BeanUtil;

import java.util.*;

public class ChatbotContext {
	private Intent currentIntent;
	private IntentExecutor currentExecutor;
	private Map<String, String> savedAnswers;
	private List<String> chatbotResponses;
	private List<String> tips;
	private Question activeQuestion;
	private String currentMessage;
	private boolean resetOnNextRequest;
	private DataRepository dao;

	public ChatbotContext() throws IntentException {
		resetContext();
	}

	public void resetContext() throws IntentException {
		// Set to defaults
		this.currentIntent = Intent.GREETING;
		this.currentExecutor = IntentExecutorFactoryHelper.getExecutorFor(Intent.GREETING);
		this.savedAnswers = new HashMap<>();
		this.chatbotResponses = new ArrayList<>();
		this.chatbotResponses.add("Hello! How may I help you?");
		this.tips = new ArrayList<>();
		this.activeQuestion = null;
		this.currentMessage = null;
		this.resetOnNextRequest = false;
		this.dao = new DataRepository(false);
	}

	public String clearUserMessage() {
		return currentMessage = null;
	}

	public void addTip(String tip) {
		tips.add(tip);
	}

	public void clearTips() {
		tips = new ArrayList<String>();
	}

	public Intent getCurrentIntent() {
		return currentIntent;
	}

	public void setCurrentIntent(Intent currentIntent) {
		this.currentIntent = currentIntent;
	}

	public IntentExecutor getCurrentExecutor() {
		return currentExecutor;
	}

	public void setCurrentExecutor(IntentExecutor currentExecutor) {
		this.currentExecutor = currentExecutor;
	}

	public Map<String, String> getSavedAnswers() {
		return savedAnswers;
	}

	public void setSavedAnswers(Map<String, String> savedAnswers) {
		this.savedAnswers = savedAnswers;
	}

	public void setChatbotResponses(List<String> chatbotResponses) {
		this.chatbotResponses = chatbotResponses;
	}

	public void clearChatbotResponses() {
		this.chatbotResponses = new ArrayList<>();
	}

	public List<String> getChatbotResponses() {
		return chatbotResponses;
	}

	public void addChatbotResponse(String response) {
		chatbotResponses.add(response);
	}

	public List<String> getTips() {
		return tips;
	}

	public void setTips(List<String> tips) {
		this.tips = tips;
	}

	public Question getActiveQuestion() {
		return activeQuestion;
	}

	public void setActiveQuestion(Question activeQuestion) {
		this.activeQuestion = activeQuestion;
	}

	public String getCurrentMessage() {
		return currentMessage == null ? null : currentMessage.trim();
	}

	public void setCurrentMessage(String currentMessage) {
		this.currentMessage = currentMessage;
	}

	public boolean isResetOnNextRequest() {
		return resetOnNextRequest;
	}

	public void setResetOnNextRequest(boolean resetOnNextRequest) {
		this.resetOnNextRequest = resetOnNextRequest;
	}

	public Set<String> getActiveTableColumns() {
		ResponseBean responseBean = BeanUtil.getBean(ResponseBean.class);
		String actvTbl = (String) responseBean.getPayload().get("actvTbl");
		String actvDs = (String) responseBean.getPayload().get("actvDs");

		return dao.getColumnsList(actvTbl, actvDs);
	}

	public void resetOnNextRequest() {
		this.resetOnNextRequest = true;
	}


}
