package upb.ida.bean.cluster;

import java.util.List;

public class CollectedParams {
	private String paramName;
	private String paramValue;

	public CollectedParams(String paramName, String paramValue) {
		super();
		this.paramName = paramName;
		this.paramValue = paramValue;
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

}
