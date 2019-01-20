package upb.ida.security;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityJavaConfig extends WebSecurityConfigurerAdapter  {
	
	@Autowired(required=true)
	private CustomAuthenticationSuccessHandler mySuccessHandler;

	@Autowired(required=true)
	private CustomAuthenticationFailureHandler myFailureHandler;
	
    @Autowired
    private CustomAuthenticationProvider authProvider;
    
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    	auth.authenticationProvider(authProvider);
    }
	
    @Override
    protected void configure(HttpSecurity http) 
      throws Exception {
        http.csrf().disable()
          .authorizeRequests()
          .antMatchers("/main/**").authenticated()
          .antMatchers("/admin/user/**").access("hasRole('ADMIN')")
          .and().formLogin()
		  .loginProcessingUrl("/auth/login-action").usernameParameter("username").passwordParameter("password")
		  .successHandler(mySuccessHandler)
		  .failureHandler(myFailureHandler)
		  .and().exceptionHandling().authenticationEntryPoint(authenticationEntryPoint())
		  .and().logout().logoutRequestMatcher(new AntPathRequestMatcher("/auth/logout"))
		  .logoutSuccessUrl("/auth/response?logout")
		  .and()
          .httpBasic();
    }
    
    private AuthenticationEntryPoint authenticationEntryPoint() {
        return new AuthenticationEntryPoint() {
            @Override
            public void commence(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
                    AuthenticationException e) throws IOException, ServletException {
                RequestDispatcher rd = httpServletRequest.getRequestDispatcher("/auth/response");
                rd.forward(httpServletRequest, httpServletResponse);
            }
        };
    }
}
