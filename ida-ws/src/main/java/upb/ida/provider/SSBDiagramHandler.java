package upb.ida.provider;

import com.rivescript.macro.Subroutine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import upb.ida.bean.ResponseBean;
import upb.ida.constant.IDALiteral;
import upb.ida.dao.DataRepository;
import upb.ida.util.FileUtil;
import upb.ida.util.StringLengthComparator;
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
    private Map<Integer, TreeSet<Integer>> rangeMap = new HashMap<>();
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
		ArrayList<ArrayList<String>> response = new ArrayList<>();

        try {
			DataRepository dataRepository = new DataRepository(actvDs, false);
			List<Map<String, String>> data = dataRepository.getData(actvTbl);
            generateSsbFile(actvTbl, args[0], args[1], actvDs);
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

    private void extractRangeData(String actvTbl, String col1, String col2, String actvDs) throws IOException {
		DataRepository dataRepository = new DataRepository(actvDs, false);
		List<Map<String, String>> data = dataRepository.getData(actvTbl);
		for (Map<String, String> entry : data) {
			int col1ID = Integer.parseInt(entry.get(col1));
			int range = Integer.parseInt(entry.get(col2));
			TreeSet<Integer> rangeSet = rangeMap.get(col1ID);
			if (rangeSet == null) {
				rangeSet = new TreeSet<>(Comparator.reverseOrder());
			}
			rangeSet.add(range);
			rangeMap.put(col1ID, rangeSet);
		}
    }

    private void generateSeqCountMap() {
        for (Integer id : rangeMap.keySet()) {
            Collection<Integer> coll = rangeMap.get(id);
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

    public void generateSsbFile(String actvTbl, String col1, String col2, String actvDs) throws IOException {
        extractRangeData(actvTbl, col1, col2, actvDs);
        generateSeqCountMap();
    }
}
