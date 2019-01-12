package upb.ida.venndiagram;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class VENN_DATA_GENERATOR<T, U> {
	private Map<T, Set<U>> dataMap;

	public VENN_DATA_GENERATOR(Map<T, Set<U>> dataMap) {
		super();
		this.dataMap = dataMap;
	}

	public Set<VENN_ITEM<T>> generateVennItems() {
		Set<VENN_ITEM<T>> vennItems = new HashSet<>();
		Set<Set<T>> powerSet = powerSet(dataMap.keySet());
		String label;
		for (Set<T> entry : powerSet) {
			if (entry.isEmpty()) {
				continue;
			} else if (entry.size() == 1) {
				label = entry.iterator().next().toString();
			} else {
				label = null;
			}
			Set<U> tempSet = null;
			for (T item : entry) {
				Set<U> curSet = dataMap.get(item);
				if (tempSet == null) {
					tempSet = new HashSet<>();
					tempSet.addAll(curSet);
				} else {
					tempSet.retainAll(curSet);
				}
				if (tempSet.isEmpty()) {
					break;
				}
			}
			if (tempSet.size() > 0) {
				VENN_ITEM<T> vennItem = new VENN_ITEM<>(entry, tempSet.size(), label);
				vennItems.add(vennItem);
			}
		}
		return vennItems;
	}

	public static <T> Set<Set<T>> powerSet(Set<T> originalSet) {
		Set<Set<T>> sets = new HashSet<Set<T>>();
		if (originalSet.isEmpty()) {
			sets.add(new HashSet<T>());
			return sets;
		}
		List<T> list = new ArrayList<T>(originalSet);
		T head = list.get(0);
		Set<T> rest = new HashSet<T>(list.subList(1, list.size()));
		for (Set<T> set : powerSet(rest)) {
			Set<T> newSet = new HashSet<T>();
			newSet.add(head);
			newSet.addAll(set);
			sets.add(newSet);
			sets.add(set);
		}
		return sets;
	}

}
