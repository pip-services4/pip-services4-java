package org.pipservices4.expressions.calculator.variables;

import java.util.List;

/**
 * Defines a variables list.
 */
public interface IVariableCollection {
    /**
     * Adds a new variable to the collection.
     * @param variable a variable to be added.
     */
    void add(IVariable variable);

    /**
     * Gets a number of variables stored in the collection.
     * @return a number of stored variables.
     */
    int length();

    /**
     * Get a variable by its index.
     * @param index a variable index.
     * @return a retrieved variable.
     */
    IVariable get(int index);

    /**
     * Get all variables stores in the collection
     * @return a list with variables.
     */
    List<IVariable> getAll();

    /**
     * Finds variable index in the list by it's name.
     * @param name The variable name to be found.
     * @return Variable index in the list or <code>-1</code> if variable was not found.
     */
    int findIndexByName(String name);

    /**
     * Finds variable in the list by it's name.
     * @param name The variable name to be found.
     * @return A variable or <code>null</code> if function was not found.
     */
    IVariable findByName(String name);

    /**
     * Finds variable in the list or create a new one if variable was not found.
     * @param name The variable name to be found.
     * @return Found or created variable.
     */
    IVariable locate(String name);

    /**
     * Removes a variable by its index.
     * @param index a index of the variable to be removed.
     */
    void remove(int index);

    /**
     * Removes variable by it's name.
     * @param name The variable name to be removed.
     */
    void removeByName(String name);

    /**
     * Clears the collection.
     */
    void clear();

    /**
     * Clears all stored variables (assigns null values).
     */
    void clearValues();
}
