package org.pipservices4.commons.data;

import java.util.List;

/**
 * Data transfer object that is used to pass results of paginated queries.
 * It contains items of retrieved page and optional total number of items.
 * <p>
 * Most often this object type is used to send responses to paginated queries.
 * Pagination parameters are defined by {@link TokenizedPagingParams} object.
 * <p>
 * The <code>token</code> parameter in the {@link TokenizedPagingParams} there means where to start the searxh.
 * The <code>takes</code> parameter sets number of items to return in the page.
 * And the optional <code>total</code> parameter tells to return total number of items in the query.
 * <p>
 * The data page returns a token that shall be passed to the next search as a starting point.
 * <p>
 * Remember: not all implementations support the <code>total</code> parameter
 * because its generation may lead to severe performance implications.
 *
 * @see PagingParams
 * <p>
 * ### Example ###
 * <pre>
 * {@code
 * DataPage<MyData> page = myDataClient.getDataByFilter(
 *  "123",
 *  FilterParams.fromTuples("completed": true),
 *  new TokenizedPagingParams(null, 100, true)
 * );
 *
 * value1.getAsInteger();   // Result: 123
 * value1.getAsString();    // Result: "123.456"
 * value1.getAsFloat();     // Result: 123.456
 * }
 * </pre>
 */
public class TokenizedDataPage<T> {
    /**
     * The items of the retrieved page.
     */
    public List<T> data;
    /**
     * The starting point for the next search.
     */
    public String token;
    /**
     * The total amount of items in a request.
     */
    public Integer total;

    /**
     * Creates a new instance of data page and assigns its values.
     *
     * @param data  a list of items from the retrieved page.
     * @param token (optional) a token to define astarting point for the next search.
     * @param total (optional) a total number of objects in the result.
     */
    public TokenizedDataPage(List<T> data, String token, int total) {
        this.total = total;
        this.token = token;
        this.data = data;
    }

    public TokenizedDataPage() {
        this.total = null;
        this.token = null;
        this.data = null;
    }
}
