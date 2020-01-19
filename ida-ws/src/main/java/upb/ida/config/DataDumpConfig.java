package upb.ida.config;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import librarian.model.Scrape;
import upb.ida.util.FileUtil;

@Configuration
public class DataDumpConfig {
	@Autowired
	public FileUtil demoMain;
	public static final String DATADUMP_PATH = "./libs/scikit-learn-cluster";
	
	@Bean
	public Scrape scrape() {
		File scrapeFile = new File(demoMain.fetchSysFilePath(DATADUMP_PATH));
		
		return Scrape.load(scrapeFile);
	}
}
