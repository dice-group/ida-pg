package upb.ida.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import upb.ida.Application;
import upb.ida.bean.ResponseBean;
import upb.ida.rest.MessageRestController;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertThat;

@RunWith(SpringRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = {Application.class})

public class MessageRestControllerClusterTest {

	@Autowired
	private MessageRestController mrc;

	@Test
	public void sendmessagetestpos() throws Exception {
		ResponseBean responseBean;
		responseBean = mrc.sendmessage("What are the available clustering algorithms?", "1", "Continent", "test");
		responseBean = mrc.sendmessage("I would like to run the k_means algorithm on the current table", "1", "Continent", "test");
		responseBean = mrc.sendmessage("Optional parameters should be n_clusters, n_jobs and n_init", "1", "Continent", "test");
		responseBean = mrc.sendmessage("Set n_clusters as 5", "1", "Continent", "test");
		responseBean = mrc.sendmessage("Set precompute_distances as auto", "1", "Continent", "test");
		responseBean = mrc.sendmessage("Set n_init as 5", "1", "Continent", "test");
		responseBean = mrc.sendmessage("Set n_jobs as 8", "1", "Continent", "test");
		responseBean = mrc.sendmessage("Clustering features are population", "1", "Continent", "test");
		responseBean = mrc.sendmessage("Label feature should be name", "1", "Continent", "test");


		String[] cluster1 = {"Antarctica", "Oceania"};
		String[] cluster2 = {"Asia"};
		String[] cluster3 = {"Europe"};
		String[] cluster4 = {"North America", "South America"};
		String[] cluster5 = {"Africa"};
		List<List<Object>> expected = new ArrayList<>();
		List<List<Object>> actual = new ArrayList<>();
		expected.add(Arrays.asList(cluster1));
		expected.add(Arrays.asList(cluster2));
		expected.add(Arrays.asList(cluster3));
		expected.add(Arrays.asList(cluster4));
		expected.add(Arrays.asList(cluster5));

		Map<String, List<Object>> clusterMap = new HashMap<>();
		List<Object> nameLst;
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> response = (List<Map<String, Object>>) responseBean.getPayload().get("clusterData");
		for (Map<String, Object> entry : response) {
			if (clusterMap.get(entry.get("clusterLabel").toString()) == null) {
				nameLst = new ArrayList<>();
			} else {
				nameLst = clusterMap.get(entry.get("clusterLabel").toString());
			}
			nameLst.add(entry.get("name"));
			clusterMap.put(entry.get("clusterLabel").toString(), nameLst);
		}
		for(String label: clusterMap.keySet()){
			actual.add(clusterMap.get(label));
		}

		assertThat("List equality without order",
				actual, containsInAnyOrder(expected.toArray()));
	}

	@Test
	public void sendmessagetestNeg() throws Exception {
		ResponseBean responseBean;
		responseBean = mrc.sendmessage("What are the available clustering algorithms?", "1", "Continent", "test");
		responseBean = mrc.sendmessage("I would like to run the k_means algorithm on the current table", "1", "Continent", "test");
		responseBean = mrc.sendmessage("Optional parameters should be n_clusters, n_jobs and n_init", "1", "Continent", "test");
		responseBean = mrc.sendmessage("Set n_clusters as 5", "1", "Continent", "test");
		responseBean = mrc.sendmessage("Set precompute_distances as auto", "1", "Continent", "test");
		responseBean = mrc.sendmessage("Set n_init as 5", "1", "Continent", "test");
		responseBean = mrc.sendmessage("Set n_jobs as 8", "1", "Continent", "test");
		responseBean = mrc.sendmessage("Clustering features are population", "1", "Continent", "test");
		responseBean = mrc.sendmessage("Label feature should be name", "1", "Continent", "test");


		String[] continents = {"Antarctica", "Oceania", "North America", "Asia", "Africa", "South America", "Europe"};
		String[] populations = {"0", "32000000", "528720588", "3879000000", "922011000", "382000000", "731000000"};
		String[] clusters = {"0", "2", "3", "1", "4", "3", "2"};
		List<Map<String, Object>> expected = new ArrayList<>();
		Map<String, Object> entry;
		for (int i = 0; i < continents.length; i++) {
			entry = new HashMap<>();
			entry.put("name", continents[i]);
			entry.put("population", populations[i]);
			entry.put("clusterLabel", clusters[i]);
			expected.add(entry);
		}

		@SuppressWarnings("unchecked")
		List<Map<String, Object>> actual = (List<Map<String, Object>>) responseBean.getPayload().get("clusterData");
		assertNotEquals(expected, actual);
	}

}
