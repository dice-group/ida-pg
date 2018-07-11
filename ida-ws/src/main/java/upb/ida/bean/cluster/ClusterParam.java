package upb.ida.bean.cluster;

import java.util.List;

/**
 * ClusterParam is used to store the parameter details from Scikit
 * data-datadump.
 * 
 * @author Nikit
 *
 */
public class ClusterParam {
	/**
	 * name of the parameter
	 */
	private String name;
	/**
	 * types list of the parameter
	 */
	private List<String> type;
	/**
	 * if the parameter is optional
	 */
	private boolean optional;
	/**
	 * description of the parameter
	 */
	private String desc;

	/**
	 * @param name
	 *            - {@link ClusterParam#name}
	 * @param type
	 *            - {@link ClusterParam#type}
	 * @param optional
	 *            - {@link ClusterParam#optional}
	 */
	public ClusterParam(String name, List<String> type, boolean optional) {
		super();
		this.name = name;
		this.type = type;
		this.optional = optional;
	}

	/**
	 * @param name
	 *            - {@link ClusterParam#name}
	 * @param type
	 *            - {@link ClusterParam#type}
	 * @param optional
	 *            - {@link ClusterParam#optional}
	 * @param desc
	 *            - {@link ClusterParam#desc}
	 */
	public ClusterParam(String name, List<String> type, boolean optional, String desc) {
		super();
		this.name = name;
		this.type = type;
		this.optional = optional;
		this.desc = desc;
	}

	/**
	 * Gets the {@link ClusterParam#name}
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the {@link ClusterParam#name}
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Gets the {@link ClusterParam#type}
	 */
	public List<String> getType() {
		return type;
	}

	/**
	 * Sets the {@link ClusterParam#type}
	 */
	public void setType(List<String> type) {
		this.type = type;
	}

	/**
	 * Gets the {@link ClusterParam#optional}
	 */
	public boolean isOptional() {
		return optional;
	}

	/**
	 * Sets the {@link ClusterParam#optional}
	 */
	public void setOptional(boolean optional) {
		this.optional = optional;
	}

	/**
	 * Gets the {@link ClusterParam#desc}
	 */
	public String getDesc() {
		return desc;
	}

	/**
	 * Sets the {@link ClusterParam#desc}
	 */
	public void setDesc(String desc) {
		this.desc = desc;
	}

}
