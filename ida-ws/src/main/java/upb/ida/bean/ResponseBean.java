package upb.ida.bean;

import java.util.Map;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import upb.ida.serializer.RespBeanSerializer;


@JsonSerialize(using = RespBeanSerializer.class)
public class ResponseBean {
	private int actnCode;
	private int errCode;
	private String errMsg;
	private Map<String, Object> payload;
	private String chatmsg;
	public int getErrCode() {
		return errCode;
	}
	public void setErrCode(int errCode) {
		this.errCode = errCode;
	}
	public String getErrMsg() {
		return errMsg;
	}
	public void setErrMsg(String errMsg) {
		this.errMsg = errMsg;
	}
	public Map<String, Object> getPayload() {
		return payload;
	}
	public void setPayload(Map<String, Object> payload) {
		this.payload = payload;
	}
	public int getActnCode() {
		return actnCode;
	}
	public void setActnCode(int actnCode) {
		this.actnCode = actnCode;
	}
	public String getChatmsg() {
		return chatmsg;
	}
	public void setChatmsg(String chatmsg) {
		this.chatmsg = chatmsg;
	}
	
}
