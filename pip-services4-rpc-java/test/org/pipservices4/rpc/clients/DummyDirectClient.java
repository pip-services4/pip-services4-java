package org.pipservices4.rpc.clients;

import org.pipservices4.components.context.IContext;
import org.pipservices4.data.query.*;
import org.pipservices4.commons.errors.ApplicationException;
import org.pipservices4.components.refer.Descriptor;
import org.pipservices4.rpc.sample.Dummy;
import org.pipservices4.rpc.sample.IDummyService;

public class DummyDirectClient extends DirectClient<IDummyService> implements IDummyClient {

	public DummyDirectClient() {
		super();
		_dependencyResolver.put("service", new Descriptor("pip-services-dummies", "service", "*", "*", "*"));
	}

	@Override
	public DataPage<Dummy> getDummies(IContext context, FilterParams filter, PagingParams paging)
		throws ApplicationException {
		
		filter = filter != null ? filter : new FilterParams();
		paging = paging != null ? paging : new PagingParams();

		var timing = this.instrument(context, "dummy.get_page_by_filter");
		try {
			return _service.getPageByFilter(context, filter, paging);
		} catch (Exception ex) {
			timing.endFailure(ex);
			throw ex;
		} finally {
			timing.endTiming();
		}
	}

	@Override
	public Dummy getDummyById(IContext context, String id)
		throws ApplicationException {
		var timing = this.instrument(context, "dummy.get_one_by_id");
		try {
			return _service.getOneById(context, id);
		} catch (Exception ex) {
			timing.endFailure(ex);
			throw ex;
		} finally {
			timing.endTiming();
		}
	}

	@Override
	public Dummy createDummy(IContext context, Dummy entity)
		throws ApplicationException {
		var timing = this.instrument(context, "dummy.create");
		try {
			return _service.create(context, entity);
		} catch (Exception ex) {
			timing.endFailure(ex);
			throw ex;
		} finally {
			timing.endTiming();
		}
	}

	@Override
	public Dummy updateDummy(IContext context, Dummy entity)
		throws ApplicationException {
		var timing = this.instrument(context, "dummy.update");
		try {
			return _service.update(context, entity);
		} catch (Exception ex) {
			timing.endFailure(ex);
			throw ex;
		} finally {
			timing.endTiming();
		}
	}

	@Override
	public Dummy deleteDummy(IContext context, String id)
		throws ApplicationException {
		var timing = this.instrument(context, "dummy.delete_by_id");
		try {
			return _service.deleteById(context, id);
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
			return this._service.checkTraceId(context);
		} catch (Exception ex) {
			timing.endFailure(ex);
			throw ex;
		} finally {
			timing.endTiming();
		}
	}

	@Override
	public void raiseException(IContext context) 
		throws ApplicationException {
		var timing = this.instrument(context, "dummy.raise_exception");
		try {
			_service.raiseException(context);
		} catch (Exception ex) {
			timing.endFailure(ex);
			throw ex;
		} finally {
			timing.endTiming();
		}

	}

}
