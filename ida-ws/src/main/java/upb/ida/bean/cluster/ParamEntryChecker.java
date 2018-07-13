package upb.ida.bean.cluster;

import java.util.List;
/**
 * ParamEntryChecker is used to store paramter values
 * for clustering algorithm provided by the User.
 * 
 * @author Faisal
 *
 */
public class ParamEntryChecker {
	/**
	 * name of the parameter
	 */
	private String paramName;
	/**
	 * Value of the parameter
	 */
	private String paramValue;
	/**
	 * if the parameter value exists
	 */
	private boolean provided;
	/**
	 * list of user selected Parameters
	 */
	private List<String> userParList;
	/**
	 * @param paramNname
	 *            - {@link ParamEntryChecker#paramName}
	 * @param paramValue
	 *            - {@link ParamEntryChecker#paramValue}
	 * @param provided
	 *            - {@link ParamEntryChecker#provided}
	 * @param userParList
	 *            - {@link ParamEntryChecker#userParList}
	 */
	public ParamEntryChecker(String paramName, String paramValue, boolean provided,List<String> userParList) {
		super();
		this.paramName = paramName;
		this.paramValue = paramValue;
		this.provided = provided;
		this.userParList = userParList;
	}

	/**
	 * Gets the {@link ParamEntryChecker#userParList}
	 */
	public List<String> getUserParList() {
		return userParList;
	}
	/**
	 * Sets the {@link ParamEntryChecker#userParList}
	 */
	public void setUserParList(List<String> userParList) {
		this.userParList = userParList;
	}
	/**
	 * Gets the {@link ParamEntryChecker#paramName}
	 */
	public String getParamName() {
		return paramName;
	}
	/**
	 * Sets the {@link ParamEntryChecker#paramName}
	 */
	public void setParamName(String paramName) {
		this.paramName = paramName;
	}
	/**
	 * Gets the {@link ParamEntryChecker#paramValue}
	 */
	public String getParamValue() {
		return paramValue;
	}
	/**
	 * Sets the {@link ParamEntryChecker#paramValue}
	 */
	public void setParamValue(String paramValue) {
		this.paramValue = paramValue;
	}
	/**
	 * Gets the {@link ParamEntryChecker#provided}
	 */
	public boolean isProvided() {
		return provided;
	}
	/**
	 * Sets the {@link ParamEntryChecker#provided}
	 */
	public void setProvided(boolean provided) {
		this.provided = provided;
	}

}
