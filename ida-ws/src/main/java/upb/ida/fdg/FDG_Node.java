package upb.ida.fdg;


public class FDG_Node {
	private Integer id;
	private String label;
	private Integer group;
	
	public FDG_Node(Integer id, String label, Integer group) {
		super();
		this.id = id;
		this.label = label;
		this.group = group;
	}
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public Integer getGroup() {
		return group;
	}
	public void setGroup(Integer group) {
		this.group = group;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FDG_Node other = (FDG_Node) obj;
		return id == other.id;
	}
}
