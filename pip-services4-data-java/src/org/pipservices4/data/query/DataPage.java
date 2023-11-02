package org.pipservices4.data.query;

import java.util.*;

import com.fasterxml.jackson.annotation.*;

/**
 * Data transfer object that is used to pass results of paginated queries.
 * It contains items of retrieved page and optional total number of items.
 * <p>
 * Most often this object type is used to send responses to paginated queries.
 * Pagination parameters are defined by {@link PagingParams} object.
 * The <code>skip</code> parameter in the {@link PagingParams} there means how many items to skip.
 * The <code>takes</code> parameter sets number of items to return in the page.
 * And the optional <code>total</code> parameter tells to return total number of items in the query.
 * <p>
 * Remember: not all implementations support <code>total</code> parameter
 * because its generation may lead to severe performance implications.
 * <p>
 * ### Example ###
 * <pre>
 * {@code
 * myDataClient.getDataByFilter(
 *   "123",
 *   FilterParams.fromTuples("completed", true),
 *   new PagingParams(0, 100, true),
 *   (DataPage<MyData> page) -> {
 *       System.out.println("Items: ");
 *       for (MyData item : page.getData()) {
 *         System.out.println(item);
 *       }
 *       System.out.println("Total items: " + page.getTotal());
 *   };
 * );
 * }
 * </pre>
 *
 * @param <T> class type
 * @see PagingParams
 */
public class DataPage<T> {
    /**
     * The total amount of items in a request.
     */
    private Long _total;
    /**
     * The items of the retrieved page.
     */
    private List<T> _data;

    public DataPage() {
    }

    /**
     * Creates a new instance of data page and assigns its values.
     *
     * @param data a list of items from the retrieved page.
     */
    public DataPage(List<T> data) {
        this(data, null);
    }

    /**
     * Creates a new instance of data page and assigns its values.
     *
     * @param data  a list of items from the retrieved page.
     * @param total (optional) .
     */
    public DataPage(List<T> data, Long total) {
        _total = total;
        _data = data;
    }

    @JsonProperty("total")
    public Long getTotal() {
        return _total;
    }

    public void setTotal(Long value) {
        _total = value;
    }

    @JsonProperty("data")
    public List<T> getData() {
        return _data;
    }

    public void setData(List<T> value) {
        _data = value;
    }
}
