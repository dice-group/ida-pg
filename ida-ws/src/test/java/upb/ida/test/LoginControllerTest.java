package upb.ida.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import upb.ida.Application;
import upb.ida.bean.ResponseBean;
import upb.ida.rest.LoginController;
import static org.junit.Assert.assertEquals;

/**
 * Class to test the Login Controller's check login method which returns if any user is logged into IDA currently or not.
 * Author: Nandeesh Prabushanker
 */
@RunWith(SpringRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = {Application.class})
public class LoginControllerTest {

	@Autowired
	private LoginController loginController;

	/**
	 * Positive test case for the "auth/check-login" service when a mock user is logged into IDA.
	 * author: Nandeesh Prabushanker
	 */
	@Test
	@WithMockUser
	public void checkLoginTestPos() {
		ResponseBean responseBean;
		responseBean = loginController.checkLogin();
		String responseMsg = responseBean.getErrMsg();
		String expected = "User logged in: user";
		assertEquals(expected, responseMsg);
	}

	/**
	 * Negative test case for the "auth/check-login" service when user is not logged into IDA.
	 * author: Nandeesh Prabushanker
	 */
	@Test
	@WithAnonymousUser
	public void checkLoginTestNeg() {
		ResponseBean responseBean;
		responseBean = loginController.checkLogin();
		String responseMsg = responseBean.getErrMsg();
		String expected = "Login Required";
		assertEquals(expected, responseMsg);
	}

}
