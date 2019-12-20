package upb.ida.venndiagram;

import java.util.*;
import org.dice_research.topicmodeling.commons.sort.AssociativeSort;
import upb.ida.dao.DataRepository;

public class VENN_DATA_FILTER {
	public Map<Integer, Set<Integer>> dataMap = new HashMap<>();

	public void createDataMap(String actvTbl, String labelCol, String dataCol, String actvDs) throws NumberFormatException {
		DataRepository dataRepository = new DataRepository(false);
		List<Map<String, String>> data = dataRepository.getData(actvTbl, actvDs);
		for (Map<String, String> entry : data) {
			int ordenId = Integer.parseInt(entry.get(labelCol));
			int fhrId = Integer.parseInt(entry.get(dataCol));
			Set<Integer> fhrSet = dataMap.computeIfAbsent(ordenId, k -> new HashSet<>());
			fhrSet.add(fhrId);
		}
	}

	public void filterDataMap(int topLimit) {
		int[] ordenIdArr = new int[dataMap.size()];
		int j = 0;
		for (int ordenId : dataMap.keySet()) {
			ordenIdArr[j] = ordenId;
			j++;
		}
		int len = ordenIdArr.length;
		int[] sizeArr = new int[len];
		for (int i = 0; i < len; i++) {
			int ordenId = ordenIdArr[i];
			sizeArr[i] = dataMap.get(ordenId).size();
		}
		AssociativeSort.quickSort(sizeArr, ordenIdArr);
		Map<Integer, Set<Integer>> newDataMap = new HashMap<>();
		int lastIndx = len - 1;
		for (int i = lastIndx; i > (lastIndx - topLimit) && i > -1; i--) {
			int ordenId = ordenIdArr[i];
			newDataMap.put(ordenId, dataMap.get(ordenId));
		}
		dataMap = newDataMap;
	}

}
