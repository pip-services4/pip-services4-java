package org.pipservices4.data.query;


import org.pipservices4.commons.data.AnyValueMap;

/**
 * Data transfer object to pass tokenized paging parameters for queries.
 * It can be used for complex paging scenarios, like paging across multiple databases
 * where the previous state is encoded in a token. The token is usually retrieved from
 * the previous response. The initial request shall go with token == <code>null</code>
 *
 * The page is defined by two parameters:
 * - the <code>token</code> token that defines a starting point for the search.
 * - the <code>take</code> parameter sets how many items to return in a page.
 * - additionally, the optional <code>total</code> parameter tells to return total number of items in the query.
 *
 * Remember: not all implementations support the <code>total</code> parameter
 * because its generation may lead to severe performance implications.
 *
 * <pre>
 * {@code
 * var filter = FilterParams.fromTuples("type", "Type1");
 * var paging = new TokenizedPagingParams(null, 100);
 *
 * var page = myDataClient.getDataByFilter(filter, paging);
 * }
 * </pre>
 */
public class TokenizedPagingParams {
    /**
     * The start token
     */
    public String token;
    /**
     * The number of items to return.
     */
    public Integer take;
    /**
     * The flag to return the total number of items.
     */
    public Boolean total;

    /**
     * Creates a new instance and sets its values.
     *
     * @param token token that defines a starting point for the search.
     * @param take  the number of items to return.
     * @param total true to return the total number of items.
     */
    public TokenizedPagingParams(String token, Integer take, Boolean total) {
        this.token = token;
        this.take = take;
        this.total = total;

        // This is for correctly using PagingParams with gRPC. gRPC defaults to 0 when take is null,
        // so we have to set it back to null if we get 0 in the constructor.
        if (this.take == 0)
            this.take = null;
    }

    public TokenizedPagingParams() {
    }

    /**
     * Gets the number of items to return in a page.
     *
     * @param maxTake the maximum number of items to return.
     * @return the number of items to return.
     */
    public int getTake(int maxTake) {
        if (this.take == null) return maxTake;
        if (this.take < 0) return 0;
        if (this.take > maxTake) return maxTake;
        return this.take;
    }

    /**
     * Converts specified value into TokenizedPagingParams.
     *
     * @param value value to be converted
     * @return a newly created PagingParams.
     */
    public static TokenizedPagingParams fromValue(Object value) {
        if (value instanceof TokenizedPagingParams) {
            return (TokenizedPagingParams) value;
        }

        AnyValueMap map = AnyValueMap.fromValue(value);
        return TokenizedPagingParams.fromMap(map);
    }

    /**
     * Creates a new TokenizedPagingParams from a list of key-value pairs called tuples.
     *
     * @param tuples a list of values where odd elements are keys and the following even elements are values
     * @return a newly created TokenizedPagingParams.
     */
    public static TokenizedPagingParams fromTuples(Object... tuples) {
        AnyValueMap map = AnyValueMap.fromTuplesArray(tuples);
        return TokenizedPagingParams.fromMap(map);
    }

    /**
     * Creates a new TokenizedPagingParams and sets it parameters from the specified map
     *
     * @param map a AnyValueMap or StringValueMap to initialize this TokenizedPagingParams
     * @return a newly created PagingParams.
     */
    public static TokenizedPagingParams fromMap(Object map) {
        AnyValueMap mapValue = AnyValueMap.fromValue(map);
        String token = mapValue.getAsNullableString("token");
        Integer take = mapValue.getAsNullableInteger("take");
        Boolean total = mapValue.getAsBooleanWithDefault("total", false);
        return new TokenizedPagingParams(token, take, total);
    }
}
