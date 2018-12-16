package upb.ida.provider;

import com.rivescript.macro.Subroutine;
import com.rivescript.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import upb.ida.bean.ResponseBean;
import upb.ida.constant.IDALiteral;
import upb.ida.util.FileUtil;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Map;

@Component
public class UploadDataset implements Subroutine {
    @Autowired
    private FileUtil fileUtil;
    @Autowired
    private ResponseBean responseBean;

    public String call(com.rivescript.RiveScript rs, String[] args) {
        String message = args[0].toLowerCase().trim();
        Map<String, Object> dataMap = responseBean.getPayload();
        dataMap.put("fdgData", message);
        String resp = IDALiteral.RESP_FAIL_ROUTINE;
        System.out.println(message);
        File file = new File(System.getProperty("user.dir") + "/upload-ds/" + message + ".ttl");
        if (! file.exists()) {

            responseBean.setActnCode(IDALiteral.UIA_UPLOAD);
            responseBean.setPayload(dataMap);
            resp = IDALiteral.RESP_PASS_ROUTINE;
        }
        return resp;
    }
}
