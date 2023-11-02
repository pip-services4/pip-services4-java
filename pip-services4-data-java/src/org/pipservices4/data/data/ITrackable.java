package org.pipservices4.data.data;

import java.time.*;

/**
 * Interface for data objects that can track their changes, including logical deletion.
 * <p>
 * ### Example ###
 * <pre>
 * {@code
 *  public class MyData implements IStringIdentifiable, ITrackable {
 *    private String id;
 *    public String field1;
 *    public int field2;
 *    ...
 *    public String getId() {...}
 *    public void setId(String newId) {...}
 *
 *    public ZonedDateTime getCreateTime(){...};
 *    public void setCreateTime(){...};
 *    public ZonedDateTime getLastChangeTime(){...};
 *    public void setLastChangeTime(){...};
 *    public boolean isDeleted(){...};
 *    public void setDeleted(){...};
 *  }
 *  }
 *  </pre>
 */
public interface ITrackable {
    /**
     * Gets the time when the object was created
     *
     * @return UTC date and time then object was created
     */
    ZonedDateTime getCreateTime();

    /**
     * Sets a time when the object was created
     *
     * @param value UTC date and time then object was created
     */
    void setCreateTime(ZonedDateTime value);

    /**
     * Gets the last time when the object was changed (created, updated or deleted)
     *
     * @return UTC date and time when the last change occurred
     */
    ZonedDateTime getLastChangeTime();

    /**
     * Sets the last time when the object was changed (created, updated or deleted)
     *
     * @param value UTC date and time when the last change occurred
     */
    void setLastChangeTime(ZonedDateTime value);

    /**
     * Gets the logical deletion flag
     *
     * @return <code>true</code> if the object was deleted and <code>false</code> if the object is still active
     */
    boolean isDeleted();

    /**
     * Sets the logical deletion flag
     *
     * @param value boolean value to set
     */
    void setDeleted(boolean value);
}
