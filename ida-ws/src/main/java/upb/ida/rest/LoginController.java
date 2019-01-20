package upb.ida.rest;

import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import upb.ida.bean.ResponseBean;
import upb.ida.constant.IDALiteral;

@RestController
@CrossOrigin(origins = "*", allowCredentials = "true")
@RequestMapping("/auth")
public class LoginController {
	
	@Autowired
	private ResponseBean responseBean;
	
	@RequestMapping(value = "/response", method = {RequestMethod.GET,RequestMethod.POST})
    public ResponseBean login(@RequestParam(value = "error", required = false) String error,
            @RequestParam(value = "logout", required = false) String logout,
            @RequestParam(value = "success", required = false) String success, HttpServletRequest request) {
        if (error != null) {
            responseBean.setErrCode(IDALiteral.FAILURE_CREDENTIALSINCORRECT);
            responseBean.setErrMsg("Username/Password Incorrect");
        } else if (success != null) {
            responseBean.setErrMsg("Login Successful");
        } else if (logout != null) {
            responseBean.setErrMsg("Logout Successful");
        } else {
            responseBean.setErrCode(IDALiteral.LOGIN_REQUIRED);
            responseBean.setErrMsg("Login Required");
        }
        return responseBean;
    }
	
	@RequestMapping(value = "/check-login", method = RequestMethod.GET)
    public ResponseBean checkLogin() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (! (auth instanceof AnonymousAuthenticationToken)) {
        	String username = auth.getName();
            responseBean.setErrCode(IDALiteral.ALREADY_LOGGEDIN);
            responseBean.setErrMsg("User logged in: "+username);
        }else{
            responseBean.setErrCode(IDALiteral.LOGIN_REQUIRED);
            responseBean.setErrMsg("Login Required");
        }
        return responseBean;
    }
}
