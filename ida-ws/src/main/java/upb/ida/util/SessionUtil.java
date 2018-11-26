package upb.ida.util;

import java.util.HashMap;

import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

/**
 * SessionUtil contians sessionMap
 * 
 * @author Faisal
 *
 */
@SessionScope
@Component
public class SessionUtil {
	
	/**
	 * Method to return a session scoped Map
	 * @return - Map
	 */
	private Map<String,Object> sessionMap;
	
	public SessionUtil() {
		sessionMap = new HashMap<>();
	}
	public Map<String, Object> getSessionMap() {
		return sessionMap;
	}
	public void setSessionMap(Map<String, Object> sessionMap) {
		this.sessionMap = sessionMap;
	}
	
}
