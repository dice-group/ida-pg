package upb.ida.bean;

import java.util.Map;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import upb.ida.serializer.RespBeanSerializer;

/**
 * ResponseBean is used as a uniform response format for the incoming REST calls
 * @author Nikit
 *
 */
@JsonSerialize(using = RespBeanSerializer.class)
public class ResponseBean {
	/**
	 * Action code is used to signal what to do with the data in the response
	 */
	private int actnCode;
	/**
	 * Error code is used to signal over what type of error (if any) happened for the request made
	 */
	private int errCode;
	/**
	 * Error message contains the brief description of the error (if any)
	 */
	private String errMsg;
	/**
	 * Payload contains the data map to be sent
	 */
	private Map<String, Object> payload;
	/**
	 * Chat Message for the request
	 */
	private String chatmsg;
	/**
	 * Gets the {@link ResponseBean#errCode}
	 */
	public int getErrCode() {
		return errCode;
	}
	/**
	 * Sets the {@link ResponseBean#errCode}
	 */
	public void setErrCode(int errCode) {
		this.errCode = errCode;
	}
	/**
	 * Gets the {@link ResponseBean#errMsg}
	 */
	public String getErrMsg() {
		return errMsg;
	}
	/**
	 * Sets the {@link ResponseBean#errMsg}
	 */
	public void setErrMsg(String errMsg) {
		this.errMsg = errMsg;
	}
	/**
	 * Gets the {@link ResponseBean#payload}
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
	/**
	 * Gets the {@link ResponseBean#actnCode}
	 */
	public int getActnCode() {
		return actnCode;
	}
	/**
	 * Sets the {@link ResponseBean#actnCode}
	 */
	public void setActnCode(int actnCode) {
		this.actnCode = actnCode;
	}
	/**
	 * Gets the {@link ResponseBean#chatmsg}
	 */
	public String getChatmsg() {
		return chatmsg;
	}
	/**
	 * Sets the {@link ResponseBean#chatmsg}
	 */
	public void setChatmsg(String chatmsg) {
		this.chatmsg = chatmsg;
	}
	@Override
	public String toString() {
		return "ResponseBean [actnCode=" + actnCode + ", errCode=" + errCode + ", errMsg=" + errMsg + ", payload="
				+ payload + ", chatmsg=" + chatmsg + "]";
	}
}
