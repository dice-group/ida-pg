package upb.ida.provider;

import java.io.IOException;
/**
 * DatasetUploadViewHandler is a subroutine that is use to open dataset file
 * upload view on the web UI.
 * It just add action code so that UI can open
 * 
 * @author Maqbool
 *
 */
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.rivescript.macro.Subroutine;

import upb.ida.bean.ResponseBean;
import upb.ida.constant.IDALiteral;
@Component
public class DatasetUploadViewHandler implements Subroutine {
	@Autowired
	private ResponseBean responseBean;

	public String call (com.rivescript.RiveScript rs, String[] args) {
        responseBean.setActnCode(IDALiteral.UIA_DTUV);
        return "pass";
	}
}
