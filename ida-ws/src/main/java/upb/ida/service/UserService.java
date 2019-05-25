package upb.ida.service;

import upb.ida.domains.User;
import upb.ida.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import  upb.ida.rest.UserController;

import java.util.ArrayList;
import java.util.List;


@Service
public class UserService{

	@Autowired
    private UserRepository userRepository;

	@Transactional(readOnly = true)
    public List<User> listAllUsers() {
    	List<User> users = new ArrayList<>();
        userRepository.findAll().forEach(users::add);
        users.forEach(user -> user.setPassword(""));
        return users;
    }

	@Transactional(readOnly = true)
    public User getById(Long id) {
        return userRepository.findById(id).orElse(null);
    }
    
	@Transactional(readOnly = true)
  //  public User getByUsername(String username) {
	public User getByUsername(String username) {
		return (User) UserController.select3(username);	
      //  return userRepository.findUserByUsername(username);
    }

	@Transactional(readOnly = false)
    public User saveOrUpdate(User user) {
    	return userRepository.save(user);
    }

	@Transactional(readOnly = false)
    public void delete(Long id) {
        userRepository.deleteById(id);
    }
}
