package org.pipservices4.persistence.read;

import org.pipservices4.commons.errors.ApplicationException;
import org.pipservices4.components.context.IContext;

import java.util.List;

/**
 * Interface for data processing components that load data items.
 */
public interface ILoader<T> {
	/**
	 * Loads data items.
	 * 
	 * @param context     (optional) a context to trace execution through call chain.
	 * @return a list of data items.
	 * @throws ApplicationException when error occured.
	 */
	List<T> load(IContext context) throws ApplicationException;
}
