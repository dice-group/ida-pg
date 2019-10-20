package upb.ida.service;

import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;

import upb.ida.domains.User;
import upb.ida.rest.UserController;


@Service
public class UserServiceUtil {


    public static User getByUsername(String username) {
		return (User) UserController.list(username);

    }

}
