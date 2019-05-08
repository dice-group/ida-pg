package upb.ida.test;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import upb.ida.Application;
import upb.ida.bean.cluster.ClusterParam;
import upb.ida.bean.cluster.ParamEntryChecker;
import upb.ida.util.DataDumpUtil;
import upb.ida.util.GetCorrectParamTypes;

@RunWith(SpringRunner.class)
@WebMvcTest
@ContextConfiguration(classes = { Application.class })
public class GetCorrectParamTypesTest {
	

	
	@Autowired
	private DataDumpUtil DataDumpUtil;
	

	@Test
	public void correctTypeValuesTest() {
		List<String> paramList = new ArrayList<>();
		paramList.add("n_init");
		paramList.add("n_clusters");
		paramList.add("init");
		paramList.add("n_jobs");

		Map<String, Object> paramMap = new HashMap<String, Object>();
		ParamEntryChecker entry = new ParamEntryChecker("n_init", "10", true, paramList);
		ParamEntryChecker entry1 = new ParamEntryChecker("n_clusters", "5", true, paramList);
		ParamEntryChecker entry2 = new ParamEntryChecker("init", "random", true, paramList);
		ParamEntryChecker entry3 = new ParamEntryChecker("n_jobs", "8", true, paramList);

		paramMap.put("n_init", entry);
		paramMap.put("n_clusters", entry1);
		paramMap.put("init", entry2);
		paramMap.put("n_jobs", entry3);
		String algoName = "KMeans_TEST";

		List<ClusterParam> resList = DataDumpUtil.getClusterAlgoParams("KMeans_TEST");

		HashMap<String, Object> mMap = new HashMap<String, Object>();

		GetCorrectParamTypes paramsMap = new GetCorrectParamTypes();

		System.out.println("resList 0====== "+resList);
		if(resList != null )
		{
			mMap = paramsMap.correctTypeValues(paramMap, algoName, resList);
			//System.out.println(mMap);

			HashMap<String, Object> expected = new HashMap<String, Object>();
			expected.put("n_init", 10);
			expected.put("n_clusters", 5);
			expected.put("init", "random");
			expected.put("n_jobs", 8);
			assertEquals(mMap.size(),expected.size());
			for(Map.Entry<String, Object> m:mMap.entrySet()){
				for(Map.Entry<String, Object> m1:expected.entrySet()){
					if(m.getKey().equals(m1.getKey()) && m.getValue().equals(m1.getValue()) )
						System.out.println("true");
				}
			}
		}
}
}

