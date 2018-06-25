package upb.ida.service;

import javax.servlet.ServletContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rivescript.RiveScript;

import upb.ida.constant.IDALiteral;
import upb.ida.temp.ExampleMacro;
import upb.ida.util.IDAContextUtils;

@Service
public class RiveScriptService {
	@Autowired
	private ServletContext context;
	public RiveScript getBotInstance() {
		RiveScript bot = null;
		//Load instance from http session
		bot = (RiveScript) IDAContextUtils.fetchFromSession(IDALiteral.RS_INSTANCE);
		//If instance not found
		if(bot==null) {
			//Initiate new instance 
			bot = initBotInstance();
			IDAContextUtils.saveToSession(IDALiteral.RS_INSTANCE, bot);
			//Load it on http session 
		}
	
		return bot;
	}
	public String getRSResponse(String query) {
		String resp = null;
		//Get bot instance 
		RiveScript bot = getBotInstance();
		//Get RiveScript reply 
		resp = bot.reply(IDALiteral.RS_USER, query);
		//Return reply
		return resp;
		}
	private RiveScript initBotInstance() {
	
		RiveScript bot = new RiveScript();
		
		// Load an individual file.
		bot.loadFile(context.getRealPath(IDALiteral.RS_FILEPATH));
		
		// Sort the replies after loading them!
		bot.sortReplies();
		bot.setSubroutine(IDALiteral.RS_LOADDATA_ROUTINE, new ExampleMacro());
	
		return bot;
	}
}
