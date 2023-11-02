package org.pipservices4.expressions.mustache;

import org.pipservices4.commons.convert.IntegerConverter;
import org.pipservices4.expressions.mustache.parsers.MustacheParser;
import org.pipservices4.expressions.mustache.parsers.MustacheToken;
import org.pipservices4.expressions.tokenizers.Token;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implements an mustache template class.
 */
public class MustacheTemplate {
    private final Map<String, Object> _defaultVariables = new HashMap<>();
    private final MustacheParser _parser = new MustacheParser();
    private boolean _autoVariables = true;

    /**
     * Constructs this class and assigns mustache template.
     *
     * @param template The mustache template.
     */
    public MustacheTemplate(String template) throws Exception {
        if (template != null)
            this.setTemplate(template);

    }

    public MustacheTemplate() throws Exception {
    }

    /**
     * The mustache template.
     */
    public String getTemplate() {
        return this._parser.getTemplate();
    }

    /**
     * The mustache template.
     */
    public void setTemplate(String value) throws Exception {
        this._parser.setTemplate(value);
        if (this._autoVariables)
            this.createVariables(this._defaultVariables);
    }

    public List<Token> getOriginalTokens() {
        return this._parser.getOriginalTokens();
    }

    public void setOriginalTokens(List<Token> value) throws MustacheException {
        this._parser.setOriginalTokens(value);
        if (this._autoVariables)
            this.createVariables(this._defaultVariables);
    }

    /**
     * Gets the flag to turn on auto creation of variables for specified mustache.
     */
    public boolean getAutoVariables() {
        return this._autoVariables;
    }

    /**
     * Sets the flag to turn on auto creation of variables for specified mustache.
     */
    public void setAutoVariables(boolean value) {
        this._autoVariables = value;
    }

    /**
     * The list with default variables.
     */
    public Map<String, Object> getDefaultVariables() {
        return this._defaultVariables;
    }

    /**
     * The list of original mustache tokens.
     */
    public List<MustacheToken> getInitialTokens() {
        return this._parser.getInitialTokens();
    }

    /**
     * The list of processed mustache tokens.
     */
    public List<MustacheToken> getResultTokens() {
        return this._parser.getResultTokens();
    }

    /**
     * Gets a variable value from the collection of variables
     *
     * @param variables a collection of variables.
     * @param name      a variable name to get.
     * @return a variable value or <code>undefined</code>
     */
    public Object getVariable(Map<String, Object> variables, String name) {
        if (variables == null || name == null) return null;

        name = name.toLowerCase();
        Object result = null;

        for (var propName : variables.keySet()) {
            if (propName.toLowerCase().equals(name)) {
                result = result != null ? result : variables.get(propName);
            }
        }

        return result;
    }

    /**
     * Populates the specified variables list with variables from parsed mustache.
     *
     * @param variables The list of variables to be populated.
     */
    public void createVariables(Map<String, Object> variables) {
        if (variables == null) return;

        for (var variableName : this._parser.getVariableNames()) {
            var found = this.getVariable(variables, variableName) != null;
            if (!found) {
                variables.put(variableName, null);
            }
        }
    }

    /**
     * Cleans up this calculator from all data.
     */
    public void clear() {
        this._parser.clear();
        this._defaultVariables.clear();
    }

    /**
     * Evaluates this mustache template using default variables.
     *
     * @return the evaluated template
     */
    public String evaluate() throws MustacheException {
        return this.evaluateWithVariables(null);
    }

    /**
     * Evaluates this mustache using specified variables.
     *
     * @param variables The collection of variables
     * @return the evaluated template
     */
    public String evaluateWithVariables(Map<String, Object> variables) throws MustacheException {
        variables = variables != null ? variables : this._defaultVariables;

        return this.evaluateTokens(this._parser.getResultTokens(), variables);
    }

    private boolean isDefinedVariable(Map<String, Object> variables, String name) {
        var value = this.getVariable(variables, name);

        if (value instanceof Boolean)
            return (boolean) value;
        if (value instanceof String)
            return value != "";
        if (value instanceof Number) {
            var res = IntegerConverter.toNullableInteger(value);
            return res != null && res != 0;
        }

        return value != null;
    }

    private String escapeString(String value) {
        if (value == null) return null;

        return value
                .replace("[\\\\]", "\\\\")
                .replace("[\\\"]", "\\\\\\\"")
                .replace("[\\/]", "\\\\/")
                .replace("[\\b]", "\\\\b")
                .replace("[\\f]", "\\f")
                .replace("[\\n]", "\\n")
                .replace("[\\r]", "\\\\r")
                .replace("[\\t]", "\\t");
    }

    private String evaluateTokens(List<MustacheToken> tokens, Map<String, Object> variables) throws MustacheException {
        if (tokens == null) return null;

        StringBuilder result = new StringBuilder();

        for (var token : tokens) {
            switch (token.getType()) {
                case Comment:
                    // Skip;
                    break;
                case Value:
                    result.append(token.getValue() != null ? token.getValue() : "");
                    break;
                case Variable:
                    var value1 = this.getVariable(variables, token.getValue());
                    result.append(value1 != null ? value1 : "");
                    break;
                case EscapedVariable:
                    var value2 = this.getVariable(variables, token.getValue());
                    value2 = this.escapeString(String.valueOf(value2));
                    result.append(value2 != null ? value2 : "");
                    break;
                case Section:
                    var defined1 = this.isDefinedVariable(variables, token.getValue());
                    if (defined1 && token.getTokens() != null) {
                        result.append(this.evaluateTokens(token.getTokens(), variables));
                    }
                    break;
                case InvertedSection:
                    var defined2 = this.isDefinedVariable(variables, token.getValue());
                    if (!defined2 && token.getTokens() != null) {
                        result.append(this.evaluateTokens(token.getTokens(), variables));
                    }
                    break;
                case Partial:
                    throw new MustacheException(null, "PARTIALS_NOT_SUPPORTED", "Partials are not supported", token.getLine(), token.getColumn());
                default:
                    throw new MustacheException(null, "INTERNAL", "Internal error", token.getLine(), token.getColumn());
            }
        }

        return result.toString();
    }
}
