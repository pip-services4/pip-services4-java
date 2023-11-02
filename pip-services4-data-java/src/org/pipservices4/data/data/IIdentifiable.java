package org.pipservices4.data.data;

/**
 * Generic interface for data objects that can be uniquely identified by an id.
 * <p>
 * The type specified in the interface defines the type of id field.
 * <p>
 * ### Example ###
 * <pre>
 * {@code
 *  public class MyData implements IIdentifiable<String> {
 *    private String id;
 *    public String field1;
 *    public int field2;
 *    ...
 *    public String getId() {...}
 *    public void setId(String newId) {...}
 *  }
 *  }
 *  </pre>
 */
public interface IIdentifiable<K> {
    /**
     * Gets the object id
     *
     * @return object id
     */
    K getId();

    /**
     * Sets the object id
     *
     * @param value a new object id
     */
    void setId(K value);

    /**
     * Generate new object id
     */
    K withGeneratedId();
}
