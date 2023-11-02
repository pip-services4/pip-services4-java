package org.pipservices4.components.refer;

/**
 * Interface for components that require explicit clearing of references to dependent components.
 * <p>
 * ### Example ###
 * <pre>
 * {@code
 *  public class MyController implements IReferenceable, IUnreferenceable {
 *     public IMyPersistence _persistence;
 *     ...
 *     public void setReferences(IReferences references) {
 *       this._persistence = (IMyPersistence)references.getOneRequired(
 *         new Descriptor("mygroup", "persistence", "*", "*", "1.0")
 *       );
 *     }
 *
 *     public void unsetReferences() {
 *       this._persistence = null;
 *     }
 *     ...
 *  }
 *  }
 *  </pre>
 *
 * @see IReferences
 * @see IReferenceable
 */
public interface IUnreferenceable {
    /**
     * Unsets (clears) previously set references to dependent components.
     */
    void unsetReferences();
}
