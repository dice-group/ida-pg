package upb.ida.security;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import upb.ida.bean.User;
import upb.ida.service.UserService;

@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {
 
	@Autowired
	private UserService userService;
	
    @Override
    public Authentication authenticate(Authentication authentication) 
      throws AuthenticationException {
    	
        String name = authentication.getName();
        String password = authentication.getCredentials().toString();
        
        User currentUser = userService.getByUsername(name);
       
        if (currentUser == null) return null;
        if(currentUser.getUsername().equals(name) && currentUser.getPassword().equals(password)) {//shouldAuthenticateAgainstThirdPartySystem()) {
  
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