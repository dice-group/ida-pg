package com.poc.controllers;

import com.poc.commands.UserForm;
import com.poc.converters.UserToUserForm;
import com.poc.model.User;
import com.poc.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;

@Controller
public class UserController {
    private UserService userService;

    private UserToUserForm userToUserForm;

    @Autowired
    public void setUserToUserForm(UserToUserForm userToUserForm) {
        this.userToUserForm = userToUserForm;
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @RequestMapping("/")
    public String redirToList(){
        return "redirect:/user/list";
    }

    @RequestMapping({"/user/list", "/user"})
    public String listProducts(Model model){
        model.addAttribute("users", userService.listAll());
        return "user/list";
    }

    @RequestMapping("/user/show/{id}")
    public String getProduct(@PathVariable String id, Model model){
        model.addAttribute("user", userService.getById(Long.valueOf(id)));
        return "user/show";
    }

    @RequestMapping("user/edit/{id}")
    public String edit(@PathVariable String id, Model model){
        User user = userService.getById(Long.valueOf(id));
        UserForm userForm = userToUserForm.convert(user);

        model.addAttribute("userForm", userForm);
        return "user/userForm";
    }

    @RequestMapping("/user/new")
    public String newProduct(Model model){
        model.addAttribute("userForm", new UserForm());
        return "user/userForm";
    }

    @RequestMapping(value = "/user", method = RequestMethod.POST)
    public String saveOrUpdateUser(@Valid UserForm userForm, BindingResult bindingResult){

        if(bindingResult.hasErrors()){
            return "user/userform";
        }

        User savedUser = userService.saveOrUpdateProductForm(userForm);

        return "redirect:/user/show/" + savedUser.getId();
    }

    @RequestMapping("/user/delete/{id}")
    public String delete(@PathVariable String id){
    	userService.delete(Long.valueOf(id));
        return "redirect:/user/list";
    }
}
