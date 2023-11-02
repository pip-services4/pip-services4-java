package org.pipservices4.expressions.calculator.functions;

import java.util.List;

/**
 * Defines a functions list.
 */
public interface IFunctionCollection {
    /**
     * Adds a new function to the collection.
     *
     * @param func a function to be added.
     */
    void add(IFunction func);

    /**
     * Gets a number of functions stored in the collection.
     *
     * @return a number of stored functions.
     */
    int length();

    /**
     * Get a function by its index.
     *
     * @param index a function index.
     * @return a retrieved function.
     */
    IFunction get(int index);

    /**
     * Get all functions stores in the collection
     *
     * @return a list with functions.
     */
    List<IFunction> getAll();

    /**
     * Finds function index in the list by it's name.
     *
     * @param name The function name to be found.
     * @return Function index in the list or <code>-1</code> if function was not found.
     */
    int findIndexByName(String name);

    /**
     * Finds function in the list by it's name.
     *
     * @param name The function name to be found.
     * @return A function or <code>null</code> if function was not found.
     */
    IFunction findByName(String name);

    /**
     * Removes a function by its index.
     *
     * @param index a index of the function to be removed.
     */
    void remove(int index);

    /**
     * Removes function by it's name.
     *
     * @param name The function name to be removed.
     */
    void removeByName(String name);

    /**
     * Clears the collection.
     */
    void clear();
}
