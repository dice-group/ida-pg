package com.poc.controllers;

import com.poc.model.User;
import com.poc.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

@Controller
public class UserController {
	
	@Autowired
    private UserService userService;

    @RequestMapping(value="/user/list", method=RequestMethod.GET)
    @ResponseBody
    public List<User> listUsers(){
        return userService.listAllUsers();
    }

    @RequestMapping(value="user/edit/{id}", method=RequestMethod.GET)
    @ResponseBody
    public User edit(@PathVariable String id){
        User user = userService.getById(Long.valueOf(id));
        return user;
    }
    
    @RequestMapping(value="/user/new", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public void createNewUser(@RequestBody final User user) {
        userService.saveOrUpdate(user);
    }

    @RequestMapping("/user/delete/{id}")
    @ResponseBody
    public void deleteUser(@PathVariable String id){
    	userService.delete(Long.valueOf(id));
    }
}
