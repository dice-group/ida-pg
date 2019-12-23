package upb.ida.intent.model;

import java.util.List;

public class IntentResponseWrapper {

	private String text;
	private List<IntentClassification> intents;

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public List<IntentClassification> getIntents() {
		return intents;
	}

	public void setIntents(List<IntentClassification> intents) {
		this.intents = intents;
	}
}
