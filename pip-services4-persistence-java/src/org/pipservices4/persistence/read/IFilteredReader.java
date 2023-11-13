package org.pipservices4.persistence.read;

import org.pipservices4.components.context.IContext;
import org.pipservices4.data.query.FilterParams;
import org.pipservices4.data.query.SortParams;
import org.pipservices4.commons.errors.ApplicationException;

import java.util.List;

/**
 * Interface for data processing components that can retrieve a list of data items by filter.
 */
public interface IFilteredReader<T> {
	/**
	 * Gets a list of data items using filter parameters.
	 * 
	 * @param context     (optional) a context to trace execution through call chain.
	 * @param filter        (optional) filter parameters
	 * @param sort          (optional) sort parameters
	 * @return list of filtered items.
	 * @throws ApplicationException when error occured.
	 */
	List<T> getListByFilter(IContext context, FilterParams filter, SortParams sort) throws ApplicationException;
}
