package upb.ida.util;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import upb.ida.bean.cluster.ClusterAlgoDesc;

@Component
public class DataDumpUtil {
	@Autowired
	@Qualifier("scktClstrDtDmp")
	private Map<String, ClusterAlgoDesc> scktClstrDtDmp;
	
	//TODO: Method to fetch the names of clustering algorithm
	//TODO: Method to fetch list of params in a clustering algorithm
}
