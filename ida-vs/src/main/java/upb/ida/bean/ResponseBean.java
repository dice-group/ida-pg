package upb.ida.bean;

import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import upb.ida.serializer.RespBeanSerializer;

/**
 * ResponseBean is used as a uniform response format for the incoming REST calls
 *
 */
@JsonSerialize(using = RespBeanSerializer.class)
@Component
@SessionScope
public class ResponseBean {
	/**
	 * Payload contains the data map to be sent
	 */
	private Map<String, Object> payload;
	/**
	 * Chat Message for the request
	 */
	public Map<String, Object> getPayload() {
		return payload;
	}
	/**
	 * Sets the {@link ResponseBean#payload}
	 */
	public void setPayload(Map<String, Object> payload) {
		this.payload = payload;
	}
		
}
