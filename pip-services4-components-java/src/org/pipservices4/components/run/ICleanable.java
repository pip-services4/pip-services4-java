package org.pipservices4.components.run;

import org.pipservices4.commons.errors.*;
import org.pipservices4.components.context.IContext;

/**
 * Interface for components that should clean their state.
 * <p>
 * Cleaning state most often is used during testing.
 * But there may be situations when it can be done in production.
 * <p>
 * ### Example ###
 * <pre>
 * {@code
 * class MyObjectWithState implements ICleanable {
 *   Object[] _state = new Object[]{};
 *   ...
 *   public void clear(IContext context) {
 *     this._state = new Object[]{};
 *   }
 * }
 * }
 * </pre>
 *
 * @see Cleaner
 */
public interface ICleanable {
    /**
     * Clears component state.
     *
     * @param context     (optional) a context to trace execution through call chain.
     * @throws ApplicationException when error or null no errors occurred.
     */
    void clear(IContext context) throws ApplicationException;
}
