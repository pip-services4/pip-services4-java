package org.pipservices4.components.exec;

import org.pipservices4.commons.errors.*;

/**
 * Interface for components that require execution parameters.
 */
public interface IParameterized {
    /**
     * Sets execution parameters.
     *
     * @param parameters execution parameters.
     * @throws ConfigException when configuration is wrong
     */
    void setParameters(Parameters parameters) throws ConfigException;
}
