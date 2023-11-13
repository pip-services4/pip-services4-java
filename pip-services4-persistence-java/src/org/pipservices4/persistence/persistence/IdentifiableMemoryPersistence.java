package org.pipservices4.persistence.persistence;

import org.pipservices4.components.config.IConfigurable;
import org.pipservices4.commons.data.AnyValueMap;
import org.pipservices4.components.context.IContext;
import org.pipservices4.data.data.IIdentifiable;
import org.pipservices4.data.data.IStringIdentifiable;
import org.pipservices4.data.keys.IdGenerator;
import org.pipservices4.commons.errors.ApplicationException;
import org.pipservices4.commons.reflect.ObjectReader;
import org.pipservices4.commons.reflect.ObjectWriter;
import org.pipservices4.persistence.read.IGetter;
import org.pipservices4.persistence.read.ILoader;
import org.pipservices4.persistence.write.IPartialUpdater;
import org.pipservices4.persistence.write.ISaver;
import org.pipservices4.persistence.write.ISetter;
import org.pipservices4.persistence.write.IWriter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Abstract persistence component that stores data in memory
 * and implements a number of CRUD operations over data items with unique ids.
 * The data items must implement <a href="https://pip-services4-java.github.io/pip-services4-data-java/org/pipservices4/data/data/IIdentifiable.html">IIdentifiable</a> interface.
 * <p>
 * In basic scenarios child classes shall only override <code>getPageByFilter()</code>,
 * <code>getListByFilter()</code> or <code>deleteByFilter()</code> operations with specific filter function.
 * All other operations can be used out of the box.
 * <p>
 * In complex scenarios child classes can implement additional operations by
 * accessing cached items via <code>this._items</code> property and calling <code>save()</code> method
 * on updates.
 * <p>
 * ### Configuration parameters ###
 * <ul>
 * <li>options:
 *     <ul>
 *     <li>max_page_size:       Maximum number of items returned in a single page (default: 100)
 * 	   </ul>
 * </ul>
 * <p>
 * ### References ###
 * <ul>
 * <li>*:logger:*:*:1.0         (optional) <a href="https://pip-services4-java.github.io/pip-services4-observability-java/org/pipservices4/observability/log/ILogger.html">ILogger</a> components to pass log messages
 * </ul>
 * <p>
 * ### Examples ###
 * <pre>
 * {@code
 * class MyMemoryPersistence extends IdentifiableMemoryPersistence<MyData, String> {
 *
 *   private Predicate<MyData> composeFilter(FilterParams filter) {
 *       filter = filter != null ? filter : new FilterParams();
 *       String name = filter.getAsNullableString("name");
 *       return (item) -> {
 *           if (name != null && item.name != name)
 *               return false;
 *           return true;
 *       };
 *   }
 *
 *   public DataPage<MyData> getPageByFilter(IContext context, FilterParams filter, PagingParams paging) {
 *       super.getPageByFilter(context, this.composeFilter(filter), paging, null, null);
 *   }
 *
 * }
 *
 * MyMemoryPersistence persistence = new MyMemoryPersistence(MyData.class);
 *
 * MyData item = persistence.create("123", new MyData("1", "ABC"));
 * DataPage<MyData> mydata = persistence.getPageByFilter(
 *         "123",
 *         FilterParams.fromTuples("name", "ABC"),
 *         null, null, null);
 * System.out.println(mydata.getData().toString());          // Result: { id: "1", name: "ABC" }
 * persistence.deleteById("123", "1");
 * ...
 * }
 * </pre>
 *
 * @see MemoryPersistence
 */
public class IdentifiableMemoryPersistence<T extends IIdentifiable<K>, K> extends MemoryPersistence<T>
        implements IConfigurable, IWriter<T, K>, IGetter<T, K>, ISetter<T>, IPartialUpdater<T, K> {

    /**
     * Creates a new instance of the persistence.
     *
     * @param type the class type
     */
    protected IdentifiableMemoryPersistence(Class<T> type) {
        this(type, null, null);
    }

    // Pass the item type since Jackson cannot recognize type from generics
    // This is related to Java type erasure issue

    /**
     * Creates a new instance of the persistence.
     *
     * @param type   the class type
     * @param loader (optional) a loader to load items from external datasource.
     * @param saver  (optional) a saver to save items to external datasource.
     */
    protected IdentifiableMemoryPersistence(Class<T> type, ILoader<T> loader, ISaver<T> saver) {
        super(type, loader, saver);
    }

    /**
     * Finds one element by id.
     *
     * @param id an id of data item.
     * @return data item.
     */
    protected T findOne(K id) {
        Optional<T> item = _items.stream().filter((v) -> v.getId().equals(id)).findFirst();
        return item.orElse(null);
    }

    /**
     * Finds all elements by ids.
     *
     * @param ids ids of data items.
     * @return data list of items.
     */
    protected List<T> findAll(K[] ids) {
        List<T> result = new ArrayList<>();
        for (K id : ids) {
            Optional<T> item = _items.stream().filter((v) -> v.getId().equals(id)).findAny();
            result.add(item.orElse(null));
        }
        return result;
    }

    /**
     * Gets a data item by its unique id.
     *
     * @param context     (optional) a context to trace execution through call chain.
     * @param id            an id of data item to be retrieved.
     * @return data item.
     */
    public T getOneById(IContext context, K id) {
        synchronized (_lock) {
            T item = findOne(id);
            if (item != null)
                _logger.trace(context, "Retrieved %s by %s", item, id);
            else
                _logger.trace(context, "Cannot find %s by %s", _typeName, id);
            return item;
        }
    }

    /**
     * Gets a list of data items retrieved by given unique ids.
     *
     * @param context     (optional) a context to trace execution through call chain.
     * @param ids           ids of data items to be retrieved
     * @return a data list.
     */
    public List<T> getListByIds(IContext context, K[] ids)  {
        List<T> result = new ArrayList<>();
        for (K oneId : ids) {
            T item = getOneById(context, oneId);
            if (item != null)
                result.add(item);
        }
        return result;
    }

    /**
     * Gets a list of data items retrieved by given unique ids.
     *
     * @param context     (optional) a context to trace execution through call chain.
     * @param ids           ids of data items to be retrieved
     * @return a data list.
     */
    public List<T> getListByIds(IContext context, List<K> ids) {
        List<T> result = new ArrayList<>();
        for (K oneId : ids) {
            T item = getOneById(context, oneId);
            if (item != null)
                result.add(item);
        }
        return result;
    }

    /**
     * Creates a data item.
     *
     * @param context     (optional) a context to trace execution through call chain.
     * @param item          an item to be created.
     * @return created item.
     * @throws ApplicationException when error occured.
     */
    @Override
    public T create(IContext context, T item) throws ApplicationException {
        // Assign unique string key
        if (item instanceof IStringIdentifiable && item.getId() == null)
            ((IStringIdentifiable) item).setId(IdGenerator.nextLong());

        synchronized (_lock) {
            _items.add(item);

            _logger.trace(context, "Created %s", item);

            save(context);
        }

        return item;
    }

    /**
     * Updates a data item.
     *
     * @param context     (optional) a context to trace execution through call chain.
     * @param newItem       an item to be updated.
     * @return updated item.
     * @throws ApplicationException when error occured.
     */
    public T update(IContext context, T newItem) throws ApplicationException {
        synchronized (_lock) {
            T oldItem = findOne(newItem.getId());
            if (oldItem == null)
                return null;

            int index = _items.indexOf(oldItem);
            if (index < 0)
                return null;

            _items.set(index, newItem);

            _logger.trace(context, "Updated %s", newItem);

            save(context);

            return newItem;
        }
    }

    /**
     * Sets a data item. If the data item exists it updates it, otherwise it create
     * a new data item.
     *
     * @param context     (optional) a context to trace execution through call chain.
     * @param newItem       a item to be set.
     * @return updated item.
     * @throws ApplicationException when error occured.
     */
    public T set(IContext context, T newItem) throws ApplicationException {
        // Assign unique string key
        if (newItem instanceof IStringIdentifiable && newItem.getId() == null)
            ((IStringIdentifiable) newItem).setId(IdGenerator.nextLong());

        synchronized (_lock) {
            T oldItem = findOne(newItem.getId());

            if (oldItem == null)
                _items.add(newItem);
            else {
                int index = _items.indexOf(oldItem);
                if (index < 0)
                    _items.add(newItem);
                else
                    _items.set(index, newItem);
            }

            _logger.trace(context, "Set %s", newItem);

            save(context);

            return newItem;
        }
    }

    /**
     * Deleted a data item by it's unique id.
     *
     * @param context     (optional) a context to trace execution through call chain.
     * @param id            an id of the item to be deleted
     * @return deleted item.
     * @throws ApplicationException when error occured.
     */
    @Override
    public T deleteById(IContext context, K id) throws ApplicationException {
        synchronized (_lock) {
            T item = findOne(id);
            if (item == null)
                return null;

            int index = _items.indexOf(item);
            if (index < 0)
                return null;

            _items.remove(index);

            _logger.trace(context, "Deleted %s", item);

            save(context);

            return item;
        }
    }

    /**
     * Deletes multiple data items by their unique ids.
     *
     * @param context     (optional) a context to trace execution through call chain.
     * @param ids           ids of data items to be deleted.
     * @throws ApplicationException when error occured.
     */
    public void deleteByIds(IContext context, K[] ids) throws ApplicationException {
        List<K> idsList = Arrays.asList(ids);

        int deleted = 0;
        synchronized (_lock) {
            Stream<T> items = _items.stream();

            items = items.filter(x -> idsList.contains(x.getId()));
            List<T> data = items.toList();
            for (T item : data) {
                _items.remove(item);
                deleted++;
            }
            _logger.trace(context, "Deleted %d items", deleted);

            if (deleted > 0) {
                save(context);
            }
        }
    }

    /**
     * Updates only few selected fields in a data item.
     *
     * @param context     (optional) a context to trace execution through call chain.
     * @param id            an id of data item to be updated.
     * @param data          a map with fields to be updated.
     * @return the updated data item.
     */
    @Override
    public T updatePartially(IContext context, K id, AnyValueMap data) throws ApplicationException {
        synchronized (_lock) {
            var index = this._items.stream().map(IIdentifiable::getId).collect(Collectors.toList()).indexOf(id);

            if (index < 0) {
                this._logger.trace(context, "Item %s was not found", id);
                return null;
            }

            var item = this._items.get(index);
            var properties = ObjectReader.getProperties(data.getAsObject());
            ObjectWriter.setProperties(item, properties);

            this._items.set(index, item);
            this._logger.trace(context, "Partially updated item %s", id);

            this.save(context);

            return item;
        }
    }
}