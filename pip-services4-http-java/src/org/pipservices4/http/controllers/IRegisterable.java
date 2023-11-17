package org.pipservices4.http.controllers;

import org.pipservices4.components.refer.ReferenceException;

/**
 * Interface to perform on-demand registrations. 
 */
public interface IRegisterable {
	/**
	 * Perform required registration steps.
	 */
	void register() throws ReferenceException;
}
