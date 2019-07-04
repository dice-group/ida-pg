package upb.ida.provider;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
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

import clojure.lang.PersistentVector;
import librarian.model.Scrape;
import upb.ida.bean.cluster.ClusterAlgoDesc;
import upb.ida.bean.cluster.ClusterParam;
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
	public Scrape scrape;

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
		Map<String, ClusterAlgoDesc> res = new HashMap<>();

		res.put("k_means", queryAlgoDesc(scrape,
				PersistentVector.create("sklearn.cluster", "k_means")));
		res.put("affinity_propagation", queryAlgoDesc(scrape,
				PersistentVector.create("sklearn.cluster", "affinity_propagation")));

		return res;
	}

	/**
	 * Queries a scrape for a given fully qualified class name and its properties.
	 * @param scrape - The scrape that should be queried.
	 * @param fqn - The fully qualified name of the queried class.
	 * @return A basic description of the queried class.
	 */
	@SuppressWarnings("unchecked")
	private ClusterAlgoDesc queryAlgoDesc(Scrape scrape, PersistentVector fqn) {
		String q = ""
				+ ":find ?fn ?name (distinct ?d) (distinct ?param) "
				+ ":in $ ?id :where "
				+ "[?fn :function/id ?id] [?fn :function/name ?name] "
				+ "[?fn :function/datatype ?st] [?st :type :semantic-type] "
				+ "[?st :semantic-type/key \"description\"] "
				+ "[?st :semantic-type/value ?desc] [?st :semantic-type/position ?dp] "
				+ "[(vector ?dp ?desc) ?d] "
				+ "[?fn :function/parameter ?param]";

		List<List<Object>> res = scrape.query("[" + q + "]", Arrays.asList(fqn));

		List<Object> data = res.get(0);
		Integer id = (Integer) data.get(0);
		String name = (String) data.get(1);
		Collection<PersistentVector> descs = (Collection<PersistentVector>) data.get(2);
		Collection<Long> paramIds = (Collection<Long>) data.get(3);
		List<String> sortedDescs = descs.stream()
				.sorted((a, b) -> (int)((Long) a.get(0) - (Long) b.get(0)))
				.map(d -> (String) d.get(1))
				.collect(Collectors.toList());
		List<ClusterParam> params = paramIds.stream()
				.map(pid -> queryClusterParam(scrape, pid))
				.filter(p -> p != null)
				.sorted((a, b) -> a.getPosition() - b.getPosition())
				.collect(Collectors.toList());

		return new ClusterAlgoDesc(id.intValue(), name,
		 		sortedDescs.get(0),
				String.join("\n", sortedDescs), params);
	}

	private ClusterParam queryClusterParam(Scrape scrape, Long pid) {
		String q = ""
				+ ":find ?name ?optional ?tname ?position (max ?desc) "
				+ ":in $ ?param :where "
				+ "[?param :parameter/name ?name] "
				+ "[(not= ?name \"X\")]"
				+ "[(get-else $ ?param :parameter/optional false) ?optional] "
				+ "[(get-else $ ?param :parameter/position -1) ?position] "
				+ "[?param :parameter/datatype ?basetype] "
				+ "[(get-else $ ?basetype :basetype/name \"\") ?tname] "
				+ "[?param :parameter/datatype ?desctype] "
				+ "(or-join [?desctype ?desc]"
				+ "  (and [?desctype :semantic-type/key \"description\"]"
				+ "       [?desctype :semantic-type/position 0]"
				+ "       [?desctype :semantic-type/value ?desc])"
				+ "  (and [?desctype] [(ground \"\") ?desc]))";

		@SuppressWarnings("unchecked")
		List<List<Object>> res = scrape.query("[" + q + "]", Arrays.asList(pid));
		
		if(res.size() == 0)
			return null;

		List<Object> data = res.get(0);
		String name = (String) data.get(0);
		Boolean optional = (Boolean) data.get(1);
		String type = (String) data.get(2);
		Long position = (Long) data.get(3);
		String desc = (String) data.get(4);

		return new ClusterParam(name, Arrays.asList(type), optional.booleanValue(), position.intValue(), desc);
	}

}
