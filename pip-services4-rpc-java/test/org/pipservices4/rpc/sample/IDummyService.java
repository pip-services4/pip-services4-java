package org.pipservices4.rpc.sample;

import org.pipservices4.data.query.*;
import org.pipservices4.commons.errors.*;
import org.pipservices4.components.context.IContext;

public interface IDummyService {
	
	DataPage<Dummy> getPageByFilter(IContext context, FilterParams filter, PagingParams paging) throws ApplicationException;
    Dummy getOneById(IContext context, String id);
    Dummy create(IContext context, Dummy entity);
    Dummy update(IContext context, Dummy entity);
    Dummy deleteById(IContext context, String id);

    String checkTraceId(IContext context);
    void raiseException(IContext context) throws ApplicationException;

    boolean ping() throws ApplicationException;
}
