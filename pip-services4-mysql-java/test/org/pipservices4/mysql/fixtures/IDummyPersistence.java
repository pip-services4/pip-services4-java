package org.pipservices4.mysql.fixtures;

import org.pipservices4.commons.data.AnyValueMap;
import org.pipservices4.components.context.IContext;
import org.pipservices4.data.query.*;
import org.pipservices4.persistence.read.IGetter;
import org.pipservices4.persistence.write.IPartialUpdater;
import org.pipservices4.persistence.write.IWriter;

import java.util.List;

public interface IDummyPersistence extends IGetter<Dummy, String>, IWriter<Dummy, String>, IPartialUpdater<Dummy, String> {
    DataPage<Dummy> getPageByFilter(IContext context, FilterParams filter, PagingParams paging);
    long getCountByFilter(IContext context, FilterParams filter);
    List<Dummy> getListByIds(IContext context, List<String> ids);

    Dummy getOneRandom(IContext context, FilterParams filter);

    List<Dummy> getListByFilter(IContext context, FilterParams filter);
    Dummy getOneById(IContext context, String id);
    Dummy create(IContext context, Dummy item);
    Dummy update(IContext context, Dummy item);
    Dummy set(IContext context, Dummy item);
    Dummy updatePartially(IContext context, String id, AnyValueMap data);
    Dummy deleteById(IContext context, String id);
    void deleteByIds(IContext context, List<String> id);
}
