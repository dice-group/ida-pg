package upb.ida.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.rivescript.RiveScript;

import upb.ida.constant.IDALiteral;

@Service
public class RiveScriptService {
	
	@Autowired
	@Qualifier("sessionBotInstance")
	private RiveScript sessionBotInstance;

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
