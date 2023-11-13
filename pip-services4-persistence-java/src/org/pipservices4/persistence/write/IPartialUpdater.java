package org.pipservices4.persistence.write;

import org.pipservices4.commons.data.AnyValueMap;
import org.pipservices4.commons.errors.ApplicationException;
import org.pipservices4.components.context.IContext;

/**
 * Interface for data processing components to update data items partially.
 */
public interface IPartialUpdater<T, K> {
	/**
	 * Updates only few selected fields in a data item.
	 * 
	 * @param context     (optional) a context to trace execution through call chain.
	 * @param id            an id of data item to be updated.
	 * @param data          a map with fields to be updated.
	 * @return updated item.
	 */
	T updatePartially(IContext context, K id, AnyValueMap data) throws ApplicationException;
}
