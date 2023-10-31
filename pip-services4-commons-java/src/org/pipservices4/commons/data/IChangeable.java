package org.pipservices4.commons.data;

import java.time.ZonedDateTime;

/**
 * Interface for data objects that contain their latest change time.
 * <p>
 * ### Example ###
 * <pre>
 * {@code
 * import java.time.ZonedDateTime;
 *
 * public class MyData implements IStringIdentifiable, IChangeable {
 *  private String id;
 *  public String field1;
 *  public String field2;
 *  private ZonedDateTime time;
 *
 *  @Override
 *  public ZonedDateTime changeTime() {
 *      time = ZonedDateTime.now();
 *      return time;
 *  }
 *
 *  @Override
 *  public String getId() {
 *      return id;
 *  }
 *
 *  @Override
 *  public void setId(String value) {
 *      id = value;
 *  }
 * }
 * }
 * </pre>
 */
public interface IChangeable {
    /**
     * The UTC time at which the object was last changed (created or updated).
     * @return changed date
     */
    ZonedDateTime changeTime();
}

