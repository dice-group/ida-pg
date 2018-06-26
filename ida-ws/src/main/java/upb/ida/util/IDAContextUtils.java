package upb.ida.util;

import javax.servlet.http.HttpSession;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
public class IDAContextUtils {
	public static HttpSession session() {
	    ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
	    return attr.getRequest().getSession(true); // true == allow create
	}
	
	public static Object fetchFromSession(String key) {
		Object obj = session().getAttribute(key);
		return obj;
	}
	
	public static void saveToSession(String key, Object obj) {
		session().setAttribute(key, obj);
	}
}
