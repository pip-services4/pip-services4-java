package org.pipservices4.expressions.calculator.variables;

import org.pipservices4.expressions.variants.Variant;

import java.util.ArrayList;
import java.util.List;

/**
 * Implements a variables list.
 */
public class VariableCollection implements IVariableCollection {
    private List<IVariable> _variables = new ArrayList<>();

    /**
     * Adds a new variable to the collection.
     *
     * @param variable a variable to be added.
     */
    @Override
    public void add(IVariable variable) {
        if (variable == null)
            throw new NullPointerException("Variable cannot be null");

        this._variables.add(variable);
    }

    /**
     * Gets a number of variables stored in the collection.
     *
     * @return a number of stored variables.
     */
    @Override
    public int length() {
        return _variables.size();
    }

    /**
     * Get a variable by its index.
     *
     * @param index a variable index.
     * @return a retrieved variable.
     */
    @Override
    public IVariable get(int index) {
        return this._variables.get(index);
    }

    /**
     * Get all variables stores in the collection
     *
     * @return a list with variables.
     */
    @Override
    public List<IVariable> getAll() {
        List<IVariable> result = new ArrayList<>();
        for (var variable : _variables)
            result.add(new Variable(variable.getName(), variable.getValue()));
        return result;
    }

    /**
     * Finds variable index in the list by it's name.
     *
     * @param name The variable name to be found.
     * @return Variable index in the list or <code>-1</code> if variable was not found.
     */
    @Override
    public int findIndexByName(String name) {
        name = name.toUpperCase();
        for (var i = 0; i < this._variables.size(); i++) {
            var varName = this._variables.get(i).getName().toUpperCase();
            if (varName.equals(name))
                return i;
        }
        return -1;
    }

    /**
     * Finds variable in the list by it's name.
     *
     * @param name The variable name to be found.
     * @return A variable or <code>null</code> if function was not found.
     */
    @Override
    public IVariable findByName(String name) {
        var index = this.findIndexByName(name);
        return index >= 0 ? this._variables.get(index) : null;
    }

    /**
     * Finds variable in the list or create a new one if variable was not found.
     *
     * @param name The variable name to be found.
     * @return Found or created variable.
     */
    @Override
    public IVariable locate(String name) {
        var v = this.findByName(name);
        if (v == null) {
            v = new Variable(name);
            this.add(v);
        }
        return v;
    }

    /**
     * Removes a variable by its index.
     *
     * @param index a index of the variable to be removed.
     */
    @Override
    public void remove(int index) {
        this._variables.remove(index);
    }

    /**
     * Removes variable by it's name.
     *
     * @param name The variable name to be removed.
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
        _variables = new ArrayList<>();
    }

    /**
     * Clears all stored variables (assigns null values).
     */
    @Override
    public void clearValues() {
        for (var v : this._variables)
            v.setValue(new Variant());
    }
}
