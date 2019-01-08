package upb.ida.provider;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

import com.rivescript.Config;
import com.rivescript.RiveScript;

import upb.ida.constant.IDALiteral;
import upb.ida.temp.ExampleMacro;
import upb.ida.util.FileUtil;


/**
 * Beans provider for the rivescript bot instance
 * 
 */

@Component
public class RiveScriptBeanProvider {

	@Autowired
	private LoadDataContent loadDataContent;
	@Autowired
	private FdgHandler fdgHandler;
	@Autowired
	private BgdHandler bgdHandler;
	@Autowired
	private ClusterConHandler clusterConHandler;
	@Autowired
	private ParamsHandler paramsHandler;
	@Autowired
	private UserParamEntry userParamEntry;
	@Autowired
	private UserParamValueCollector userParamValueCollector;
	@Autowired
	private ClusterDataGetter clusterDataGetter;
	@Autowired
	private CheckParamCollected checkParamCollected;
	@Autowired
	private FileUtil demoMain;
	@Autowired
	private LoadDsMetadata dsmdLoader;
	@Autowired
	private VennDiagramHandler VennDiagramHandler;
	/**
	 * Method to provide a session scoped bean for the RiveScript bot
	 * @return - RiveScript Instance
	 */
	@Bean
	@Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
	@Qualifier("sessionBotInstance")
	public RiveScript initBotInstance() {

		RiveScript bot = new RiveScript(Config.utf8());

		// Load the Rivescript directory.
		bot.loadDirectory(demoMain.fetchSysFilePath(IDALiteral.RS_DIRPATH));

		// Sort the replies and set Subroutine calls for designated functionality
		bot.sortReplies();
		bot.setSubroutine("sayname", new ExampleMacro());
		bot.setSubroutine("loadDataset", loadDataContent);
		bot.setSubroutine("FdgHandler", fdgHandler);
		bot.setSubroutine("BgdHandler", bgdHandler);
		bot.setSubroutine("ClusterConHandler", clusterConHandler);
		bot.setSubroutine("ParamsHandler", paramsHandler);
		bot.setSubroutine("UserParamEntry", userParamEntry);
		bot.setSubroutine("UserParamValueCollector", userParamValueCollector);
		bot.setSubroutine("ClusterDataGetter", clusterDataGetter);
		bot.setSubroutine("CheckParamCollected", checkParamCollected);
		bot.setSubroutine("LoadDsMetadata", dsmdLoader);
		bot.setSubroutine("VennDiagramHandler", VennDiagramHandler);
		return bot;
	}

	

}
