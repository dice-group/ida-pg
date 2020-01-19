package upb.ida.venndiagram;

import java.util.*;
import org.springframework.stereotype.Component;

/**
 * Exposes util methods to perform FDG related operations
 *
 * @author Maqbool
 *
 */
@Component
public class VennUtil {

    public HashMap<String, Object> generateVennDiagram (String actvTbl, String[] args, String actvDs)
            throws NumberFormatException  {
        System.out.println(Arrays.toString(args));
        int limit = Integer.parseInt(args[1]);

        VennDataFilter vennGenerator = new VennDataFilter();
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
