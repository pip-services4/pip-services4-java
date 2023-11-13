package org.pipservices4.persistence.write;

import org.pipservices4.components.context.IContext;
import org.pipservices4.data.data.IIdentifiable;
import org.pipservices4.commons.errors.ApplicationException;

/**
 * Interface for data processing components that can create, update and delete data items.
 */
public interface IWriter<T extends IIdentifiable<K>, K> {
	/**
	 * Creates a data item.
	 * 
	 * @param context     (optional) a context to trace execution through call chain.
	 * @param entity        an item to be created.
	 * @return created item.
	 * @throws ApplicationException when error occured.
	 */
	T create(IContext context, T entity) throws ApplicationException;

	/**
	 * Updates a data item.
	 * 
	 * @param context     (optional) a context to trace execution through call chain.
	 * @param entity        an item to be updated.
	 * @return updated item.
	 * @throws ApplicationException when error occured.
	 */
	T update(IContext context, T entity) throws ApplicationException;

	/**
	 * Deleted a data item by it's unique id.
	 * 
	 * @param context     (optional) a context to trace execution through call chain.
	 * @param id            an id of the item to be deleted
	 * @return deleted item by unique id.
	 * @throws ApplicationException when error occured.
	 */
	T deleteById(IContext context, K id) throws ApplicationException;
}
