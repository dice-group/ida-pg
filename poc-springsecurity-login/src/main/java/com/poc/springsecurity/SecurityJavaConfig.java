package com.poc.springsecurity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@ComponentScan("com.poc.springsecurity")
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
          .authorizeRequests().antMatchers("/*").access("hasRole('USER')").and().formLogin()
		  .loginProcessingUrl("/login_check").usernameParameter("username").passwordParameter("password")
		  .successHandler(mySuccessHandler).failureHandler(myFailureHandler)
		  .and()
          .httpBasic();
    }
}
