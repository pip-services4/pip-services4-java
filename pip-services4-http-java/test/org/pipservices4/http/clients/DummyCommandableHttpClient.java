package org.pipservices4.http.clients;

import jakarta.ws.rs.core.*;

import org.pipservices4.components.context.IContext;
import org.pipservices4.components.exec.Parameters;
import org.pipservices4.data.query.*;
import org.pipservices4.commons.errors.*;
import org.pipservices4.http.sample.Dummy;

import java.util.HashMap;
import java.util.Map;

public class DummyCommandableHttpClient extends CommandableHttpClient implements IDummyClient{

	public DummyCommandableHttpClient() {
		super("dummy");
	}

	@Override
	public DataPage<Dummy> getDummies(IContext context,
									  FilterParams filter, PagingParams paging) throws ApplicationException {
		
		return callCommand(
			new GenericType<DataPage<Dummy>>() {},
			"get_dummies",
			context,
			Parameters.fromTuples(
				"filter", filter,
				"paging", paging
			)
		);
	}

	@Override
	public Dummy getDummyById(IContext context, String id) throws ApplicationException {
		return callCommand(
			Dummy.class,
			"get_dummy_by_id",
			context,
			Parameters.fromTuples("dummy_id", id)
		);
	}

	@Override
	public Dummy createDummy(IContext context, Dummy entity) throws ApplicationException {
		return callCommand(
			Dummy.class,
			"create_dummy",
			context,
			Parameters.fromTuples("dummy", entity)
		);
	}

	@Override
	public Dummy updateDummy(IContext context, Dummy entity) throws ApplicationException {
		return callCommand(
			Dummy.class,
			"update_dummy",
			context,
			Parameters.fromTuples("dummy", entity)
		);
	}

	@Override
	public Dummy deleteDummy(IContext context, String id) throws ApplicationException {
		return callCommand(
			Dummy.class,
			"delete_dummy",
			context,
			Parameters.fromTuples("dummy_id", id)
		);
	}

	@Override
	public String checkTraceId(IContext context) throws ApplicationException {
		Map<String, String> res =  callCommand(
				HashMap.class,
				"check_trace_id",
				context,
				null
		);

		return res.get("trace_id");
	}

	@Override
	public void raiseException(IContext context) throws ApplicationException {
		callCommand(
			Object.class,
			"raise_exception",
			context,
			new Parameters()
		);
	}

}



