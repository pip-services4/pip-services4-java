package org.pipservices4.persistence.persistence;

import org.pipservices4.components.config.ConfigParams;
import org.pipservices4.components.config.IConfigurable;
import org.pipservices4.commons.convert.JsonConverter;
import org.pipservices4.components.context.IContext;
import org.pipservices4.data.query.DataPage;
import org.pipservices4.data.query.PagingParams;
import org.pipservices4.commons.errors.ApplicationException;
import org.pipservices4.commons.errors.ConfigException;
import org.pipservices4.data.random.RandomInteger;
import org.pipservices4.components.refer.IReferenceable;
import org.pipservices4.components.refer.IReferences;
import org.pipservices4.components.run.ICleanable;
import org.pipservices4.components.run.IOpenable;
import org.pipservices4.observability.log.CompositeLogger;
import org.pipservices4.persistence.read.ILoader;
import org.pipservices4.persistence.write.ISaver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Abstract persistence component that stores data in memory.
 * <p>
 * This is the most basic persistence component that is only
 * able to store data items of any type. Specific CRUD operations
 * over the data items must be implemented in child classes by
 * accessing <code>this._items</code> property and calling <code>save()</code> method.
 * <p>
 * The component supports loading and saving items from another data source.
 * That allows to use it as a base class for file and other types
 * of persistence components that cache all data in memory.
 * <p>
 * ### References ###
 * <ul>
 * <li>*:logger:*:*:1.0         (optional) <a href="https://pip-services4-java.github.io/pip-services4-observability-java/org/pipservices4/observability/log/ILogger.html">ILogger</a> components to pass log messages
 * </ul>
 * <p>
 * <pre>
 * {@code
 * ### Example ###
 *
 * class MyMemoryPersistence extends MemoryPersistence {
 *   public MyData getByName(IContext context, String name) {
 *     MyData item = find(name); // search method
 *     ...
 *     return item;
 *   });
 *
 *   public MyData set(IContext context, MyData item) {
 *     this._items = filter(); // filter method
 *     ...
 *     this._items.add(item);
 *     this.save(context);
 *   }
 *
 * }
 *
 * MyMemoryPersistence persistence = new MyMemoryPersistence();
 *
 * persistence.set("123", new MyData("ABC"));
 * System.out.println(persistence.getByName("123", "ABC")).toString(); // Result: { name: "ABC" }
 * }
 * </pre>
 */
public abstract class MemoryPersistence<T> implements IConfigurable, IReferenceable, IOpenable, ICleanable {
    protected Class<?> _type;
    protected String _typeName;

    protected CompositeLogger _logger = new CompositeLogger();

    protected List<T> _items = new ArrayList<>();
    protected ILoader<T> _loader;
    protected ISaver<T> _saver;
    protected boolean _opened = false;
    protected int _maxPageSize = 100;
    protected final Object _lock = new Object();

    /**
     * Creates a new instance of the persistence.
     *
     * @param type the class type
     */
    protected MemoryPersistence(Class<T> type) {
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
    protected MemoryPersistence(Class<T> type, ILoader<T> loader, ISaver<T> saver) {
        _type = type;
        _typeName = type.getName();
        _loader = loader;
        _saver = saver;
    }

    /**
     * Configures component by passing configuration parameters.
     *
     * @param config configuration parameters to be set.
     */
    @Override
    public void configure(ConfigParams config) throws ConfigException {
        this._maxPageSize = config.getAsIntegerWithDefault("options.max_page_size", this._maxPageSize);
    }

    /**
     * Sets references to dependent components.
     *
     * @param references references to locate the component dependencies.
     */
    @Override
    public void setReferences(IReferences references) {
        _logger.setReferences(references);
    }

    /**
     * Checks if the component is opened.
     *
     * @return true if the component has been opened and false otherwise.
     */
    public boolean isOpen() {
        return _opened;
    }

    /**
     * Opens the component.
     *
     * @param context     (optional) a context to trace execution through call chain.
     * @throws ApplicationException when error occured.
     */
    public void open(IContext context) throws ApplicationException {
        load(context);
        _opened = true;
    }

    /**
     * Closes component and frees used resources.
     *
     * @param context     (optional) a context to trace execution through call chain.
     * @throws ApplicationException when error occured.
     */
    public void close(IContext context) throws ApplicationException {
        save(context);
        _opened = false;
    }

    private void load(IContext context) throws ApplicationException {
        if (_loader != null) {
            synchronized (_lock) {
                _items = _loader.load(context);
                _logger.trace(context, "Loaded %d of %s", _items.size(), _typeName);
            }
        }
    }

    /**
     * Saves items to external data source using configured saver component.
     *
     * @param context     (optional) a context to trace execution through call chain.
     * @throws ApplicationException when error occured.
     */
    public void save(IContext context) throws ApplicationException {
        if (_saver != null) {
            synchronized (_lock) {
                _saver.save(context, _items);
                _logger.trace(context, "Saved %d of %s", _items.size(), _typeName);
            }
        }
    }

    /**
     * Clears component state.
     *
     * @param context     (optional) a context to trace execution through call chain.
     * @throws ApplicationException when error occured.
     */
    public void clear(IContext context) throws ApplicationException {
        synchronized (_lock) {
            _items = new ArrayList<>();
            _logger.trace(context, "Cleared %s", _typeName);
            save(context);
        }
    }

    /**
     * Gets a page of data items retrieved by a given filter and sorted according to sort parameters.
     * <p>
     * This method shall be called by a public getPageByFilter method from child class that
     * receives FilterParams and converts them into a filter function.
     *
     * @param context     (optional) a context to trace execution through call chain.
     * @param filter        (optional) a filter function to filter items
     * @param paging        (optional) paging parameters
     * @param sort          (optional) sorting parameters
     * @return a requested page with data items.
     */
    protected DataPage<T> getPageByFilter(IContext context, Predicate<T> filter,
                                          PagingParams paging, Comparator<T> sort) {
        synchronized (_lock) {
            Stream<T> items = this._items.stream();

            // Apply filter
            if (filter != null)
                items = items.filter(filter);

            // Extract a page
            paging = paging != null ? paging : new PagingParams();
            long skip = paging.getSkip(-1);
            long take = paging.getTake(_maxPageSize);

            Long total = null;
            if (paging.hasTotal()) {
                List<T> selectedItems = items.collect(Collectors.toList());
                total = (long) selectedItems.size();
                items = selectedItems.stream();
            }

            if (skip > 0)
                items = items.skip(skip);
            items = items.limit(take);

            // Apply sorting
            if (sort != null)
                items = items.sorted(sort);

            List<T> data = items.collect(Collectors.toList());

            _logger.trace(context, "Retrieved %d of %s", data.size(), _typeName);

            return new DataPage<>(data, total);
        }
    }

    /**
     * Gets a page of data items retrieved by a given filter and sorted according to
     * sort parameters.
     * <p>
     * This method shall be called by a public getPageByFilter method from child
     * class that receives FilterParams and converts them into a filter function.
     *
     * @param context     (optional) a context to trace execution through call chain.
     * @param filter        (optional) a filter function to filter items
     * @param paging        (optional) paging parameters
     * @param sort          (optional) sorting parameters
     * @param select        (optional) projection parameters (not used yet)
     * @return a data page of result by filter.
     * @throws ApplicationException when error occured.
     */
    protected DataPage<T> getPageByFilter(IContext context, Predicate<T> filter, PagingParams paging,
                                          Comparator<T> sort, Function<T, T> select) throws ApplicationException {

        DataPage<T> page = getPageByFilter(context, filter, paging, sort);

        Long total = page.getTotal();
        List<T> items = page.getData();
        if (select != null)
            items = page.getData().stream().map(select).collect(Collectors.toList());

        return new DataPage<>(items, total);
    }

    /**
     * Gets a number of items retrieved by a given filter.
     * <p>
     * This method shall be called by a public getCountByFilter method from child class that
     * receives FilterParams and converts them into a filter function.
     *
     * @param context     (optional) a context to trace execution through call chain.
     * @param filter        (optional) a filter function to filter items
     * @return a number of data items that satisfy the filter.
     */
    protected int getCountByFilter(IContext context, Predicate<T> filter) {
        synchronized (_lock) {
            Stream<T> items = this._items.stream();

            // Apply filter
            if (filter != null)
                items = items.filter(filter);

            var res = items.toArray();
            this._logger.trace(context, "Counted %d items", res.length);

            return res.length;
        }
    }

    /**
     * Gets a list of data items retrieved by a given filter and sorted according to
     * sort parameters.
     * <p>
     * This method shall be called by a public getListByFilter method from child
     * class that receives FilterParams and converts them into a filter function.
     *
     * @param context     (optional) a context to trace execution through call chain.
     * @param filter        (optional) a filter function to filter items
     * @param sort          (optional) sorting parameters
     * @return a data list of results by filter.
     */
    protected List<T> getListByFilter(IContext context, Predicate<T> filter, Comparator<T> sort) {

        synchronized (_lock) {
            Stream<T> items = this._items.stream();

            // Apply filter
            if (filter != null)
                items = items.filter(filter);

            // Apply sorting
            if (sort != null)
                items = items.sorted(sort);

            List<T> data = items.collect(Collectors.toList());

            _logger.trace(context, "Retrieved %d of %s", data.size(), _typeName);

            return data;
        }
    }

    /**
     * Gets a list of data items retrieved by a given filter and sorted according to
     * sort parameters.
     * <p>
     * This method shall be called by a public getListByFilter method from child
     * class that receives FilterParams and converts them into a filter function.
     *
     * @param context     (optional) a context to trace execution through call chain.
     * @param filter        (optional) a filter function to filter items
     * @param sort          (optional) sorting parameters
     * @param select        (optional) projection parameters (not used yet)
     * @return a data list of results by filter.
     * @throws ApplicationException when error occured.
     */
    protected List<T> getListByFilter(IContext context, Predicate<T> filter, Comparator<T> sort,
                                      Function<T, T> select) throws ApplicationException {

        var res = getListByFilter(context, filter, sort);
        if (res != null && select != null)
            res = res.stream().map(select).collect(Collectors.toList());
        return res;
    }

    /**
     * Gets a random item from items that match to a given filter.
     * <p>
     * This method shall be called by a public getOneRandom method from child class that
     * receives FilterParams and converts them into a filter function.
     *
     * @param context     (optional) a context to trace execution through call chain.
     * @param filter        (optional) a filter function to filter items.
     * @return a random data item.
     */
    protected T getOneRandom(IContext context, Predicate<T> filter) {
        synchronized (_lock) {
            Stream<T> items = this._items.stream();

            // Apply filter
            if (filter != null) {
                items = items.filter(filter);
            }

            int itemsLen = items.toArray().length;

            var index = RandomInteger.nextInteger(0, itemsLen);
            T item = itemsLen > 0 ? items.collect(Collectors.toList()).get(index) : null;

            if (item != null) {
                this._logger.trace(context, "Retrieved a random item");
            } else {
                this._logger.trace(context, "Nothing to return as random item");
            }

            return item;
        }
    }

    /**
     * Creates a data item.
     *
     * @param context     (optional) a context to trace execution through call chain.
     * @param item          an item to be created.
     * @return a created data item
     */
    public T create(IContext context, T item) throws IOException, ApplicationException {
        synchronized (_lock) {
            // clone object
            var strObj = JsonConverter.toJson(item);
            _items.add(item);

            this._logger.trace(context, "Created item %s", JsonConverter.toMap(strObj).getOrDefault("id", null));

            save(context);

            return item;
        }
    }

    /**
     * Deletes data items that match to a given filter.
     * <p>
     * This method shall be called by a public deleteByFilter method from child
     * class that receives FilterParams and converts them into a filter function.
     *
     * @param context     (optional) a context to trace execution through call chain.
     * @param filter        (optional) a filter function to filter items.
     * @throws ApplicationException when error occured.
     */
    public void deleteByFilter(IContext context, Predicate<T> filter) throws ApplicationException {
        int deleted = 0;
        synchronized (_lock) {
            Stream<T> items = _items.stream();

            if (filter != null) {
                items = items.filter(filter);
                List<T> data = items.collect(Collectors.toList());
                for (T item : data) {
                    _items.remove(item);
                    deleted++;
                }
                _logger.trace(context, "Deleted %d items", deleted);
            }
            if (deleted > 0)
                save(context);
        }
    }
}