package org.pipservices4.persistence.read;

import org.pipservices4.components.context.IContext;
import org.pipservices4.data.query.DataPage;
import org.pipservices4.data.query.PagingParams;
import org.pipservices4.data.query.SortParams;
import org.pipservices4.commons.errors.ApplicationException;

/**
 * Interface for data processing components that can query a page of data items.
 */
public interface IQuarablePageReader<T> {
	/**
	 * Gets a page of data items using a query string.
	 * 
	 * @param context     (optional) a context to trace execution through call chain.
	 * @param query         (optional) a query string
	 * @param paging        (optional) paging parameters
	 * @param sort          (optional) sort parameters
	 * @return a list of items by query.
	 * @throws ApplicationException when error occured.
	 */
	DataPage<T> getPageByQuery(IContext context, String query, PagingParams paging, SortParams sort)
			throws ApplicationException;
}
