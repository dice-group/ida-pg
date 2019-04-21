package upb.ida.provider;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.rivescript.macro.Subroutine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import upb.ida.bean.ResponseBean;
import upb.ida.constant.IDALiteral;
import upb.ida.util.FileUtil;
import upb.ida.util.StringLengthComparator;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * SSBDiagramHandler is a subroutine that is used to generate data for
 * Sequence Sun Burst diagram.
 *
 * @author Maqbool
 */

@Component
public class SSBDiagramHandler implements Subroutine {
	@Autowired
	private FileUtil fileUtil;
	@Autowired
	private ResponseBean responseBean;
    private Map<Integer, TreeSet<Integer>> rangMap = new HashMap<>();
    private Map<String, Integer> seqCountMap = new TreeMap<>(new StringLengthComparator().reversed());
	
	/**
	 * Method to create response for Geo Spatial Diagram visualization
	 * @param args
	 *            - {@link call#args}
	 * @return  String - pass or fail
	 */
	
	public String call (com.rivescript.RiveScript rs, String[] args) {

		String actvTbl = (String) responseBean.getPayload().get("actvTbl");
		String actvDs = (String) responseBean.getPayload().get("actvDs");

		Map<String, Object> dataMap = responseBean.getPayload();
		String path = fileUtil.getDTFilePath(actvDs, actvTbl);
		File csvFile = new File(fileUtil.fetchSysFilePath(path));

		ArrayList<ArrayList<String>> response = new ArrayList<>();

        try {
            List<Map<String, String>> data = fileUtil.convertToMap(csvFile);
//            ArrayList<HashMap<String, ArrayList<Double>>> response = new ArrayList<>();
            generateSsbFile(csvFile, fileUtil.getColumnId(data.get(0), args[0]), fileUtil.getColumnId(data.get(0), args[1]));
            dataMap.put("label", "sequence sun burst diagram data");
            responseBean.setActnCode(IDALiteral.UIA_SSBDIAGRAM);

            for (String ele : seqCountMap.keySet()) {
                ArrayList<String> row = new ArrayList<>();
                row.add(ele);
                row.add(seqCountMap.get(ele).toString());
                response.add(row);
            }

            dataMap.put("ssbDiagramData", response);
            responseBean.setPayload(dataMap);
        } catch (IOException e) {
            e.printStackTrace();
        }
		return "pass";
	}

    private void extractRangeData(File inputFile, int col1, int col2) throws IOException {
        // create csvreaderbuilder for dnstTbl
        CSVReaderBuilder rBuilder = new CSVReaderBuilder(new FileReader(inputFile));
        // build the reader
        CSVReader csvReader = rBuilder.build();
        try {
            // read first line
            csvReader.readNext();
            int col1Index = col1;
            int rangeIndex = col2;
            for (String[] line; (line = csvReader.readNext()) != null;) {
                int col1ID = Integer.parseInt(line[col1Index]);
                int rang = Integer.parseInt(line[rangeIndex]);
                TreeSet<Integer> rangSet = rangMap.get(col1ID);
                if (rangSet == null) {
                    rangSet = new TreeSet<>(Comparator.reverseOrder());
                    rangMap.put(col1ID, rangSet);
                }
                rangSet.add(rang);
            }
        } finally {
            csvReader.close();
        }

    }

    private void generateSeqCountMap() {
        for (Integer id : rangMap.keySet()) {
            Collection<Integer> coll = rangMap.get(id);
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

    public static <T> String convertCollToSeq(Collection<T> coll) {
        StringBuilder seqStr = new StringBuilder();
        for (T entry : coll) {
            seqStr.append(entry).append("-");
        }
        seqStr.append("end");
        return seqStr.toString();
    }

    public void generateSsbFile(File inputFile, int col1, int col2) throws IOException {
        extractRangeData(inputFile, col1, col2);
        generateSeqCountMap();
    }
}
