package org.pipservices4.container.refer;

import org.pipservices4.commons.errors.*;
import org.pipservices4.components.context.IContext;
import org.pipservices4.components.refer.*;
import org.pipservices4.components.run.*;

/**
 * Managed references that in addition to keeping and locating references can also 
 * manage their lifecycle.
 * <ul>
 * <li>Auto-creation of missing component using available factories
 * <li>Auto-linking newly added components
 * <li>Auto-opening newly added components
 * <li>Auto-closing removed components
 * </ul>
 * 
 * @see RunReferencesDecorator
 * @see LinkReferencesDecorator
 * @see BuildReferencesDecorator
 * @see <a href="https://pip-services4-java.github.io/pip-services4-commons-java/org/pipservices4/commons/refer/References.html">References</a>
 */
public class ManagedReferences extends ReferencesDecorator implements IOpenable, IClosable {
	protected References _references;
	protected BuildReferencesDecorator _builder;
	protected LinkReferencesDecorator _linker;
	protected RunReferencesDecorator _runner;

	/**
	 * Creates a new instance of the references
	 */
	public ManagedReferences() {
		this(null);
	}

	/**
	 * Creates a new instance of the references
	 * 
	 * @param tuples tuples where odd values are component locators (descriptors)
	 *               and even values are component references
	 */
	public ManagedReferences(Object[] tuples) {
		_references = new References(tuples);
		_builder = new BuildReferencesDecorator(_references, this);
		_linker = new LinkReferencesDecorator(_builder, this);
		_runner = new RunReferencesDecorator(_linker, this);

		setNextReferences(_runner);
	}

	/**
	 * Checks if the component is opened.
	 * 
	 * @return true if the component has been opened and false otherwise.
	 */
	public boolean isOpen() {
		return _linker.isOpen() && _runner.isOpen();
	}

	/**
	 * Opens the component.
	 * 
	 * @param context     (optional) a context to trace execution through call chain.
	 * @throws ApplicationException when error occured.
	 */
	public void open(IContext context) throws ApplicationException {
		_linker.open(context);
		_runner.open(context);
	}

	/**
	 * Closes component and frees used resources.
	 * 
	 * @param context     (optional) a context to trace execution through call chain.
	 * @throws ApplicationException when error occured.
	 */
	public void close(IContext context) throws ApplicationException {
		_runner.close(context);
		_linker.close(context);
	}

	/**
	 * Creates a new ManagedReferences object filled with provided key-value pairs
	 * called tuples. Tuples parameters contain a sequence of locator1, component1,
	 * locator2, component2, ... pairs.
	 * 
	 * @param tuples the tuples to fill a new ManagedReferences object.
	 * @return a new ManagedReferences object.
	 * @throws ReferenceException when no found references.
	 */
	public static ManagedReferences fromTuples(Object... tuples) throws ReferenceException {
		return new ManagedReferences(tuples);
	}
}
