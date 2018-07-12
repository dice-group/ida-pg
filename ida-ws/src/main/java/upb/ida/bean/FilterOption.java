package upb.ida.bean;

/**
 * Class to help encapsulate the configurational details of a Data filtering
 * operation
 * 
 * @author Nikit
 *
 */
public class FilterOption {
	/**
	 * Name of the Field for which the sort operation is to be performed
	 */
	private String fieldName;
	/**
	 * Direction of the sort
	 */
	private FilterSort sort;
	/**
	 * index of the record from which selection is started (inclusive)
	 */
	private Integer fromSeq;
	/**
	 * index of the record at which selection should end (exclusive)
	 */
	private Integer toSeq;
	/**
	 * if the field is of Numeric type or else
	 */
	private boolean isNumeric;

	/**
	 * For only field level sorting
	 * 
	 * @param fieldName
	 *            - name of the field
	 * 
	 * @param sort
	 *            - direction of the sort
	 * 
	 * @param isNumeric
	 *            - if the field is of numeric type
	 */
	public FilterOption(String fieldName, FilterSort sort, boolean isNumeric) {
		super();
		this.fieldName = fieldName;
		this.sort = sort;
		this.isNumeric = isNumeric;
	}

	/**
	 * For custom sub selection
	 * 
	 * @param fieldName
	 *            - name of the field
	 * @param fromSeq
	 *            - Index of the starting record (inclusive)
	 * @param toSeq
	 *            - Index of the end record (exclusive)
	 * 
	 * @param isNumeric
	 *            - if the field is of numeric type
	 */
	public FilterOption(String fieldName, Integer fromSeq, Integer toSeq, boolean isNumeric) {
		super();
		this.fieldName = fieldName;
		this.fromSeq = fromSeq;
		this.toSeq = toSeq;
		this.isNumeric = isNumeric;
	}

	/**
	 * For custom sub selection
	 * 
	 * @param fromSeq
	 *            - Index of the starting record (inclusive)
	 * @param toSeq
	 *            - Index of the end record (exclusive)
	 * 
	 * @param isNumeric
	 *            - if the field is of numeric type
	 */
	public FilterOption(Integer fromSeq, Integer toSeq) {
		super();
		this.fromSeq = fromSeq;
		this.toSeq = toSeq;
	}

	/**
	 * For sorting and custom sub selection together
	 * 
	 * @param fieldName
	 *            - name of the field
	 * @param sort
	 *            - direction of the sort
	 * @param fromSeq
	 *            - Index of the starting record (inclusive)
	 * @param toSeq
	 *            - Index of the end record (exclusive)
	 * 
	 * @param isNumeric
	 *            - if the field is of numeric type
	 */
	public FilterOption(String fieldName, FilterSort sort, Integer fromSeq, Integer toSeq, boolean isNumeric) {
		super();
		this.fieldName = fieldName;
		this.sort = sort;
		this.fromSeq = fromSeq;
		this.toSeq = toSeq;
		this.isNumeric = isNumeric;
	}

	/**
	 * Gets the {@link FilterOption#fieldName}
	 */
	public String getFieldName() {
		return fieldName;
	}

	/**
	 * Sets the {@link FilterOption#fieldName}
	 */
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	/**
	 * Gets the {@link FilterOption#sort}
	 */
	public FilterSort getSort() {
		return sort;
	}

	/**
	 * Sets the {@link FilterOption#sort}
	 */
	public void setSort(FilterSort sort) {
		this.sort = sort;
	}

	/**
	 * Gets the {@link FilterOption#fromSeq}
	 */
	public Integer getFromSeq() {
		return fromSeq;
	}

	/**
	 * Sets the {@link FilterOption#fromSeq}
	 */
	public void setFromSeq(Integer fromSeq) {
		this.fromSeq = fromSeq;
	}

	/**
	 * Gets the {@link FilterOption#toSeq}
	 */
	public Integer getToSeq() {
		return toSeq;
	}

	/**
	 * Sets the {@link FilterOption#toSeq}
	 */
	public void setToSeq(Integer toSeq) {
		this.toSeq = toSeq;
	}

	/**
	 * Gets the {@link FilterOption#isNumeric}
	 */
	public boolean isNumeric() {
		return isNumeric;
	}

	/**
	 * Sets the {@link FilterOption#isNumeric}
	 */
	public void setNumeric(boolean isNumeric) {
		this.isNumeric = isNumeric;
	}

}
