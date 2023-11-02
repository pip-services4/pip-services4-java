package org.pipservices4.components.config;

/**
 * An interface to set configuration parameters to an object.
 * <p>
 * It is similar to {@link IConfigurable} interface, but emphasizes the fact
 * that <code>configure()</code> method can be called more than once to change object configuration
 * in runtime.
 *
 * @see IConfigurable
 */
public interface IReconfigurable extends IConfigurable {

}
