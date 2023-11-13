package org.pipservices4.persistence.write;

import org.pipservices4.commons.errors.ApplicationException;
import org.pipservices4.components.context.IContext;

import java.util.List;

/**
 * Interface for data processing components that save data items.
 */
public interface ISaver<T> {
	/**
	 * Saves given data items.
	 * 
	 * @param context     (optional) a context to trace execution through call chain.
	 * @param entities      a list of items to save.
	 * @throws ApplicationException when error occured.
	 */
	void save(IContext context, List<T> entities) throws ApplicationException;
}
