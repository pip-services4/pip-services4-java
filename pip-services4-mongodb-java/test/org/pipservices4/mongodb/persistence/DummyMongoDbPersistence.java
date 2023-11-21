package org.pipservices4.mongodb.persistence;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.mongodb.client.model.IndexOptions;
import org.bson.Document;
import org.bson.conversions.*;
import org.pipservices4.components.context.IContext;
import org.pipservices4.data.query.*;
import org.pipservices4.components.run.*;

import com.mongodb.client.model.Filters;
import org.pipservices4.mongodb.fixtures.Dummy;
import org.pipservices4.mongodb.fixtures.IDummyPersistence;

import javax.print.Doc;

public class DummyMongoDbPersistence extends IdentifiableMongoDbPersistence<Dummy, String>
        implements IDummyPersistence, ICleanable {

    public DummyMongoDbPersistence() {
        super("dummies", Dummy.class);
    }

    @Override
    protected void defineSchema() {
        this.ensureIndex(new Document("key", 1), new IndexOptions());
    }

    public DataPage<Dummy> getPageByFilter(IContext context, FilterParams filter, PagingParams paging) {
        filter = filter != null ? filter : new FilterParams();

        List<Bson> filters = new ArrayList<Bson>();

        String key = filter.getAsNullableString("key");
        if (key != null)
            filters.add(Filters.eq("key", key));

        String keys = filter.getAsNullableString("keys");
        if (keys != null)
            filters.add(Filters.in("key", Arrays.stream(keys.split(",")).toList()));

        Bson filterDefinition = !filters.isEmpty() ? Filters.and(filters) : null;

        return super.getPageByFilter(context, filterDefinition, paging, null, null);
    }

    @Override
    public long getCountByFilter(IContext context, FilterParams filter) {
        filter = filter != null ? filter : new FilterParams();
        var key = filter.getAsNullableString("key");

        var filterCondition = new Document();

        if (key != null)
            filterCondition.put("key", key);

        return super.getCountByFilter(context, filterCondition);
    }
}
