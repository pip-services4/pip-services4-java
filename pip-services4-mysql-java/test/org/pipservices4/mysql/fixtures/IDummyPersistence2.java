package org.pipservices4.mysql.fixtures;

import org.pipservices4.commons.data.AnyValueMap;
import org.pipservices4.components.context.IContext;
import org.pipservices4.data.query.*;
import org.pipservices4.persistence.read.IGetter;
import org.pipservices4.persistence.write.IPartialUpdater;
import org.pipservices4.persistence.write.IWriter;

import java.util.List;

public interface IDummyPersistence2 extends IGetter<Dummy2, Long>, IWriter<Dummy2, Long>, IPartialUpdater<Dummy2, Long> {
    DataPage<Dummy2> getPageByFilter(IContext context, FilterParams filter, PagingParams paging);
    long getCountByFilter(IContext context, FilterParams filter);
    List<Dummy2> getListByIds(IContext context, List<Long> ids);

    Dummy2 getOneRandom(IContext context, FilterParams filter);

    List<Dummy2> getListByFilter(IContext context, FilterParams filter);
    Dummy2 getOneById(IContext context, Long id);
    Dummy2 create(IContext context, Dummy2 item);
    Dummy2 update(IContext context, Dummy2 item);
    Dummy2 set(IContext context, Dummy2 item);
    Dummy2 updatePartially(IContext context, Long id, AnyValueMap data);
    Dummy2 deleteById(IContext context, Long id);
    void deleteByIds(IContext context, List<Long> id);
}
