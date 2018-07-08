package upb.ida.bean.cluster;

import java.util.List;

public class ParamEntryChecker {
	private String paramName;
	private String paramValue;
	private boolean provided;
	private List<String> userParList;

	public ParamEntryChecker(String paramName, String paramValue, boolean provided,List<String> userParList) {
		super();
		this.paramName = paramName;
		this.paramValue = paramValue;
		this.provided = provided;
		this.userParList = userParList;
	}

	public List<String> getUserParList() {
		return userParList;
	}

	public void setUserParList(List<String> userParList) {
		this.userParList = userParList;
	}

	public String getParamName() {
		return paramName;
	}

	public void setParamName(String paramName) {
		this.paramName = paramName;
	}

	public String getParamValue() {
		return paramValue;
	}

	public void setParamValue(String paramValue) {
		this.paramValue = paramValue;
	}

	public boolean isProvided() {
		return provided;
	}

	public void setProvided(boolean provided) {
		this.provided = provided;
	}

}
