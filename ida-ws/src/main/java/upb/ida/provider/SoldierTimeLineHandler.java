package upb.ida.provider;

import com.rivescript.macro.Subroutine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import upb.ida.bean.ResponseBean;
import upb.ida.constant.IDALiteral;
import upb.ida.dao.SoldierTimeLine;

import java.util.Map;

@Component
public class SoldierTimeLineHandler implements Subroutine {
	@Autowired
	private ResponseBean responseBean;

	public String call (com.rivescript.RiveScript rs, String[] args) {
		Map<String, Object> dataMap = responseBean.getPayload();
		SoldierTimeLine soldierTimeLine = new SoldierTimeLine("ssfuehrer");
		responseBean.setActnCode(10);
		try {
			dataMap.put("soldierTimeLineData", soldierTimeLine.getData(args[0]));
			return IDALiteral.RESP_PASS_ROUTINE;
		} catch (Exception ex){
			System.out.println(ex.getMessage());
		}
		return IDALiteral.RESP_FAIL_ROUTINE;
	}
}
