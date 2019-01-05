package com.poc.services;

import com.poc.commands.UserForm;
import com.poc.converters.UserFormToUser;
import com.poc.model.User;
import com.poc.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;
    private UserFormToUser userFormToUser;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, UserFormToUser userFormToUser) {
        this.userRepository = userRepository;
        this.userFormToUser = userFormToUser;
    }


    @Override
    public List<User> listAll() {
        List<User> users = new ArrayList<>();
        userRepository.findAll().forEach(users::add); //fun with Java 8
        return users;
    }

    @Override
    public User getById(Long id) {
        return userRepository.findById(id).orElse(null);
    }
    
    @Override
    public User getByUsername(String username) {
        return userRepository.findUserByUsername(username);
    }

    @Override
    public User saveOrUpdate(User user) {
        userRepository.save(user);
        return user;
    }

    @Override
    public void delete(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public User saveOrUpdateProductForm(UserForm userForm) {
        User savedUser = saveOrUpdate(userFormToUser.convert(userForm));

        System.out.println("Saved User Id: " + savedUser.getId());
        return savedUser;
    }
}
