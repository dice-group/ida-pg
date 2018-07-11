package upb.ida.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.rivescript.RiveScript;

import upb.ida.constant.IDALiteral;

/**
 * Service class to expose the communication to RiveScript
 * 
 * @author Nikit
 *
 */
@Service
public class RiveScriptService {
	/**
	 * Session scoped instance of the RiveScript chatbot
	 */
	@Autowired
	@Qualifier("sessionBotInstance")
	private RiveScript sessionBotInstance;

	/**
	 * Method to fetch the Rivescript response for the given query
	 * 
	 * @param query - natural language query
	 * @return - natural language response
	 */
	public String getRSResponse(String query) {
		String resp = null;
		// Get bot instance
		RiveScript bot = sessionBotInstance;
		// Get RiveScript reply
		resp = bot.reply(IDALiteral.RS_USER, query);
		// Return reply
		return resp;
	}
}
