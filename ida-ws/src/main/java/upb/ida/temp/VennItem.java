package upb.ida.temp;

import java.util.Set;

public class VennItem<T> {
	private Set<T> sets;
	private int size;
	private String label;

	public VennItem(Set<T> itersectionSet, int count, String label) {
		super();
		this.sets = itersectionSet;
		this.size = count;
		this.label = label;
	}

	// TODO: Combine labels in case label and sets depiction values are different
	public Set<T> getSets() {
		return sets;
	}

	public void setSets(Set<T> sets) {
		this.sets = sets;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	@Override
	public String toString() {
		return "{ sets:" + sets + ", size:" + size + (label == null ? "" : ", label:\"" + label + "\"") + "}";
	}

}
