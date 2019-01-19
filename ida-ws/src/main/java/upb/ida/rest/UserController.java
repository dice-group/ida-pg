package upb.ida.rest;

import upb.ida.bean.ResponseBean;
import upb.ida.bean.User;
import upb.ida.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class UserController {
	
	@Autowired
    private UserService userService;
	
	@Autowired
	private ResponseBean responseBean;

    @RequestMapping(value="/user/list", method=RequestMethod.GET)
    @ResponseBody
    public ResponseBean listUsers(){
    	List<User> users = userService.listAllUsers();
    	if(users.isEmpty() || users == null)
    	{
    		responseBean.setErrCode(404);
    	}
    	else 
    	{
    		Map<String, Object> returnMap = new HashMap<String, Object>();
    		returnMap.put("users", users);
    		responseBean.setPayload(returnMap);
    	}
        return responseBean; 
    }

    @RequestMapping(value="/user/update", method=RequestMethod.POST)
    @ResponseBody
    public ResponseBean updateUser(@RequestBody final User user){
    	User updatedUser = userService.saveOrUpdate(user);
    	if(updatedUser == null)
    	{
    		responseBean.setErrCode(404);
    	}
    	else 
    	{
    		Map<String, Object> returnMap = new HashMap<String, Object>();
    		returnMap.put("updatedUser", updatedUser);
    		responseBean.setPayload(returnMap);
    	}
        return responseBean; 
    }
    
    @RequestMapping(value="/user/new", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public ResponseBean createNewUser(@RequestBody final User user) {  
    	User newUser = userService.saveOrUpdate(user);
    	if(newUser == null)
    	{
    		responseBean.setErrCode(404);
    	}
    	else 
    	{
    		Map<String, Object> returnMap = new HashMap<String, Object>();
    		returnMap.put("newUser", newUser);
    		responseBean.setPayload(returnMap);
    	}
        return responseBean; 
    }

    @RequestMapping("/user/delete/{id}")
    @ResponseBody
    public void deleteUser(@PathVariable String id){
    	userService.delete(Long.valueOf(id));
    }
}
