package org.pipservices4.persistence.read;

import org.pipservices4.components.context.IContext;
import org.pipservices4.data.query.DataPage;
import org.pipservices4.data.query.FilterParams;
import org.pipservices4.data.query.PagingParams;
import org.pipservices4.data.query.SortParams;
import org.pipservices4.commons.errors.ApplicationException;

/**
 * Interface for data processing components that can retrieve a page of data items by a filter.
 */
public interface IFilteredPageReader<T> {
	/**
	 * Gets a page of data items using filter parameters.
	 * 
	 * @param context     (optional) a context to trace execution through call chain.
	 * @param filter        (optional) filter parameters
	 * @param paging        (optional) paging parameters
	 * @param sort          (optional) sort parameters
	 * @return list of filtered items.
	 * @throws ApplicationException when error occured.
	 */
	DataPage<T> getPageByFilter(IContext context, FilterParams filter, PagingParams paging, SortParams sort)
			throws ApplicationException;
}
