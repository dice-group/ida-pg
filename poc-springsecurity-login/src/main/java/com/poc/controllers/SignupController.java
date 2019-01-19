package com.poc.controllers;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import com.poc.ResponseBean;
import com.poc.smtp.EmailForSignup;

@RestController
@CrossOrigin
public class SignupController {
	
	@Autowired
	private ResponseBean responseBean;
	
	@RequestMapping(value = "/signup/email", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public ResponseBean sendEmailToUser(@RequestBody final String userName) {  
		boolean isEmailSent;
		try {
			isEmailSent = EmailForSignup.sendEmail(userName);
			if(isEmailSent)
			{
				Map<String, Object> returnMap = new HashMap<String, Object>();
				returnMap.put("isEmailSent", isEmailSent);
				responseBean.setPayload(returnMap);
			}
			else 
			{
				responseBean.setErrCode(404);
			}
		} catch (Exception e) {
			responseBean.setErrCode(404);
		}
        return responseBean; 
    }
}