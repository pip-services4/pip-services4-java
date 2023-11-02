package org.pipservices4.data.query;

/**
 * Defines a field name and order used to sort query results.
 * <p>
 * ### Example ###
 * <pre>
 * {@code
 * FilterParams filter = FilterParams.fromTuples("type", "Type1");
 * PagingParams paging = new PagingParams(0, 100);
 * SortingParams sorting = new SortingParams(new SortField("create_time", true));
 * 
 * myDataClient.getDataByFilter(filter, paging, sorting);
 * }
 * </pre>
 * @see SortParams
 */
public class SortField {
	/** The field name to sort by */
	private String _name;
	/** The flag to define sorting order. True to sort ascending, false to sort descending */
	private boolean _ascending = true;
	
	public SortField() {}
	
	/**
	 * Creates a new instance and assigns its values.
	 * 
	 * @param name 			the name of the field to sort by.
	 * @param ascending 	true to sort in ascending order, and false to sort in descending order. 
	 */
	public SortField(String name, boolean ascending) {
		_name = name;
		_ascending = ascending;
	}

	public SortField(String name) {
		_name = name;
	}
	
	public String getName() { return _name; }
	public void setName(String value) { _name = value; }
	
	public boolean isAscending() { return _ascending; }
	public void setAscending(boolean value) { _ascending = value; }
}
