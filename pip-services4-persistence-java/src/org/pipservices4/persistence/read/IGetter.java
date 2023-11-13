package org.pipservices4.persistence.read;

import org.pipservices4.components.context.IContext;
import org.pipservices4.data.data.IIdentifiable;
import org.pipservices4.commons.errors.ApplicationException;

/**
 * Interface for data processing components that can get data items.
 */
public interface IGetter<T extends IIdentifiable<K>, K> {
	/**
	 * Gets a data items by its unique id.
	 * 
	 * @param context     (optional) a context to trace execution through call chain.
	 * @param id            an id of item to be retrieved.
	 * @return an item by its id.
	 * @throws ApplicationException when error occured.
	 */
	T getOneById(IContext context, K id) throws ApplicationException;
}
