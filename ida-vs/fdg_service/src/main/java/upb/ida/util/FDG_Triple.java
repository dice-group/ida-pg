package upb.ida.util;

/**
 * Describes the attributes of a Node-Edge-Node Triple in a Force Directed Graph
 * 
 * @author Nikit
 *
 */
public class FDG_Triple {
	/**
	 * id of the triple
	 */
	private Integer id;
	/**
	 * sourceNode of the triple
	 */
	private FDG_Node sourceNode;
	/**
	 * targetNode of the triple
	 */
	private FDG_Node targetNode;
	/**
	 * strngthVal of the triple
	 */
	private Double strngthVal;
	/**
	 * label of the triple
	 */
	private String label;

	/**
	 * @param id
	 *            - {@link FDG_Triple#id}
	 * @param sourceNode
	 *            - {@link FDG_Triple#sourceNode}
	 * @param targetNode
	 *            - {@link FDG_Triple#targetNode}
	 * @param strngthVal
	 *            - {@link FDG_Triple#strngthVal}
	 */
	public FDG_Triple(Integer id, FDG_Node sourceNode, FDG_Node targetNode, Double strngthVal) {
		super();
		this.id = id;
		this.sourceNode = sourceNode;
		this.targetNode = targetNode;
		this.strngthVal = strngthVal;
	}

	/**
	 * Gets the {@link FDG_Triple#sourceNode}
	 */
	public FDG_Node getSourceNode() {
		return sourceNode;
	}

	/**
	 * Sets the {@link FDG_Triple#sourceNode}
	 */
	public void setSourceNode(FDG_Node sourceNode) {
		this.sourceNode = sourceNode;
	}

	/**
	 * Gets the {@link FDG_Triple#targetNode}
	 */
	public FDG_Node getTargetNode() {
		return targetNode;
	}

	/**
	 * Sets the {@link FDG_Triple#targetNode}
	 */
	public void setTargetNode(FDG_Node targetNode) {
		this.targetNode = targetNode;
	}

	/**
	 * Gets the {@link FDG_Triple#strngthVal}
	 */
	public Double getStrngthVal() {
		return strngthVal;
	}

	/**
	 * Sets the {@link FDG_Triple#strngthVal}
	 */
	public void setStrngthVal(Double str_val) {
		this.strngthVal = str_val;
	}

	/**
	 * Gets the {@link FDG_Triple#label}
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * Sets the {@link FDG_Triple#label}
	 */
	public void setLabel(String label) {
		this.label = label;
	}

	/**
	 * Gets the {@link FDG_Triple#id}
	 */
	public Integer getId() {
		return id;
	}

	/**
	 * Sets the {@link FDG_Triple#id}
	 */
	public void setId(Integer id) {
		this.id = id;
	}

}
