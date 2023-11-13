package org.pipservices4.persistence.write;

import org.pipservices4.commons.errors.ApplicationException;
import org.pipservices4.components.context.IContext;

/**
 * Interface for data processing components that can set (create or update) data items.
 */
public interface ISetter<T> {
	/**
	 * Sets a data item. If the data item exists it updates it, otherwise it create
	 * a new data item.
	 * 
	 * @param context     (optional) a context to trace execution through call chain.
	 * @param entity        a item to be set.
	 * @return updated item.
	 * @throws ApplicationException when error occured.
	 */
	T set(IContext context, T entity) throws ApplicationException;
}
