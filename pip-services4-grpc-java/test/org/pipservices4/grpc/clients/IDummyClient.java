package org.pipservices4.grpc.clients;

import org.pipservices4.components.context.IContext;
import org.pipservices4.data.query.*;
import org.pipservices4.grpc.sample.Dummy;

public interface IDummyClient {
    DataPage<Dummy> getDummies(IContext context, FilterParams filter, PagingParams paging);
    Dummy getDummyById(IContext context, String dummyId);
    Dummy createDummy(IContext context, Dummy dummy);
    Dummy updateDummy(IContext context, Dummy dummy);
    Dummy deleteDummy(IContext context, String dummyId);
}
