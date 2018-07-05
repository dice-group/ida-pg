package upb.ida.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import upb.ida.bean.cluster.ClusterAlgoDesc;
import upb.ida.bean.cluster.ClusterParam;

@Component
public class DataDumpUtil {
	@Autowired
	@Qualifier("scktClstrDtDmp")
	private Map<String, ClusterAlgoDesc> scktClstrDtDmp;

	// Method to fetch the names of clustering algorithm
	public List<String> getClusteringAlgoNames() {
		List<String> resList = new ArrayList<>();
		for (ClusterAlgoDesc entry : scktClstrDtDmp.values()) {
			resList.add(entry.getFnDesc());
		}
		return resList;
	}

	// Method to fetch list of params in a clustering algorithm
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
