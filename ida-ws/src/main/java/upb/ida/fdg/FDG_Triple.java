package upb.ida.fdg;

public class FDG_Triple {

	private Integer id;
	private FDG_Node sourceNode;
	private FDG_Node targetNode;
	private Double str_val;
	private String label;

	public FDG_Triple(Integer id, FDG_Node sourceNode, FDG_Node targetNode, Double str_val) {
		super();
		this.id = id;
		this.sourceNode = sourceNode;
		this.targetNode = targetNode;
		this.str_val = str_val;
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

	public Double getStrVal() {
		return str_val;
	}

	public void setStrVal(Double str_val) {
		this.str_val = str_val;
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
