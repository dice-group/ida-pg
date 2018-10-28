package upb.ida.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;

import com.fasterxml.jackson.databind.ObjectMapper;

import upb.ida.bean.ResponseBean;

@Configuration
public class BeanConfig {
	@Bean
	@Scope(value = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)
	public ResponseBean responseBean() {
		return new ResponseBean();
	}
	
	@Bean
	@Scope(value = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)
	public ObjectMapper objectMapper() {
		return new ObjectMapper();
	}
}