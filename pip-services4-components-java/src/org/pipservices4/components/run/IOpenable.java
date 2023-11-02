package org.pipservices4.components.run;

import org.pipservices4.commons.errors.*;
import org.pipservices4.components.context.IContext;

/**
 * Interface for components that require explicit opening and closing.
 * <p>
 * For components that perform opening on demand consider using
 * {@link IClosable} interface instead.
 * <p>
 * ### Example ###
 * <pre>
 * {@code
 * class MyPersistence implements IOpenable {
 *   private Object _client;
 *   ...
 *   public boolean isOpen() {
 *     return this._client != null;
 *   }
 *
 *   public void open(IContext context) {
 *     if (this.isOpen()) {
 *       return;
 *     }
 *     ...
 *   }
 *
 *   public void close(IContext context) {
 *     if (this._client != null) {
 *       this._client.close();
 *       this._client = null;
 *     }
 *   }
 *
 *   ...
 * }
 * }
 * </pre>
 *
 * @see IOpenable
 * @see Opener
 */
public interface IOpenable extends IClosable {
    /**
     * Checks if the component is opened.
     *
     * @return true if the component has been opened and false otherwise.
     */
    boolean isOpen();

    /**
     * Opens the component.
     *
     * @param context     (optional) a context to trace execution through call chain.
     * @throws ApplicationException when error or null no errors occurred.
     */
    void open(IContext context) throws ApplicationException;
}
