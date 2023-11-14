package org.pipservices4.rpc.commands;

import java.util.*;

import org.pipservices4.components.exec.IExecutable;
import org.pipservices4.components.exec.Parameters;
import org.pipservices4.data.validate.ValidationResult;

/**
 * An interface for Commands, which are part of the Command design pattern.
 * Each command wraps a method or function and allows to call them in uniform and safe manner.
 *
 * @see Command
 * @see IExecutable
 * @see ICommandInterceptor
 * @see InterceptedCommand
 */
public interface ICommand extends IExecutable {
    /**
     * Gets the command name.
     *
     * @return the command name.
     */
    String getName();

    /**
     * Validates command arguments before execution using defined schema.
     *
     * @param args the parameters (arguments) to validate.
     * @return an array of ValidationResults.
     * @see Parameters
     * @see ValidationResult
     */
    List<ValidationResult> validate(Parameters args);
}
