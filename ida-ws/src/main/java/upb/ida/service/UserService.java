package upb.ida.service;

import upb.ida.domains.User;
//import upb.ida.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;


@Service
public class UserService{

	//@Autowired
    //private UserRepository userRepository;

	//@Transactional(readOnly = true)
    public List<User> listAllUsers() {
    	//List<User> users = new ArrayList<>();
        //userRepository.findAll().forEach(users::add);
        //users.forEach(user -> user.setPassword(""));
        //return users;
		return null;
    }

	//@Transactional(readOnly = true)
    public User getById(Long id) {
        //return userRepository.findById(id).orElse(null);
		return null;
    }
    
	//@Transactional(readOnly = true)
    public User getByUsername(String username) {
        //return userRepository.findUserByUsername(username);
        return null;
    }

	//@Transactional(readOnly = false)
    public User saveOrUpdate(User user) {
    	//return userRepository.save(user);
		return null;
    }

	//@Transactional(readOnly = false)
    public void delete(Long id) {
        //userRepository.deleteById(id);
    }
}
