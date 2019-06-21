package upb.ida.provider;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.ServletContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import librarian.model.Scrape;
import upb.ida.bean.cluster.ClusterAlgoDesc;
import upb.ida.bean.cluster.ClusterParam;
import upb.ida.util.FileUtil;
/**
 * Beans Provider for Scikit datadump
 * @author Nikit
 *
 */
@Component
public class DataDumpProvider {
	@Autowired
	public ServletContext context;
	@Autowired
	public FileUtil demoMain;
	public static final String DATADUMP_PATH = "./libs/scikit-learn-cluster";
	
	/**
	 * Method to generate a map of clustering algorithms in the scrape database
	 * @return map of clustering algorithms
	 * @throws JsonGenerationException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	@Bean
	@Scope(value = "singleton", proxyMode = ScopedProxyMode.TARGET_CLASS)
	@Qualifier("scktClstrDtDmp")
	public Map<String, ClusterAlgoDesc> getScktClstrDtDmp() {
		File scrapeFile = new File(demoMain.fetchSysFilePath(DATADUMP_PATH));
		Scrape scrape = Scrape.load(scrapeFile);
				
		Map<String, ClusterAlgoDesc> res = new HashMap<>();
		
		res.put("KMeans", queryAlgoDesc(scrape, "sklearn.cluster.KMeans"));
		res.put("AffinityPropagation", queryAlgoDesc(scrape, "sklearn.cluster.AffinityPropagation"));
		
		return res;
	}
	
	/**
	 * Queries a scrape for a given fully qualified class name and its properties.
	 * @param scrape - The scrape that should be queried.
	 * @param fqn - The fully qualified name of the queried class.
	 * @return A basic description of the queried class.
	 */
	@SuppressWarnings("unchecked")
	private ClusterAlgoDesc queryAlgoDesc(Scrape scrape, String fqn) {
		String q = ":find ?class ?name ?summary (distinct ?desc) (distinct ?param)"
				+ ":in $ ?id :where "
				+ "[?class :class/id ?id] [?class :class/name ?name] "
				+ "[?class :description-summary ?summary] [?class :description ?desc] "
				+ "[?class :class/constructor ?ctr] [?ctr :constructor/parameter ?param]";
		
		List<List<Object>> res = scrape.query("[" + q + "]", Arrays.asList(fqn));
		
		List<Object> data = res.get(0);
		Long id = (Long) data.get(0);
		String name = (String) data.get(1);
		String summary = (String) data.get(2);
		List<String> descs = (List<String>) data.get(3);
		List<Long> paramIds = (List<Long>) data.get(4);
		List<ClusterParam> params = paramIds.stream()
				.map(pid -> queryClusterParam(scrape, pid))
				.collect(Collectors.toList());
		
		return new ClusterAlgoDesc(id.intValue(), name, summary, String.join("\n", descs), params);
	}
	
	private ClusterParam queryClusterParam(Scrape scrape, Long pid) {
		String q = ":find ?name ?optional ?tname "
				+ ":in $ ?param :where "
				+ "[?param :parameter/name ?name] "
				+ "[(get-else $ ?param :parameter/optional false) ?optional] "
				+ "[(get-else $ ?param :parameter/datatype -1) ?type] "
				+ "[(get-else $ ?type :datatype/name \"\") ?tname]";
		
		@SuppressWarnings("unchecked")
		List<List<Object>> res = scrape.query("[" + q + "]", Arrays.asList(pid));
		
		List<Object> data = res.get(0);
		String name = (String) data.get(0);
		Boolean optional = (Boolean) data.get(1);
		String type = (String) data.get(2);
		
		return new ClusterParam(name, Arrays.asList(type), optional.booleanValue());
	}

}
