package upb.ida.provider;

import com.rivescript.macro.Subroutine;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import upb.ida.bean.ResponseBean;

import java.io.File;

import static upb.ida.constant.IDALiteral.DS_PATH;


@Component
public class ShowDataset implements Subroutine {
    @Autowired
    private ResponseBean responseBean;

    public String call(com.rivescript.RiveScript rs, String[] args) {
        File directory = new File(DS_PATH);
        String[] files = directory.list();
        String temp = "Datasets: ";
        for (String file: files) {
            temp = temp + FilenameUtils.removeExtension(file) + ", ";
        }

        responseBean.setChatmsg(DS_PATH);
        temp = temp.substring(0, temp.length() - 1);
        return temp;
    }
}
