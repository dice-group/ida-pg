package upb.ida.venndiagram;

import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import upb.ida.dao.DataRepository;
import upb.ida.util.FileUtil;

/**
 * Exposes util methods to perform FDG related operations
 *
 * @author Maqbool
 *
 */
@Component
public class VENN_Util {
    @Autowired
    private FileUtil dem;

    public HashMap<String, Object> generateVennDiagram (String actvTbl, String[] args, String actvDs)
            throws NumberFormatException  {
        System.out.println(Arrays.toString(args));
		DataRepository dataRepository = new DataRepository(actvDs, false);
		List<Map<String, String>> data = dataRepository.getData(actvTbl);
		Map<String, String> columns = data.get(0);
        int limit = Integer.parseInt(args[1]);

        VENN_DATA_FILTER vennGenerator = new VENN_DATA_FILTER();
        vennGenerator.createDataMap(actvTbl, args[0], args[2], actvDs);
		vennGenerator.filterDataMap(limit);

        VENN_DATA_GENERATOR<Integer, Integer> vennDataGenerator = new VENN_DATA_GENERATOR<>(vennGenerator.dataMap);
        Set<VENN_ITEM<Integer>> vennItems = vennDataGenerator.generateVennItems();

        HashMap<String, Object> response = new HashMap<>();

        response.put("data", vennItems);
        response.put("label", args[2]);
        return response;
    }
}
