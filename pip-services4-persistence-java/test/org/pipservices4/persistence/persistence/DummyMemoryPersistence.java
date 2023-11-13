package org.pipservices4.persistence.persistence;

import org.pipservices4.commons.errors.ApplicationException;
import org.pipservices4.components.context.IContext;
import org.pipservices4.data.query.DataPage;
import org.pipservices4.data.query.FilterParams;
import org.pipservices4.data.query.PagingParams;
import org.pipservices4.persistence.sample.Dummy;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

public class DummyMemoryPersistence extends IdentifiableMemoryPersistence<Dummy, String> implements IDummyPersistence {

    protected DummyMemoryPersistence() {
        super(Dummy.class);
    }

    private Predicate<Dummy> composeFilter(FilterParams filter) {
        filter = filter != null ? filter : new FilterParams();
        var key = filter.getAsNullableString("key");

        return (item) -> {
            return key == null || Objects.equals(item.getKey(), key);
        };
    }

    public DataPage<Dummy> getPageByFilter(IContext context, FilterParams filter, PagingParams paging) {
        return super.getPageByFilter(context, composeFilter(filter), paging, null);
    }

    @Override
    public int getCountByFilter(IContext context, FilterParams filter) {
        return super.getCountByFilter(context, composeFilter(filter));
    }

    @Override
    public DataPage<Dummy> getSortedPage(IContext context, Comparator<Dummy> sort) {
        return super.getPageByFilter(context, null, null, sort);
    }

    @Override
    public List<Dummy> getSortedList(IContext context, Comparator<Dummy> sort) throws ApplicationException {
        return super.getListByFilter(context, null, sort, null);
    }

}
