package upb.ida.bean.cluster;

import java.util.ArrayList;
import java.util.List;

/**
 * Encapsulation of the details for a clustering algorithm extracted from Scikit
 * data-datadump.
 * 
 * @author Nikit
 *
 */
public class ClusterAlgoDesc {
	/**
	 * id of the clustering algorithm
	 */
	private int id;
	/**
	 * function name of the clustering algorithm
	 */
	private String fnName;
	/**
	 * brief description of the clustering algorithm
	 */
	private String fnDesc;
	/**
	 * Long description of the clustering algorithm
	 */
	private String note;
	/**
	 * List of parameters of the clustering algorithm
	 */
	private List<ClusterParam> params;

	/**
	 * 
	 * @param id
	 *            - {@link ClusterAlgoDesc#fieldName}
	 * @param fnName
	 *            - {@link ClusterAlgoDesc#fnName}
	 * @param fnDesc
	 *            - {@link ClusterAlgoDesc#fnDesc}
	 * @param note
	 *            - {@link ClusterAlgoDesc#note}
	 * @param params
	 *            - {@link ClusterAlgoDesc#params}
	 */
	public ClusterAlgoDesc(int id, String fnName, String fnDesc, String note, List<ClusterParam> params) {
		super();
		this.id = id;
		this.fnName = fnName;
		this.fnDesc = fnDesc;
		this.note = note;
		this.params = params;
	}

	/**
	 * Gets the {@link ClusterAlgoDesc#id}
	 */
	public int getId() {
		return id;
	}

	/**
	 * Sets the {@link ClusterAlgoDesc#id}
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Gets the {@link ClusterAlgoDesc#fnName}
	 */
	public String getFnName() {
		return fnName;
	}

	/**
	 * Sets the {@link ClusterAlgoDesc#fnName}
	 */
	public void setFnName(String fnName) {
		this.fnName = fnName;
	}

	/**
	 * Gets the {@link ClusterAlgoDesc#fnDesc}
	 */
	public String getFnDesc() {
		return fnDesc;
	}

	/**
	 * Sets the {@link ClusterAlgoDesc#fnDesc}
	 */
	public void setFnDesc(String fnDesc) {
		this.fnDesc = fnDesc;
	}

	/**
	 * Gets the {@link ClusterAlgoDesc#note}
	 */
	public String getNote() {
		return note;
	}

	/**
	 * Sets the {@link ClusterAlgoDesc#note}
	 */
	public void setNote(String note) {
		this.note = note;
	}

	/**
	 * Gets the {@link ClusterAlgoDesc#params}
	 */
	public List<ClusterParam> getParams() {
		if (params == null) {
			params = new ArrayList<>();
		}
		return params;
	}

	/**
	 * Sets the {@link ClusterAlgoDesc#params}
	 */
	public void setParams(List<ClusterParam> params) {
		this.params = params;
	}

}
