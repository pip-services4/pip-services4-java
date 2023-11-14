package org.pipservices4.rpc.clients;


import org.pipservices4.components.context.IContext;
import org.pipservices4.data.query.*;
import org.pipservices4.commons.errors.*;
import org.pipservices4.rpc.sample.Dummy;


public interface IDummyClient {	
	DataPage<Dummy> getDummies(IContext context, FilterParams filter, PagingParams paging) throws ApplicationException;
    Dummy getDummyById(IContext context, String id) throws ApplicationException;
    Dummy createDummy(IContext context, Dummy entity) throws ApplicationException;
    Dummy updateDummy(IContext context, Dummy entity) throws ApplicationException;
    Dummy deleteDummy(IContext context, String id) throws ApplicationException;
    String checkTraceId(IContext context) throws ApplicationException;
    void raiseException(IContext context) throws ApplicationException;
}
