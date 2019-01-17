package com.poc.services;

import com.poc.model.User;
import java.util.List;

public interface UserService {

    public List<User> listAllUsers();

    public User getById(Long id);

    public User saveOrUpdate(User user);

    public void delete(Long id);

    public User getByUsername(String username);
}
