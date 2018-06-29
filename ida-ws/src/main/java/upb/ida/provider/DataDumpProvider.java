package upb.ida.provider;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;

import javax.servlet.ServletContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.node.ArrayNode;

import upb.ida.bean.cluster.ClusterAlgoDesc;
@Component
public class DataDumpProvider {
	@Autowired
	public ServletContext context;
	@Bean
	@Scope(value = "singleton", proxyMode = ScopedProxyMode.TARGET_CLASS)
	@Qualifier("scktClstrDtDmp")
	public Map<String, ClusterAlgoDesc> getScktClstrDtDmp() throws JsonGenerationException, JsonMappingException, IOException{
		Map<String, ClusterAlgoDesc> resMap = null;
		ObjectMapper mapper = new ObjectMapper();
		//Read datadump json
		File clstrDd = new File(context.getRealPath("./scikit/datadump/cluster_datadump.txt"));
		ArrayNode nodeArr1 = mapper.createArrayNode();
		ObjectReader reader = mapper.reader();
		nodeArr1 = (ArrayNode) reader.readTree(new FileInputStream(clstrDd));
		System.out.println("Hello");
		//Create Json Array
		//Loop through array and create ClusterAlgoDesc instance
		//select fnName
		//select fnDesc
		//select note
		//select params
		// add to map
		return resMap;
	}

}
