package upb.ida.security;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import upb.ida.domains.User;
import upb.ida.rest.UserController;
import upb.ida.service.UserService;

@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {
 
	@Autowired
	private UserService userService;
	
//	@Bean
//	public PasswordEncoder encoder() {
//	    return new BCryptPasswordEncoder();
//	}
	
    @Override
    public Authentication authenticate(Authentication authentication) 
      throws AuthenticationException {
    	
    	//name is username here
        String name = authentication.getName();
        String password = authentication.getCredentials().toString(); //12345
     
       // User currentUser = userService.getByUsername(name);
        User currentUser = userService.getByUsername(name); //12345hashcode
        try {
        	password = UserController.hashPassword(password);
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
       
        if (currentUser == null) return null;
        if(currentUser.getUsername().equals(name) && currentUser.getPassword().equals(password)) {
       // if(currentUser.getUsername().equals(name) && encoder().matches(password, currentUser.getPassword())) {
  
            List<GrantedAuthority> grantedAuths = new ArrayList<>();
            if(currentUser.getUserRole().equals("ADMIN"))
            	grantedAuths.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
            grantedAuths.add(new SimpleGrantedAuthority("ROLE_USER"));
            
            return new UsernamePasswordAuthenticationToken(
              name, password, grantedAuths);
        } else {
        	authentication.setAuthenticated(false);
            return authentication;
        }
    }
 
    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(
          UsernamePasswordAuthenticationToken.class);
    }
}