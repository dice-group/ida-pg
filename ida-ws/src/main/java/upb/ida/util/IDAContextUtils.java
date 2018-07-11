package upb.ida.util;

import javax.servlet.http.HttpSession;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Exposes util methods for http session scoped Map
 * 
 * @author Nikit
 *
 */
public class IDAContextUtils {
	/**
	 * Method to fetch the HttpSession instance
	 * 
	 * @return - httpsession instance
	 */
	public static HttpSession session() {
		ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
		return attr.getRequest().getSession(true); // true == allow create
	}
	/**
	 * Method to fetch the value from session map for a given key
	 * @param key - key for which value is to be fetched
	 * @return - value of the key in the session map
	 */
	public static Object fetchFromSession(String key) {
		Object obj = session().getAttribute(key);
		return obj;
	}
	/**
	 * Method to save a value is the session map
	 * @param key - key for the value to be saved
	 * @param obj - value to be saved
	 */
	public static void saveToSession(String key, Object obj) {
		session().setAttribute(key, obj);
	}
}
