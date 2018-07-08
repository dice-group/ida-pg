package upb.ida.util;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;
@SessionScope
@Component
public class SessionUtil {
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
