package org.pipservices4.commons.data;

/**
 * Interface for data objects that are able to create their full binary copy,.
 * <p>
 * ### Example ###
 * <pre>
 * {@code
 *  public class MyClass implements IMyClass, ICloneable {
 *    MyClass() { };
 *
 *    public Object clone() {
 *      Object cloneObj = new Object (this);
 *
 *      // Copy every attribute from this to cloneObj here.
 *      ...
 *
 *      return cloneObj;
 *    }
 *  }
 *  }
 *  </pre>
 */
public interface ICloneable {
    /**
     * Creates a binary clone of this object.
     *
     * @return a clone of this object.
     */
    Object clone();

}
