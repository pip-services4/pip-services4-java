package org.pipservices4.commons.data;

/**
 * Interface for data objects that can be versioned.
 * <p>
 * Versioning is often used as optimistic concurrency mechanism.
 * <p>
 * The version doesn't have to be a number, but it is recommended to use sequential
 * values to determine if one object has newer or older version than another one.
 * <p>
 * It is a common pattern to use the time of change as the object version.
 * <p>
 * ### Example ###
 * <pre>
 * {@code
 *  public class MyData implements IStringIdentifiable, IVersioned {
 *    private String id;
 *    public String field1;
 *    public int field2;
 *    private String version;
 *    ...
 *  }
 *
 * public void updateData(String correlationId, MyData item) {
 *  ...
 *  if (item.getVersion() < oldItem.getVersion()) {
 *    throw new ConcurrencyException(null, "VERSION_CONFLICT", "The change has older version stored value");
 *  }
 *  ...
 * }
 * }
 * </pre>
 */
public interface IVersioned {
    /**
     * Gets the object version
     *
     * @return the object version
     */
    String getVersion();

    /**
     * Sets the object version
     *
     * @param value a new object version
     */
    void setVersion(String value);
}
