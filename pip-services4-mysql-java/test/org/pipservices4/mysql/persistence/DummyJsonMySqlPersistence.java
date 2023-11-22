package org.pipservices4.mysql.persistence;

import org.pipservices4.components.context.IContext;
import org.pipservices4.data.query.*;
import org.pipservices4.mysql.fixtures.Dummy;
import org.pipservices4.mysql.fixtures.IDummyPersistence;

import java.util.List;
import java.util.Map;

public class DummyJsonMySqlPersistence extends IdentifiableJsonMySqlPersistence<Dummy, String>
        implements IDummyPersistence {

    public DummyJsonMySqlPersistence() {
        super(Dummy.class, "dummies_json", null);
    }

    @Override
    protected void defineSchema() {
        this.clearSchema();
        this.ensureTable();
        this.ensureSchema("ALTER TABLE `" + this._tableName + "` ADD `data_key` VARCHAR(50) AS (JSON_UNQUOTE(`data`->\"$.key\"))");
        this.ensureIndex(this._tableName + "_json_key", Map.of("data_key", 1), Map.of("unique", true));
    }

    @Override
    public DataPage<Dummy> getPageByFilter(IContext context, FilterParams filter, PagingParams paging) {
        filter = filter != null ? filter : new FilterParams();
        var key = filter.getAsNullableString("key");

        String filterCondition = null;
        if (key != null)
            filterCondition = "data->'$.key'='" + key + "'";

        return super.getPageByFilter(context, filterCondition, paging, null, null);
    }

    @Override
    public long getCountByFilter(IContext context, FilterParams filter) {
        filter = filter != null ? filter : new FilterParams();
        var key = filter.getAsNullableString("key");

        String filterCondition = null;
        if (key != null)
            filterCondition = "data->'$.key'='" + key + "'";

        return super.getCountByFilter(context, filterCondition);
    }

    @Override
    public Dummy getOneRandom(IContext context, FilterParams filter) {
        filter = filter != null ? filter : new FilterParams();
        var key = filter.getAsNullableString("key");

        String filterCondition = null;
        if (key != null)
            filterCondition = "data->'$.key'='" + key + "'";

        return super.getOneRandom(context, filterCondition);
    }

    @Override
    public List<Dummy> getListByFilter(IContext context, FilterParams filter) {
        filter = filter != null ? filter : new FilterParams();
        var key = filter.getAsNullableString("key");

        String filterCondition = null;
        if (key != null)
            filterCondition = "data->'$.key'='" + key + "'";

        return super.getListByFilter(context, filterCondition, null, null);
    }
}
