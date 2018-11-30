package upb.ida.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import upb.ida.bean.cluster.ClusterAlgoDesc;
import upb.ida.bean.cluster.ClusterParam;

/**
 * Exposes util methods for scikit datadump
 * 
 * @author Nikit
 *
 */
@Component
public class DataDumpUtil {
	@Autowired
	@Qualifier("scktClstrDtDmp")
	private Map<String, ClusterAlgoDesc> scktClstrDtDmp;
	@Autowired
	private SessionUtil sessionUtil;

	/**
	 *  Method to fetch the names of clustering algorithm
	 * @return - names of clustering algorithms
	 */
	public List<String> getClusteringAlgoNames() {
		List<String> resList = new ArrayList<>();
		for (ClusterAlgoDesc entry : scktClstrDtDmp.values()) {
			resList.add(entry.getFnName());
		}
		return resList;
	}

	/** Method to fetch list of params in a clustering algorithm
	 * 
	 * @param algoName - name of the algorithm for which params are to be fetched
	 * @return - List of parameters
	 */
	public List<ClusterParam> getClusterAlgoParams(String algoName) {
		List<ClusterParam> resList = null;
		String trmdName = algoName.trim();
		for (ClusterAlgoDesc entry : scktClstrDtDmp.values()) {
			if (entry.getFnName().equalsIgnoreCase(trmdName) || entry.getFnDesc().equalsIgnoreCase(trmdName)) {
				resList = entry.getParams();
				break;
			}
		}
		return resList;
	}
}
