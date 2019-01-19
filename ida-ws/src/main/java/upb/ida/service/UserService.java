package upb.ida.service;

import upb.ida.bean.User;
import upb.ida.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
public class UserService{

	@Autowired
    private UserRepository userRepository;

    public List<User> listAllUsers() {
        List<User> users = new ArrayList<>();
        userRepository.findAll().forEach(users::add); //fun with Java 8
        return users;
    }

    public User getById(Long id) {
        return userRepository.findById(id).orElse(null);
    }
    
    public User getByUsername(String username) {
        return userRepository.findUserByUsername(username);
    }

    public User saveOrUpdate(User user) {
    	return userRepository.save(user);
    }

    public void delete(Long id) {
        userRepository.deleteById(id);
    }
}
