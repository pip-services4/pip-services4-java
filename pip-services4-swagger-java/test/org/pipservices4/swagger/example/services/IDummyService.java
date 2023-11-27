package org.pipservices4.swagger.example.services;

import org.pipservices4.components.context.IContext;
import org.pipservices4.swagger.example.data.Dummy;
import org.pipservices4.data.query.*;

public interface IDummyService {
    DataPage<Dummy> getPageByFilter(IContext context, FilterParams filter, PagingParams paging);
    Dummy getOneById(IContext context, String id);
    Dummy create(IContext context, Dummy entity);
    Dummy update(IContext context, Dummy entity);
    Dummy deleteById(IContext context, String id);
}
