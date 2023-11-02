package org.pipservices4.data.query;

import org.pipservices4.commons.convert.*;
import org.pipservices4.commons.data.AnyValueMap;
import org.pipservices4.commons.data.StringValueMap;

/**
 * Data transfer object to pass paging parameters for queries.
 * The page is defined by two parameters.
 * The <code>skip</code> parameter defines number of items to skip.
 * The <code>paging</code> parameter sets how many items to return in a page.
 * And the optional <code>total</code> parameter tells to return total number of items in the query.
 * <p>
 * Remember: not all implementations support <code>total</code> parameter
 * because its generation may lead to severe performance implications.
 * <p>
 * ### Example ###
 * <pre>
 * {@code
 * FilterParams filter = FilterParams.fromTuples("type", "Type1");
 * PagingParams paging = new PagingParams(0, 100);
 * 
 * myDataClient.getDataByFilter(filter, paging);
 * }
 * </pre>
 */
public class PagingParams {
	/** The number of items to skip. */
	private Long _skip;
	/** The number of items to return. */
	private Long _take;
	/** The flag to return the total number of items. */
	private boolean _total;

	public PagingParams() {
	}

	/**
	 * Creates a new instance and sets its values.
	 * 
	 * @param skip  the number of items to skip.
	 * @param take  the number of items to return.
	 * @param total true to return the total number of items.
	 */
	public PagingParams(Object skip, Object take, Object total) {
		_skip = LongConverter.toNullableLong(skip);
		_take = LongConverter.toNullableLong(take);
		_total = BooleanConverter.toBooleanWithDefault(total, false);

		// This is for correctly using PagingParams with gRPC. gRPC defaults to 0 when take is null,
		// so we have to set it back to null if we get 0 in the constructor.
		if (_take != null && _take == 0)
			_take = null;

	}

	/**
	 * Gets the number of items to skip.
	 * 
	 * @return the number of items to skip.
	 */
	public Long getSkip() {
		return _skip;
	}

	/**
	 * Gets the number of items to skip.
	 * 
	 * @param minSkip the minimum number of items to skip.
	 * @return the number of items to skip.
	 */
	public long getSkip(long minSkip) {
		if (_skip == null)
			return minSkip;
		if (_skip < minSkip)
			return minSkip;
		return _skip;
	}

	/**
	 * Gets the number of items to return in a page.
	 * 
	 * @return the number of items to return.
	 */
	public Long getTake() {
		return _take;
	}

	/**
	 * Sets value to skip
	 * 
	 * @param value value to set skip
	 */
	public void setSkip(long value) {
		_skip = value;
	}

	/**
	 * Gets the number of items to return in a page.
	 * 
	 * @param maxTake the maximum number of items to return.
	 * @return the number of items to return.
	 */
	public long getTake(long maxTake) {
		if (_take == null)
			return maxTake;
		if (_take < 0)
			return 0;
		if (_take > maxTake)
			return maxTake;
		return _take;
	}

	/**
	 * Sets value to take
	 * 
	 * @param value value to set take
	 */
	public void setTake(long value) {
		_take = value;
	}

	/**
	 * 
	 * @return value of total
	 */
	public boolean hasTotal() {
		return _total;
	}

	/**
	 * Sets value to total
	 * 
	 * @param value value to set total
	 */
	public void setTotal(boolean value) {
		_total = value;
	}

	/**
	 * Converts specified value into PagingParams.
	 * 
	 * @param value value to be converted
	 * @return a newly created PagingParams.
	 */
	public static PagingParams fromValue(Object value) {
		if (value instanceof PagingParams)
			return (PagingParams) value;

		AnyValueMap map = AnyValueMap.fromValue(value);
		return PagingParams.fromMap(map);
	}

	/**
	 * Creates a new PagingParams from a list of key-value pairs called tuples.
	 * 
	 * @param tuples a list of values where odd elements are keys and the following
	 *               even elements are values
	 * @return a newly created PagingParams.
	 */
	public static PagingParams fromTuples(Object... tuples) {
		AnyValueMap map = AnyValueMap.fromTuples(tuples);
		return PagingParams.fromMap(map);
	}

	/**
	 * Creates a new PagingParams and sets it parameters from the AnyValueMap map
	 * 
	 * @param map a AnyValueMap to initialize this PagingParams
	 * @return a newly created PagingParams.
	 */
	public static PagingParams fromMap(AnyValueMap map) {
		Long skip = map.getAsNullableLong("skip");
		Long take = map.getAsNullableLong("take");
		boolean total = map.getAsBooleanWithDefault("total", false);
		return new PagingParams(skip, take, total);
	}

	/**
	 * Creates a new PagingParams and sets it parameters from the StringValueMap map
	 * 
	 * @param map a StringValueMap to initialize this PagingParams
	 * @return a newly created PagingParams.
	 */
	public static PagingParams fromMap(StringValueMap map) {
		Long skip = map.getAsNullableLong("skip");
		Long take = map.getAsNullableLong("take");
		boolean total = map.getAsBooleanWithDefault("total", true);
		return new PagingParams(skip, take, total);
	}
}
