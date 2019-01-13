package upb.ida.provider;

import com.rivescript.macro.Subroutine;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import upb.ida.bean.ResponseBean;

import java.io.File;
import java.util.Arrays;

import static upb.ida.constant.IDALiteral.DS_PATH;


@Component
public class ShowDataset implements Subroutine {
    @Autowired
    private ResponseBean responseBean;

    public String call(com.rivescript.RiveScript rs, String[] args) {
//        String message = args[0].toLowerCase().trim();
        File directory = new File(DS_PATH);
        String files1 = Arrays.toString(directory.list());
        responseBean.setChatmsg(DS_PATH);
        return FilenameUtils.removeExtension(files1);
    }
}
