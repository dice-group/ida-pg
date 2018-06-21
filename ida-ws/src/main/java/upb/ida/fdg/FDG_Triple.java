package upb.ida.fdg;

public class FDG_Triple {

	private Integer id;
	private FDG_Node sourceNode;
	private FDG_Node targetNode;
	private Double strngthVal;
	private String label;

	public FDG_Triple(Integer id, FDG_Node sourceNode, FDG_Node targetNode, Double strngthVal) {
		super();
		this.id = id;
		this.sourceNode = sourceNode;
		this.targetNode = targetNode;
		this.strngthVal = strngthVal;
	}

	public FDG_Node getSourceNode() {
		return sourceNode;
	}

	public void setSourceNode(FDG_Node sourceNode) {
		this.sourceNode = sourceNode;
	}

	public FDG_Node getTargetNode() {
		return targetNode;
	}

	public void setTargetNode(FDG_Node targetNode) {
		this.targetNode = targetNode;
	}
  
	public Double getStrngthVal() {
		return strngthVal;
	}

	public void setStrngthVal(Double str_val) {
		this.strngthVal = str_val;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

}
