package upb.ida.security;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import upb.ida.domains.User;
import upb.ida.rest.UserController;
import upb.ida.service.UserService;

@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {
 	
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
    	
    	//name is username here
        String name = authentication.getName();
        String password = authentication.getCredentials().toString();
        
        System.out.println("password from authentication before hashing"+password);
        try {
			password = UserController.hashPassword(password);
		} catch (NoSuchAlgorithmException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        System.out.println("password from authentication after hashing"+password);
        
        System.out.println("username entered by user   "+name);
        System.out.println("password entered by user    "+password);        
        
       // User currentUser = userService.getByUsername(name);
        User currentUser = UserService.getByUsername(name); 
        System.out.println("username databse "+currentUser.getUsername());
        System.out.println("password database by user    "+currentUser.getPassword());        
       
        if(currentUser.getUsername().equals(name) && currentUser.getPassword().equals(password)) {
        	
            List<GrantedAuthority> grantedAuths = new ArrayList<>();
            if(currentUser.getUserRole().equals("ADMIN"))
            	grantedAuths.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
            grantedAuths.add(new SimpleGrantedAuthority("ROLE_USER"));
            
            return new UsernamePasswordAuthenticationToken(
              name, password, grantedAuths);
        } else {
        	return null;
        }
    }
 
    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(
          UsernamePasswordAuthenticationToken.class);
    }
}