package org.pipservices4.expressions.calculator.functions;

import java.util.ArrayList;
import java.util.List;

/**
 * Implements a functions list.
 */
public class FunctionCollection implements IFunctionCollection {
    private List<IFunction> _functions = new ArrayList<>();

    /**
     * Adds a new function to the collection.
     *
     * @param func a function to be added.
     */
    @Override
    public void add(IFunction func) {
        if (func == null)
            throw new NullPointerException("Func cannot be null");

        this._functions.add(func);
    }

    /**
     * Gets a number of functions stored in the collection.
     *
     * @return a number of stored functions.
     */
    @Override
    public int length() {
        return this._functions.size();
    }

    /**
     * Get a function by its index.
     *
     * @param index a function index.
     * @return a retrieved function.
     */
    @Override
    public IFunction get(int index) {
        return this._functions.get(index);
    }

    /**
     * Get all functions stores in the collection
     *
     * @return a list with functions.
     */
    @Override
    public List<IFunction> getAll() {
        return new ArrayList<>(_functions);
    }

    /**
     * Finds function index in the list by it's name.
     *
     * @param name The function name to be found.
     * @return Function index in the list or <code>-1</code> if function was not found.
     */
    @Override
    public int findIndexByName(String name) {
        name = name.toUpperCase();
        for (var i = 0; i < this._functions.size(); i++) {
            var varName = this._functions.get(i).getName().toUpperCase();
            if (varName.equals(name)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Finds function in the list by it's name.
     *
     * @param name The function name to be found.
     * @return A function or <code>null</code> if function was not found.
     */
    @Override
    public IFunction findByName(String name) {
        var index = this.findIndexByName(name);
        return index >= 0 ? this._functions.get(index) : null;
    }

    /**
     * Removes a function by its index.
     *
     * @param index a index of the function to be removed.
     */
    @Override
    public void remove(int index) {
        this._functions.remove(index);
    }

    /**
     * Removes function by it's name.
     *
     * @param name The function name to be removed.
     */
    @Override
    public void removeByName(String name) {
        var index = this.findIndexByName(name);
        if (index >= 0)
            this.remove(index);
    }

    /**
     * Clears the collection.
     */
    @Override
    public void clear() {
        this._functions = new ArrayList<>();
    }
}
