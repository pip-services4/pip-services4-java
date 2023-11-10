package org.pipservices4.config.connect;

import org.pipservices4.commons.errors.ApplicationException;
import org.pipservices4.components.context.IContext;
import org.pipservices4.config.auth.CredentialParams;

import java.util.List;

/**
 * Interface for discovery services which are used to store and resolve connection parameters
 * to connect to external services.
 * 
 * @see ConnectionParams
 * @see CredentialParams
 */
public interface IDiscovery {
	/**
	 * Registers connection parameters into the discovery service.
	 *
	 * @param context (optional) transaction id to trace execution through
	 *                      call chain.
	 * @param key           a key to uniquely identify the connection parameters.
	 * @param connection    a connection to be registered.
	 * @throws ApplicationException when registration fails for whatever reasons
	 */
	void register(IContext context, String key, ConnectionParams connection) throws ApplicationException;

	/**
	 * Resolves a single connection parameters by its key.
	 * 
	 * @param context (optional) transaction id to trace execution through
	 *                      call chain.
	 * @param key           a key to uniquely identify the connection.
	 * @return a resolved connection.
	 * @throws ApplicationException when resolution failed for whatever reasons.
	 */
	ConnectionParams resolveOne(IContext context, String key) throws ApplicationException;

	/**
	 * Resolves all connection parameters by their key.
	 * 
	 * @param context (optional) transaction id to trace execution through
	 *                      call chain.
	 * @param key           a key to uniquely identify the connections.
	 * @return a list with resolved connections.
	 * @throws ApplicationException when resolution failed for whatever reasons.
	 */
	List<ConnectionParams> resolveAll(IContext context, String key) throws ApplicationException;
}
