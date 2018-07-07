package upb.ida.provider;

import java.util.HashMap;
import java.util.Map;

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
	private LoadDataContent loadDataContent;
	@Autowired
	private FdgHandler FdgHandler;
	@Autowired
	private BgdHandler BgdHandler;
	@Autowired
	private ClusterConHandler ClusterConHandler;
	@Autowired
	private ParamsHandler ParamsHandler;
	@Autowired
	private UserParamEntry UserParamEntry;
	@Autowired
	private	UserParamValueCollector UserParamValueCollector;
	@Bean
	@Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
	@Qualifier("sessionBotInstance")
	public RiveScript initBotInstance() {

		RiveScript bot = new RiveScript();

		// Load the Rivescript directory.
		bot.loadDirectory(context.getRealPath(IDALiteral.RS_DIRPATH));

		// Sort the replies and set Subroutine calls for designated functionality 
		bot.sortReplies();
		bot.setSubroutine("sayname", new ExampleMacro());
		bot.setSubroutine("loadDataset", loadDataContent);
		bot.setSubroutine("FdgHandler", FdgHandler);
		bot.setSubroutine("BgdHandler", BgdHandler);
		bot.setSubroutine("ClusterConHandler", ClusterConHandler);
		bot.setSubroutine("ParamsHandler", ParamsHandler);
		bot.setSubroutine("UserParamEntry", UserParamEntry);
		bot.setSubroutine("UserParamValueCollector", UserParamValueCollector);
		
		return bot;
	}
	
	@Bean
	@Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
	@Qualifier("riveSessionMap")
	public Map<String,Object> riveSessionMap() {
		Map<String,Object> sessionMap = new HashMap<>();
		return sessionMap;
	}

}
