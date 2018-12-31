package com.poc.services;

import com.poc.commands.UserForm;
import com.poc.model.User;

import java.util.List;

public interface UserService {

    List<User> listAll();

    User getById(Long id);

    User saveOrUpdate(User user);

    void delete(Long id);

    User saveOrUpdateProductForm(UserForm userForm);
}
