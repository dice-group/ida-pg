package upb.ida.provider;

import com.rivescript.macro.Subroutine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import upb.ida.bean.ResponseBean;
import upb.ida.constant.IDALiteral;
import upb.ida.ontologyExplorer.OntologyExplorer;

import java.util.Map;

@Component
public class OntologyExplorerHandler implements Subroutine {
	@Autowired
	private ResponseBean responseBean;

	public String call (com.rivescript.RiveScript rs, String[] args) {
		Map<String, Object> dataMap = responseBean.getPayload();
		OntologyExplorer oe = new OntologyExplorer();
		responseBean.setActnCode(11);
		try {
			dataMap.put("ontologyData", oe.fetchData(args[0]));
			return IDALiteral.RESP_PASS_ROUTINE;
		} catch (Exception ex){
			System.out.println(ex.getMessage());
		}
		return IDALiteral.RESP_FAIL_ROUTINE;
	}
}
