package org.pipservices4.data.data;

/**
 * Interface for data objects that have human-readable names.
 * <p>
 * ### Example ###
 * <pre>
 * {@code
 *  public class MyData implements IStringIdentifiable, INamed {
 *    private String id;
 *    private String name;
 *    public String field1;
 *    public int field2;
 *    ...
 *    public String getId() {...}
 *    public void setId(String newId) {...}
 *    public String getName() {...}
 *    public void setName(String newName) {...}
 * }
 * }
 * </pre>
 */
public interface INamed {
    /**
     * Gets the object name
     *
     * @return the object name
     */
    String getName();

    /**
     * Sets the object name
     *
     * @param value a new object name
     */
    void setName(String value);
}
