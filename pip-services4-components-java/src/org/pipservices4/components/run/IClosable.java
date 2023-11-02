package org.pipservices4.components.run;

import org.pipservices4.commons.errors.*;
import org.pipservices4.components.context.IContext;

/**
 * Interface for components that require explicit closure.
 * <p>
 * For components that require opening as well as closing
 * use {@link IOpenable} interface instead.
 * <p>
 * ### Example ###
 * <pre>
 * {@code
 * class MyConnector implements ICloseable {
 *   private Object _client = null;
 *
 *   ... // The _client can be lazy created
 *
 *   public void close(IContext context) {
 *     if (this._client != null) {
 *       this._client.close();
 *       this._client = null;
 *     }
 *   }
 * }
 * }
 * </pre>
 *
 * @see IOpenable
 * @see Closer
 */
public interface IClosable {
    /**
     * Closes component and frees used resources.
     *
     * @param context     (optional) a context to trace execution through call chain.
     * @throws ApplicationException when error or null no errors occurred.
     */
    void close(IContext context) throws ApplicationException;
}
