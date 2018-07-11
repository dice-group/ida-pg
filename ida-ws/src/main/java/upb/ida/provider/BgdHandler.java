package upb.ida.provider;

import java.io.IOException;
import java.text.ParseException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.rivescript.macro.Subroutine;

import upb.ida.bean.ResponseBean;
import upb.ida.util.BarGraphUtil;

@Component
public class BgdHandler implements Subroutine {

	@Autowired
	private ResponseBean responseBean;
	@Autowired
	private BarGraphUtil barGraphUtil;

	public String call(com.rivescript.RiveScript rs, String[] args) {

		try {
			barGraphUtil.generateBarGraphData(args, responseBean);
			return "pass";
		} catch (IOException e) {
			e.printStackTrace();

		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return "fail";
	}

}
