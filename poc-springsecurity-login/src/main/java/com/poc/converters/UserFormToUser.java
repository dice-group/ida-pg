package com.poc.converters;

import com.poc.commands.UserForm;
import com.poc.model.User;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class UserFormToUser implements Converter<UserForm, User> {

    @Override
    public User convert(UserForm userForm) {
        User user = new User();
        if (userForm.getId() != null  && !StringUtils.isEmpty(userForm.getId())) {
            user.setId(new Long(userForm.getId()));
        }
        user.setUsername(userForm.getUsername());
        user.setPassword(userForm.getPassword());
        user.setFirstname(userForm.getFirstname());
        user.setLastname(userForm.getLastname());
        return user;
    }
}