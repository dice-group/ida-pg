package upb.ida.rest;

import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import upb.ida.bean.ResponseBean;

@RestController
@CrossOrigin
public class LoginController {
	
	@Autowired
	private ResponseBean responseBean;
	
	@RequestMapping(value = "/login", method = {RequestMethod.GET,RequestMethod.POST})
    public ResponseBean login(@RequestParam(value = "error", required = false) String error,
            @RequestParam(value = "logout", required = false) String logout,
            @RequestParam(value = "success", required = false) String success, HttpServletRequest request) {
        if (error != null) {
            responseBean.setErrCode(404);
            responseBean.setErrMsg("Username/Password Incorrect");
        } else if (success != null) {
            responseBean.setErrMsg("Login Successful");
        } else if (logout != null) {
            responseBean.setErrMsg("Logout Successful");
        } else {
            responseBean.setErrCode(401);
            responseBean.setErrMsg("Login Required");
        }
        return responseBean;
    }
}
