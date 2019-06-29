package upb.ida.venndiagram;

import java.io.File;
import java.io.IOException;
import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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

    public HashMap<String, Object> generateVennDiagram (String filePath, String[] args)
            throws IOException  {
        System.out.println(Arrays.toString(args));
        File file = new File(dem.fetchSysFilePath(filePath));
        Map<String, String> columns = dem.convertToMap(file).get(0);
        int limit = Integer.parseInt(args[1]);

        VENN_DATA_FILTER vennGenerator = new VENN_DATA_FILTER();
        vennGenerator.createDataMap(file, dem.getColumnId(columns, args[0]), dem.getColumnId(columns, args[2]));
        vennGenerator.filterDataMap(limit);

        VENN_DATA_GENERATOR<Integer, Integer> vennDataGenerator = new VENN_DATA_GENERATOR<>(vennGenerator.dataMap);
        Set<VENN_ITEM<Integer>> vennItems = vennDataGenerator.generateVennItems();

        HashMap<String, Object> response = new HashMap<>();

        response.put("data", vennItems);
        response.put("label", args[2]);
        return response;
    }
}
