package upb.ida.util;

import java.util.Comparator;

import org.springframework.stereotype.Component;

@Component
public class StringLengthComparator implements Comparator<String> {

	@Override
	public int compare(String o1, String o2) {
		return o1.length() - o2.length();
	}

}
