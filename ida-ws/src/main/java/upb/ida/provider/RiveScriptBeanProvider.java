package upb.ida.provider;

import javax.servlet.ServletContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

import com.rivescript.RiveScript;
import upb.ida.constant.IDALiteral;
import upb.ida.temp.ExampleMacro;

@Component
public class RiveScriptBeanProvider {

	@Autowired
	private ServletContext context;
	@Autowired
	LoadDataContent loadDataContent;
	@Autowired
	FdgHandler FdgHandler; 
	@Autowired
	BgdHandler BgdHandler; 

	@Bean
	@Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
	@Qualifier("sessionBotInstance")
	private RiveScript initBotInstance() {

		RiveScript bot = new RiveScript();

		// Load an individual file.
		bot.loadFile(context.getRealPath(IDALiteral.RS_FILEPATH));

		// Sort the replies after loading them!
		bot.sortReplies();
		bot.setSubroutine("sayname", new ExampleMacro());
		bot.setSubroutine("loadDataset", loadDataContent);
		bot.setSubroutine("FdgHandler", FdgHandler);
		bot.setSubroutine("BgdHandler", BgdHandler);
		return bot;
	}
	
}
