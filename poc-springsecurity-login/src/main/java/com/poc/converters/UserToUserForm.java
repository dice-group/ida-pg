package com.poc.converters;

import com.poc.commands.UserForm;
import com.poc.model.User;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class UserToUserForm implements Converter<User, UserForm> {
    @Override
    public UserForm convert(User user) {
    	UserForm userForm = new UserForm();
    	userForm.setId(user.getId());
    	userForm.setUsername(user.getUsername());
    	userForm.setPassword(user.getPassword());
    	userForm.setFirstname(user.getFirstname());
    	userForm.setLastname(user.getLastname());
        return userForm;
    }
}
