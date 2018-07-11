package upb.ida.util;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import upb.ida.bean.FilterOption;
import upb.ida.bean.FilterSort;

/**
 * Exposes util methods to perform filtering on a given dataset of type
 * List<Map<String, String>>
 * 
 * @author Nikit
 *
 */
@Component
public class FilterUtil {

	public static final int GREATER = 1;
	public static final int LESSER = -1;
	public static final int EQUAL = 0;

	/**
	 * Method to generate FilterOptions for First N record
	 * 
	 * @param n
	 *            - number of records needed
	 * @return FilterOptions instance
	 */
	public FilterOption getFirstNFilterOpt(int n) {
		FilterOption filterOption = new FilterOption(0, n);
		return filterOption;
	}

	/**
	 * Method to generate FilterOptions for Last N record
	 * 
	 * @param n
	 *            - number of records needed
	 * @param datasetSize
	 *            - size of the dataset
	 * @return FilterOptions instance
	 */
	public FilterOption getLastNFilterOpt(int n, int datasetSize) {
		FilterOption filterOption = new FilterOption(datasetSize - n - 1, datasetSize);
		return filterOption;
	}

	/**
	 * Method to generate FilterOptions for from n to n1 record
	 * 
	 * @param fromSeq
	 *            - index of record to start from (inclusive)
	 * @param toSeq
	 *            - index of record to end on (exclusive)
	 * @return FilterOptions instance
	 */
	public FilterOption getSubDatasetFilterOpt(int fromSeq, int toSeq) {
		FilterOption filterOption = new FilterOption(fromSeq, toSeq);
		return filterOption;
	}

	/**
	 * Method to generate FilterOptions for sorted top N records
	 * 
	 * @param fieldName
	 *            - name of the field to be filtered
	 * @param n
	 *            - number of records needed
	 * @param sortDirection
	 *            - direction of the sort
	 * @param isNumeric
	 *            - if field is numeric
	 * @return FilterOptions instance
	 */
	public FilterOption getSortedFirstNFilterOpt(String fieldName, int n, FilterSort sortDirection, boolean isNumeric) {
		FilterOption filterOption = new FilterOption(fieldName, sortDirection, 0, n, isNumeric);
		return filterOption;
	}

	/**
	 * Method to perform filter operation over a given dataset
	 * 
	 * @param data
	 *            - Data on which filter operation is to be performed
	 * @param filterOption
	 *            - criteria/configuration for filtering operation to be performed
	 * @return Filtered data list
	 * @throws NumberFormatException
	 * @throws ParseException
	 */
	public List<Map<String, String>> getFilteredData(List<Map<String, String>> data, FilterOption filterOption)
			throws NumberFormatException, ParseException {
		List<Map<String, String>> resList = null;
		// Run Sorting with length limit
		List<Map<String, String>> sortedData = data;
		if (filterOption.getSort() != null) {
			sortedData = getSortedData(data, filterOption);
		}
		// Trim the sorting results as per the sequence provided in the filteroption
		resList = getDataSection(sortedData, filterOption);
		return resList;
	}

	/**
	 * Method to get a sub section of a passed list of data
	 * 
	 * @param data
	 *            - data for which sub section is needed
	 * @param filterOption
	 *            - criteria for which section needs to be performed
	 * @return - sub list generated from the provided data
	 */
	private List<Map<String, String>> getDataSection(List<Map<String, String>> data, FilterOption filterOption) {
		List<Map<String, String>> res = data.subList(filterOption.getFromSeq(), filterOption.getToSeq());
		return res;
	}

	/**
	 * Method to perform sorting on a list upto a given limit
	 * 
	 * @param data
	 *            - data to perform sorting on
	 * @param filterOption
	 *            - criteria for sorting
	 * @return Sorted data list
	 * @throws NumberFormatException
	 * @throws ParseException
	 */
	private List<Map<String, String>> getSortedData(List<Map<String, String>> data, FilterOption filterOption)
			throws NumberFormatException, ParseException {
		// Implement selection sort to select the minimum first and stop when limit is
		// reached
		List<Map<String, String>> resList = new ArrayList<>();
		resList.addAll(data);
		String curSel;
		String curComp;
		String fieldName = filterOption.getFieldName();
		Integer limit = filterOption.getToSeq();
		// If no limit provided by user
		if (limit == null) {
			limit = data.size();
		}
		boolean isNumeric = filterOption.isNumeric();
		int matchNum = filterOption.getSort() == FilterSort.ASC ? GREATER : LESSER;
		Map<String, String> curEntry;
		Map<String, String> comEntry;
		// TODO: Fix sorting algorithm
		// Select elements sequentially
		for (int i = 0; i < limit; i++) {

			// Compare them with the ones ahead, switch positions if current selection is
			// bigger
			for (int j = i + 1; j < resList.size(); j++) {
				curEntry = resList.get(i);
				curSel = curEntry.get(fieldName);
				comEntry = resList.get(j);
				curComp = comEntry.get(fieldName);
				if (compareEntries(curSel, curComp, isNumeric) == matchNum) {
					// swap
					resList.set(i, comEntry);
					resList.set(j, curEntry);
				}
			}
		}
		return resList.subList(0, limit);
	}

	/**
	 * Method to compare two entries and return 1 if entry 1 is greater, -1 if
	 * entry1 is smaller and 0 is both are equal
	 * 
	 * @param entry1
	 *            - first entry
	 * @param entry2
	 *            - second entry
	 * @param isNumeric
	 *            - if the provided string is in numeric format
	 * @return comparison result
	 * @throws NumberFormatException
	 * @throws ParseException
	 */
	private int compareEntries(String entry1, String entry2, boolean isNumeric)
			throws NumberFormatException, ParseException {
		return isNumeric ? compareNumericEntries(entry1, entry2) : compareStrEntries(entry1, entry2);
	}

	/**
	 * Method to parse a given string to a double
	 * 
	 * @param entry
	 *            - string to be parsed
	 * @return parsed double value
	 * @throws NumberFormatException
	 * @throws ParseException
	 */
	private Double getDoubleVal(String entry) throws NumberFormatException, ParseException {
		Double res = Double.parseDouble(entry);
		return res;
	}

	/**
	 * Method to compare two numeric entries and return 1 if entry 1 is greater, -1
	 * if entry1 is smaller and 0 is both are equal
	 * 
	 * @param entry1
	 *            - first entry
	 * @param entry2
	 *            - second entry
	 * @return
	 * @throws NumberFormatException
	 * @throws ParseException
	 */
	private int compareNumericEntries(String entry1, String entry2) throws NumberFormatException, ParseException {
		int diff = 0;
		// parse into double
		double num1 = getDoubleVal(entry1);
		double num2 = getDoubleVal(entry2);
		double dblDiff = num1 - num2;
		if (dblDiff > 0) {
			diff = 1;
		} else if (dblDiff < 0) {
			diff = -1;
		}
		return diff;
	}

	/**
	 * Method to compare two string entries lexicographically and return 1 if entry
	 * 1 is greater, -1 if entry1 is smaller and 0 is both are equal
	 * 
	 * @param entry1
	 *            - first entry
	 * @param entry2
	 *            - second entry
	 * @return
	 */
	private int compareStrEntries(String entry1, String entry2) {
		int diff = 0;
		diff = StringUtils.compare(entry1, entry2);
		if (diff > 0) {
			diff = 1;
		} else if (diff < 0) {
			diff = -1;
		}
		return diff;
	}
}
