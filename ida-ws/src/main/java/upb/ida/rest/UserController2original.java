//package upb.ida.rest;
//
//import upb.ida.bean.ResponseBean;
//import upb.ida.domains.User;
//import upb.ida.constant.IDALiteral;
//import upb.ida.service.UserService;
////import upb.ida.smtp.EmailForSignup;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.CrossOrigin;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//import org.springframework.web.bind.annotation.ResponseBody;
//import org.springframework.web.bind.annotation.ResponseStatus;
//
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//@Controller
//@CrossOrigin(origins = "*", allowCredentials = "true")
//@RequestMapping("/user")
//public class UserController {
//	//"http://fuseki:3030"
//	@Autowired
//    private UserService userService;
//	
//	@Autowired
//	private ResponseBean responseBean;
//
//    @RequestMapping(value="/list", method=RequestMethod.GET)
//    @ResponseBody
//    public ResponseBean listUsers(){
//    	List<User> users = userService.listAllUsers();
//    	if(users == null)
//    	{
//    		responseBean.setErrCode(IDALiteral.FAILURE_USERLIST);
//    	}
//    	else 
//    	{
//    		Map<String, Object> returnMap = new HashMap<String, Object>();
//    		returnMap.put("users", users);
//    		responseBean.setPayload(returnMap);
//    	}
//        return responseBean; 
//    }
//
//    @RequestMapping(value="/update", method=RequestMethod.POST)
//    @ResponseBody
//    public ResponseBean updateUser(@RequestBody final User user){
//    	User updatedUser = userService.saveOrUpdate(user);
//    	if(updatedUser == null)
//    	{
//    		responseBean.setErrCode(IDALiteral.FAILURE_UPDATEUSER);
//    	}
//    	else 
//    	{
//    		Map<String, Object> returnMap = new HashMap<String, Object>();
//    		returnMap.put("updatedUser", updatedUser);
//    		responseBean.setPayload(returnMap);
//    	}
//        return responseBean; 
//    }
//
//    @RequestMapping(value="/new", method = RequestMethod.POST)
//    @ResponseStatus(HttpStatus.CREATED)
//    @ResponseBody
//    public ResponseBean createNewUser(@RequestBody final User user) throws Exception {
//    	
//    	if(userService.getByUsername(user.getUsername()) != null)
//    	{
//    		responseBean.setErrCode(IDALiteral.FAILURE_USEREXISTS);
//    		return responseBean;
//    	}
//    	User newUser = userService.saveOrUpdate(user);
//    	if(newUser == null)
//    	{
//    		responseBean.setErrCode(IDALiteral.FAILURE_NEWUSER);
//    	}
//    	else 
//    	{
//    		Map<String, Object> returnMap = new HashMap<String, Object>();
//    		returnMap.put("newUser", newUser);
//    		responseBean.setPayload(returnMap);
////    		TODO: This email functionality is commented just for now. (Tested and working)
////    		try{
////    			EmailForSignup.sendEmail(newUser.getUsername());
////    		}catch(Exception ex)
////    		{
////    			responseBean.setErrCode(IDALiteral.FAILURE_EMAILSENT);
////    			responseBean.setErrMsg(ex.getMessage());
////    		}
//    	}
//        return responseBean; 
//    }
//
//    @RequestMapping(value="/delete/{id}", method = RequestMethod.DELETE)
//    @ResponseBody
//    public ResponseBean deleteUser(@PathVariable String id){
//    	if (userService.getById(Long.valueOf(id)) == null)
//    	{
//    		responseBean.setErrCode(IDALiteral.ALREADY_LOGGEDIN);
//    	}else
//    	{
//    		userService.delete(Long.valueOf(id));
//    		responseBean.setErrMsg("User Deleted");
//    	}
//    	return responseBean;
//    }
//}
