package org.pipservices4.persistence.read;

import org.pipservices4.components.context.IContext;
import org.pipservices4.data.query.SortParams;
import org.pipservices4.commons.errors.ApplicationException;

import java.util.List;

/**
 * Interface for data processing components that can query a list of data items.
 */
public interface IQuerableReader<T> {
	/**
	 * Gets a list of data items using a query string.
	 * 
	 * @param context     (optional) a context to trace execution through call chain.
	 * @param query         (optional) a query string
	 * @param sort          (optional) sort parameters
	 * @return a list of items by query.
	 * @throws ApplicationException when error occured.
	 */
	List<T> getListByQuery(IContext context, String query, SortParams sort) throws ApplicationException;
}
