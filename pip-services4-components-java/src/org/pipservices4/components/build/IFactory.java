package org.pipservices4.components.build;

/**
 * Interface for component factories.
 * <p>
 * Factories use locators to identify components to be created.
 * <p>
 * The locators are similar to those used to locate components in references.
 * They can be of any type like strings or integers. However Pip.Services toolkit
 * most often uses {@link org.pipservices4.components.refer.Descriptor} objects as component locators.
 */
public interface IFactory {
	/**
	 * Checks if this factory is able to create component by given locator.
	 * 
	 * This method searches for all registered components and returns
	 * a locator for component it is able to create that matches the given locator.
	 * If the factory is not able to create a requested component is returns null.
	 * 
	 * @param locator 	a locator to identify component to be created.
	 * @return			a locator for a component that the factory is able to create.
	 */
	Object canCreate(Object locator);
	
	/**
	 * Creates a component identified by given locator.
	 * 
	 * @param locator 	a locator to identify component to be created.
	 * @return the created component.
	 * 
	 * @throws CreateException if the factory is not able to create the component.
	 */
	Object create(Object locator) throws CreateException;
}
