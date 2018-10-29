package upb.ida.temp;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.dice_research.topicmodeling.commons.sort.AssociativeSort;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;

public class DummyVennDataGenerator {
	public Map<Integer, Set<Integer>> dataMap = new HashMap<>();

	private void createDataMap(File inputFile, int labelColIndx, int dataColIndx) throws IOException {
		// create csvreaderbuilder for dnstTbl
		CSVReaderBuilder rBuilder = new CSVReaderBuilder(new FileReader(inputFile));
		// build the reader
		CSVReader csvReader = rBuilder.build();
		try {
			// read first line
			csvReader.readNext();
			for (String[] line; (line = csvReader.readNext()) != null;) {
				int ordenId = Integer.parseInt(line[labelColIndx]);
				int fhrId = Integer.parseInt(line[dataColIndx]);
				Set<Integer> fhrSet = dataMap.get(ordenId);
				if (fhrSet == null) {
					fhrSet = new HashSet<>();
					dataMap.put(ordenId, fhrSet);
				}
				fhrSet.add(fhrId);
			}
		} finally {
			csvReader.close();
		}
	}

	public static void main(String[] args) throws IOException {
		File inputFile = new File("D:\\Nikit\\ProjectGroup_WS\\Datasets\\ss-dienstalterliste\\tbl_ssfuehrer_orden.csv");
		DummyVennDataGenerator dataGenerator = new DummyVennDataGenerator();
		dataGenerator.createDataMap(inputFile, 2, 1);
		dataGenerator.filterDataMap(5);
		VennDataGenerator<Integer, Integer> vennDataGenerator = new VennDataGenerator<>(dataGenerator.dataMap);
		Set<VennItem<Integer>> vennItems = vennDataGenerator.generateVennItems();
		System.out.println(vennItems);
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
