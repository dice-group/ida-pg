package upb.ida.temp;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.CSVWriter;

public class SsRangDataExtractor {

	private static final String INPUT_FILE_NAME = "D:\\Nikit\\ProjectGroup_WS\\Datasets\\ss-dienstalterliste\\tbl_ssfuehrer_ssrang.csv";
	private static final String OUTPUT_FILE_NAME = "D:\\Nikit\\ProjectGroup_WS\\Datasets\\ss-dienstalterliste\\tbl_ssfuehrer_ssrang_info.csv";
	private Map<Integer, SsFuehrerInfo> dataMap = new HashMap<>();
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy");
	private String[] writeHeaders = { "ssfuehrer_id", "promtn_count", "last_rang", "promtn_freq" };

	public void extractRangData(File inputFile) throws IOException, ParseException {
		// create csvreaderbuilder for dnstTbl
		CSVReaderBuilder rBuilder = new CSVReaderBuilder(new FileReader(inputFile));
		// build the reader
		CSVReader csvReader = rBuilder.build();
		try {
			// read first line
			csvReader.readNext();
			int fhrIdIndx = 1;
			int rangIndx = 2;
			int promDtIndx = 3;
			for (String[] line; (line = csvReader.readNext()) != null;) {
				int fhrId = Integer.parseInt(line[fhrIdIndx]);
				int rang = Integer.parseInt(line[rangIndx]);
				Date dt = DATE_FORMAT.parse(line[promDtIndx]);
				SsFuehrerInfo fuehrerInfo = dataMap.get(fhrId);
				if (fuehrerInfo == null) {
					fuehrerInfo = new SsFuehrerInfo();
					dataMap.put(fhrId, fuehrerInfo);
				}
				fuehrerInfo.updateData(rang, dt);
			}
		} finally {
			csvReader.close();
		}

	}

	public void writeDataToFile(File outputFile) throws IOException {
		// create dir for output file
		outputFile.getParentFile().mkdirs();
		// open csv writer for output file

		CSVWriter csvWriter = new CSVWriter(new FileWriter(outputFile));
		try {
			csvWriter.writeNext(writeHeaders);
			for (int fhrId : dataMap.keySet()) {
				SsFuehrerInfo fuehrerInfo = dataMap.get(fhrId);
				int count = fuehrerInfo.getCount();
				int lastRang = fuehrerInfo.getLastSsRang();
				float freq = fuehrerInfo.getRangFreq();
				String[] line = getStringArr(fhrId, count, lastRang, freq);
				csvWriter.writeNext(line);
			}
		} finally {
			csvWriter.close();
		}
	}

	private String[] getStringArr(int fhrId, int count, int lastRang, float freq) {
		String[] strArr = new String[4];
		strArr[0] = String.valueOf(fhrId);
		strArr[1] = String.valueOf(count);
		strArr[2] = String.valueOf(lastRang);
		strArr[3] = String.valueOf(freq);
		return strArr;
	}

	public static void main(String[] args) throws IOException, ParseException {
		SsRangDataExtractor dataExtractor = new SsRangDataExtractor();
		dataExtractor.extractRangData(new File(INPUT_FILE_NAME));
		dataExtractor.writeDataToFile(new File(OUTPUT_FILE_NAME));
	}
}
