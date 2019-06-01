package upb.ida.provider;

import com.rivescript.macro.Subroutine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import upb.ida.bean.ResponseBean;
import upb.ida.util.StoryCreationUtil;
import java.io.IOException;

/**
 * scHandler is a subroutine that is used to create response for UI
 * of Story Creation by taking inputs from user through rivescript  .
 *
 * @author Ayaz
 */

@Component
public class ScHandler implements Subroutine {
	@Autowired
	private ResponseBean responseBean;
	@Autowired
	private StoryCreationUtil storyCreationUtil;
	
	/**
	 *Method to create response for Force Directed Graph visualization
	 * @param rs
	 *            - {@link call#rs}
	 * @param args
	 *            - {@link call#args}
	 * @return  String - pass or fail
	 */
	
	public String call (com.rivescript.RiveScript rs, String[] args) {

		try {
			/**
			 * function call takes responseBean and arguments as
			 * input for Story Creation
			 */
			storyCreationUtil.generateStory(args, responseBean);
			return "pass";
		} catch (IOException e) {
			e.printStackTrace();

		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		return "fail";
	}
}
