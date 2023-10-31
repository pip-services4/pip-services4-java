package org.pipservices4.commons.data;

import org.pipservices4.commons.convert.StringConverter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * An object that contains string translations for multiple languages.
 * Language keys use two-letter codes like: 'en', 'sp', 'de', 'ru', 'fr', 'pr'.
 * When translation for specified language does not exists it defaults to English ('en').
 * When English does not exists it falls back to the first defined language.
 *
 * ### Example ###
 * <pre>
 * {@code
 * MultiString values = MultiString.fromTuples(
 *  "en", "Hello World!",
 *  "ru", "Привет мир!"
 * );
 *
 * String value1 = values.get("ru"); // Result: "Привет мир!"
 * String value2 = values.get("pt"); // Result: "Hello World!"
 * }
 * </pre>
 */
public class MultiString extends HashMap<String, String> {
    public MultiString() {
    }

    /**
     * Creates a new MultiString object and initializes it with values.
     *
     * @param map a map with language-text pairs.
     */
    public MultiString(Map<?, ?> map) {
        if (map != null)
            this.append(map);
    }

    /**
     * Gets a string translation by specified language.
     * When language is not found it defaults to English ('en').
     * When English is not found it takes the first value.
     *
     * @param language a language two-symbol code.
     * @return a translation for the specified language or default translation.
     */
    public String get(String language) {
        // Get specified language
        String value = super.get(language);

        // Default to english
        if (value == null)
            value = super.get("en");

        // Default to the first property
        if (value == null) {
            for (Entry<String, String> entry : this.entrySet()) {
                value = this.get(entry.getKey());
                break;
            }
        }

        return value;
    }

    /**
     * Gets all languages stored in this MultiString object,
     *
     * @return a list with language codes.
     */
    public List<String> getLanguages() {
        return new ArrayList<>(this.keySet());
    }

    /**
     * Puts a new translation for the specified language.
     *
     * @param language a language two-symbol code.
     * @param value    a new translation for the specified language.
     */
    public void put(String language, Object value) {
        super.put(language, StringConverter.toNullableString(value));
    }

    /**
     * Appends a map with language-translation pairs.
     *
     * @param map the map with language-translation pairs.
     */
    public void append(Map<?, ?> map) {
        if (map == null)
            return;

        for (Entry<?, ?> entry : map.entrySet()) {
            Object value = entry.getValue();
            if (entry.getKey() != null)
                this.put(StringConverter.toNullableString(entry.getKey()), StringConverter.toNullableString(value));
        }
    }

    /**
     * Returns the number of translations stored in this MultiString object.
     *
     * @return the number of translations.
     */
    public int length() {
        return this.size();
    }

    /**
     * Creates a new MultiString object from a value that contains language-translation pairs.
     *
     * @param value the value to initialize MultiString.
     * @return a MultiString object.
     * @see StringValueMap
     */
    public static MultiString fromValue(Map<?, ?> value) {
        return new MultiString(value);
    }

    /**
     * Creates a new MultiString object from language-translation pairs (tuples).
     *
     * @param tuples an array that contains language-translation tuples.
     * @return a MultiString Object.
     * @see MultiString#fromTuplesArray
     */
    public static MultiString fromTuples(Object... tuples) {
        return MultiString.fromTuplesArray(tuples);
    }

    /**
     * Creates a new MultiString object from language-translation pairs (tuples) specified as array.
     *
     * @param tuples an array that contains language-translation tuples.
     * @return a MultiString Object.
     */
    public static MultiString fromTuplesArray(Object[] tuples) {
        MultiString result = new MultiString();
        if (tuples == null || tuples.length == 0)
            return result;

        for (int index = 0; index < tuples.length; index += 2) {
            if (index + 1 >= tuples.length) break;

            String name = StringConverter.toString(tuples[index]);
            String value = StringConverter.toNullableString(tuples[index + 1]);

            result.put(name, value);
        }

        return result;
    }
}
