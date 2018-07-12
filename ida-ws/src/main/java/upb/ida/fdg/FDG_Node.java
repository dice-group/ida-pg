package upb.ida.fdg;

/**
 * Describes the attributes of a Node in a Force Directed Graph
 * 
 * @author Nikit
 *
 */
public class FDG_Node {
	/**
	 * id of the node
	 */
	private Integer id;
	/**
	 * label of the node
	 */
	private String label;
	/**
	 * group of the node
	 */
	private Integer group;

	/**
	 * @param id
	 *            - {@link FDG_Node#id}
	 * @param label
	 *            - {@link FDG_Node#label}
	 * @param group
	 *            - {@link FDG_Node#group}
	 */
	public FDG_Node(Integer id, String label, Integer group) {
		super();
		this.id = id;
		this.label = label;
		this.group = group;
	}

	/**
	 * Gets the {@link FDG_Node#id}
	 */
	public Integer getId() {
		return id;
	}

	/**
	 * Sets the {@link FDG_Node#id}
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * Gets the {@link FDG_Node#label}
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * Sets the {@link FDG_Node#label}
	 */
	public void setLabel(String label) {
		this.label = label;
	}

	/**
	 * Gets the {@link FDG_Node#group}
	 */
	public Integer getGroup() {
		return group;
	}

	/**
	 * Sets the {@link FDG_Node#group}
	 */
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
