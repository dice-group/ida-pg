package upb.ida.bean;

import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;


/**
 * SessionValues is used to store values coming from NLP engine
 *
 */
@Component
@SessionScope
public class SessionValues {
	
	private Map<String, Object> data;
	
	public Map<String, Object> getData() {
		return data;
	}
	/**
	 * Sets the {@link SessionValues#data}
	 */
	public void setData(Map<String, Object> data) {
		this.data = data;
	}
		
}
