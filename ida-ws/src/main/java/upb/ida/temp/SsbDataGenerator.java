package upb.ida.temp;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.CSVWriter;

import upb.ida.util.StringLengthComparator;

public class SsbDataGenerator {
	private static final String INPUT_FILE_NAME = "D:\\Nikit\\ProjectGroup_WS\\Datasets\\ss-dienstalterliste\\tbl_ssfuehrer_ssrang.csv";
	private static final String OUTPUT_FILE_NAME = "D:\\Nikit\\ProjectGroup_WS\\Datasets\\ss-dienstalterliste\\rangseq_ssb.csv";
	private Map<Integer, TreeSet<Integer>> rangMap = new HashMap<>();
	private Map<String, Integer> seqCountMap = new TreeMap<>(new StringLengthComparator().reversed());

	private void extractRangData(File inputFile) throws IOException, ParseException {
		// create csvreaderbuilder for dnstTbl
		CSVReaderBuilder rBuilder = new CSVReaderBuilder(new FileReader(inputFile));
		// build the reader
		CSVReader csvReader = rBuilder.build();
		try {
			// read first line
			csvReader.readNext();
			int fhrIdIndx = 1;
			int rangIndx = 2;
			for (String[] line; (line = csvReader.readNext()) != null;) {
				int fhrId = Integer.parseInt(line[fhrIdIndx]);
				int rang = Integer.parseInt(line[rangIndx]);
				TreeSet<Integer> rangSet = rangMap.get(fhrId);
				if (rangSet == null) {
					rangSet = new TreeSet<>(Comparator.reverseOrder());
					rangMap.put(fhrId, rangSet);
				}
				rangSet.add(rang);
			}
		} finally {
			csvReader.close();
		}

	}

	private void generateSeqCountMap() {
		for (Integer fhrId : rangMap.keySet()) {
			Collection<Integer> coll = rangMap.get(fhrId);
			String seq = convertCollToSeq(coll);
			Integer count = seqCountMap.get(seq);
			if (count == null) {
				count = 1;
				seqCountMap.put(seq, count);
			} else {
				seqCountMap.put(seq, count + 1);
			}
		}
	}

	private void writeDataToCsv(File outputFile) throws IOException {
		// create dir for output file
		outputFile.getParentFile().mkdirs();
		// open csv writer for output file

		CSVWriter csvWriter = new CSVWriter(new FileWriter(outputFile));
		try {
			for (String seq : seqCountMap.keySet()) {
				String[] line = { seq, seqCountMap.get(seq).toString() };
				csvWriter.writeNext(line);
			}
		} finally {
			csvWriter.close();
		}
	}

	public static <T> String convertCollToSeq(Collection<T> coll) {
		StringBuilder seqStr = new StringBuilder();
		for (T entry : coll) {
			seqStr.append(entry).append("-");
		}
		seqStr.append("end");
		return seqStr.toString();
	}

	public void generateSsbFile(File inputFile, File outputFile) throws IOException, ParseException {
		extractRangData(inputFile);
		generateSeqCountMap();
		writeDataToCsv(outputFile);
	}

	public static void main(String[] args) throws IOException, ParseException {
		SsbDataGenerator dataGenerator = new SsbDataGenerator();
		File inputFile = new File(INPUT_FILE_NAME);
		File outputFile = new File(OUTPUT_FILE_NAME);
		dataGenerator.generateSsbFile(inputFile, outputFile);
	}

}
