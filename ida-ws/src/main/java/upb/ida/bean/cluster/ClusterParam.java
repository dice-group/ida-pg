package upb.ida.bean.cluster;

import java.util.List;

public class ClusterParam {
	private String name;
	private List<String> type;
	private boolean optional;
	private String desc;

	public ClusterParam(String name, List<String> type, boolean optional) {
		super();
		this.name = name;
		this.type = type;
		this.optional = optional;
	}

	public ClusterParam(String name, List<String> type, boolean optional, String desc) {
		super();
		this.name = name;
		this.type = type;
		this.optional = optional;
		this.desc = desc;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<String> getType() {
		return type;
	}

	public void setType(List<String> type) {
		this.type = type;
	}

	public boolean isOptional() {
		return optional;
	}

	public void setOptional(boolean optional) {
		this.optional = optional;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

}
