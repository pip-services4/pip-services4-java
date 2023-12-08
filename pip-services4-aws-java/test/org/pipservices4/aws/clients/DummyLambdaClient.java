package org.pipservices4.aws.clients;

import org.pipservices4.aws.Dummy;
import org.pipservices4.aws.IDummyClient;
import org.pipservices4.commons.data.AnyValueMap;
import org.pipservices4.components.context.IContext;
import org.pipservices4.data.query.*;
import org.pipservices4.commons.errors.ApplicationException;


public class DummyLambdaClient extends LambdaClient implements IDummyClient {
    @Override
    public DataPage<Dummy> getDummies(IContext context, FilterParams filter, PagingParams paging) throws ApplicationException {
        var timing = this.instrument(context, "dummy.get_page_by_filter");
        var params = new AnyValueMap();
        params.setAsObject("filter", filter);
        params.setAsObject("paging", paging);
        try {
            return call(DataPage.class, "get_dummies", context, params);
        } catch (Exception ex) {
            timing.endFailure(ex);
            throw ex;
        } finally {
            timing.endTiming();
        }
    }

    @Override
    public Dummy getDummyById(IContext context, String id) throws ApplicationException {
        var timing = this.instrument(context, "dummy.get_one_by_id");
        var params = new AnyValueMap();
        params.setAsObject("dummy_id", id);
        try {
            return call(Dummy.class, "get_dummy_by_id", context, params);
        } catch (Exception ex) {
            timing.endFailure(ex);
            throw ex;
        } finally {
            timing.endTiming();
        }
    }

    @Override
    public Dummy createDummy(IContext context, Dummy entity) throws ApplicationException {
        var timing = this.instrument(context, "dummy.create");
        var params = new AnyValueMap();
        params.setAsObject("dummy", entity);
        try {
            return this.call(Dummy.class, "create_dummy", context, params);
        } catch (Exception ex) {
            timing.endFailure(ex);
            throw ex;
        } finally {
            timing.endTiming();
        }
    }

    @Override
    public Dummy updateDummy(IContext context, Dummy entity) throws ApplicationException {
        var timing = this.instrument(context, "dummy.update");
        var params = new AnyValueMap();
        params.setAsObject("dummy", entity);
        try {
            return this.call(Dummy.class,"update_dummy", context , params);
        } catch (Exception ex) {
            timing.endFailure(ex);
            throw ex;
        } finally {
            timing.endTiming();
        }
    }

    @Override
    public Dummy deleteDummy(IContext context, String id) throws ApplicationException {
        var timing = this.instrument(context, "dummy.delete_by_id");
        var params = new AnyValueMap();
        params.setAsObject("dummy_id", id);
        try {
            return this.call(Dummy.class, "delete_dummy", context, params);
        } catch (Exception ex) {
            timing.endFailure(ex);
            throw ex;
        } finally {
            timing.endTiming();
        }
    }
}
