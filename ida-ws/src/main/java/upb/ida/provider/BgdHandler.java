package upb.ida.provider;

import java.io.IOException;
import java.text.ParseException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.rivescript.macro.Subroutine;

import upb.ida.bean.ResponseBean;
import upb.ida.util.BarGraphUtil;
/**
 * BgdHandler is a subroutine that is used to create response for UI
 * of Bar Graph by taking inputs from user through rivescript  .
 * 
 * @author Faisal
 *
 */
@Component
public class BgdHandler implements Subroutine {

	@Autowired
	private ResponseBean responseBean;
	@Autowired
	private BarGraphUtil barGraphUtil;
	
	/**
	 *Method to create response for bar graph visualization
	 * @param rs
	 *            - {@link call#rs}
	 * @param args
	 *            - {@link call#args}
	 * @return - String "pass" or "fail"
	 */
	public String call(com.rivescript.RiveScript rs, String[] args) {

		try {
			/**
			 * function call takes responseBean and arguments as 
			 * input for Bar Graph visualization 
			 */
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
