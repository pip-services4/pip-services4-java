package org.pipservices4.rpc.commands;

/**
 * An interface for commandable objects, which are part of the command design pattern.
 * The commandable object exposes its functonality as commands and events grouped
 * into a {@link CommandSet}.
 * <p>
 * This interface is typically implemented by services and is used to auto generate
 * external interfaces.
 * <p>
 * ### Example ###
 * <pre>
 * {@code
 * public class MyDataService implements ICommandable, IMyDataService {
 *   private MyDataCommandSet _commandSet;
 *
 *   public CommandSet getCommandSet() {
 *     if (this._commandSet == null)
 *       this._commandSet = new MyDataCommandSet(this);
 *     return this._commandSet;
 *   }
 *
 *   ...
 * }
 * }
 * </pre>
 *
 * @see CommandSet
 * @see CommandSet examples
 */

public interface ICommandable {

    /**
     * Gets a command set with all supported commands and events.
     *
     * @return a command set with commands and events.
     * @see CommandSet
     */
    CommandSet getCommandSet();
}
