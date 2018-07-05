package upb.ida.bean.cluster;

import java.util.ArrayList;
import java.util.List;

public class ClusterAlgoDesc {
	
	private int id;
	private String fnName;
	private String fnDesc;
	private String note;
	private List<ClusterParam> params;
	
	public ClusterAlgoDesc(int id, String fnName, String fnDesc, String note, List<ClusterParam> params) {
		super();
		this.id = id;
		this.fnName = fnName;
		this.fnDesc = fnDesc;
		this.note = note;
		this.params = params;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getFnName() {
		return fnName;
	}
	public void setFnName(String fnName) {
		this.fnName = fnName;
	}
	public String getFnDesc() {
		return fnDesc;
	}
	public void setFnDesc(String fnDesc) {
		this.fnDesc = fnDesc;
	}
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	}
	public List<ClusterParam> getParams() {
		if(params == null) {
			params = new ArrayList<>();
		}
		return params;
	}
	public void setParams(List<ClusterParam> params) {
		this.params = params;
	}

}
