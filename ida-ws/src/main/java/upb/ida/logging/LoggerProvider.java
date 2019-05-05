package upb.ida.logging;

import javax.annotation.PostConstruct;

/**
 * Class to expose logger provider methods for Aspect Logger in IDA
 * 
 * @author Ayaz
 *
 */

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

import upb.ida.constant.IDALiteral;
import upb.ida.util.FileUtil;

import java.io.File;

@Component
public class LoggerProvider {

	@Autowired
	private FileUtil fileUtil;

	@PostConstruct
	public void setLogProps() {
		String log4jConfigFile = fileUtil.fetchSysFilePath(IDALiteral.ASPECTLOGGER_PROP_FILEPATH);
		PropertyConfigurator.configure(log4jConfigFile);
		new File("app-logs").mkdirs();
	}

	/**
	 * Method to get the logger instance for RequestResponseLogger from a
	 * log4j.properties file
	 * 
	 * @return - RequestResponseLogger
	 */
	@Bean
	@Scope(value = "singleton", proxyMode = ScopedProxyMode.TARGET_CLASS)
	@Qualifier("requestResponseLogger")
	public Logger getRequestResponseLogger() {

		return Logger.getLogger("RequestResponseLogger");
	}

	/**
	 * Method to get the logger instance for ExceptionLogger from a log4j.properties
	 * file
	 * 
	 * @return - ExceptionLogger
	 */
	@Bean
	@Scope(value = "singleton", proxyMode = ScopedProxyMode.TARGET_CLASS)
	@Qualifier("exceptionLogger")
	public Logger getExceptionLogger() {
		return Logger.getLogger("ExceptionLogger");
	}

	/**
	 * Method to get the logger instance for DatasetResponseLogger from a
	 * log4j.properties file
	 * 
	 * @return - DatasetResponseLogger
	 */
	@Bean
	@Scope(value = "singleton", proxyMode = ScopedProxyMode.TARGET_CLASS)
	@Qualifier("datasetResponseLogger")
	public Logger getDatasetReponseLogger() {
		return Logger.getLogger("DatasetResponseLogger");
	}
}
