package org.pipservices4.http.clients;

import jakarta.ws.rs.HttpMethod;
import org.pipservices4.components.context.IContext;
import org.pipservices4.data.query.*;
import org.pipservices4.commons.errors.ApplicationException;
import org.pipservices4.http.sample.Dummy;

import java.util.HashMap;

public class DummyRestClient extends RestClient implements IDummyClient{
    @Override
    public DataPage<Dummy> getDummies(IContext context, FilterParams filter, PagingParams paging) throws ApplicationException {
        String route = "/dummies";

        route = this.addFilterParams(route, filter);
        route = this.addPagingParams(route, paging);

        var timing = this.instrument(context, "dummy.get_page_by_filter");
        try {
            return this.call(DataPage.class, context,HttpMethod.GET, route, null);
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
        try {
            return this.call(Dummy.class, context,HttpMethod.GET, "/dummies/" + id, null);
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
        try {
            return this.call(Dummy.class, context, HttpMethod.POST, "/dummies", entity);
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
        try {
            return this.call(Dummy.class,context , HttpMethod.PUT, "/dummies", entity);
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
        try {
            return this.call(Dummy.class, context, HttpMethod.DELETE, "/dummies/" + id, null);
        } catch (Exception ex) {
            timing.endFailure(ex);
            throw ex;
        } finally {
            timing.endTiming();
        }
    }

    @Override
    public String checkTraceId(IContext context) throws ApplicationException {
        var timing = this.instrument(context, "dummy.check_trace_id");
        try {
            HashMap<String, String> result = this.call(HashMap.class, context, HttpMethod.GET, "/dummies/check/trace_id", null);
            return result.getOrDefault("trace_id", null);
        } catch (Exception ex) {
            timing.endFailure(ex);
            throw ex;
        } finally {
            timing.endTiming();
        }
    }

    @Override
    public void raiseException(IContext context) throws ApplicationException {
        var timing = this.instrument(context, "dummy.raise_exception");
        try {
            throw this.call(ApplicationException.class, context, HttpMethod.POST, "/dummies/raise_exception", null);
        } catch (Exception ex) {
            timing.endFailure(ex);
            throw ex;
        } finally {
            timing.endTiming();
        }
    }
}
