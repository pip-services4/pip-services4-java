package org.pipservices4.aws.clients;

import com.fasterxml.jackson.core.type.TypeReference;
import org.pipservices4.aws.Dummy;
import org.pipservices4.aws.IDummyClient;
import org.pipservices4.components.context.IContext;
import org.pipservices4.components.exec.Parameters;
import org.pipservices4.data.query.*;
import org.pipservices4.commons.errors.*;


public class DummyCommandableLambdaClient extends CommandableLambdaClient implements IDummyClient {

	public DummyCommandableLambdaClient() {
		super("dummy");
	}

	@Override
	public DataPage<Dummy> getDummies(IContext context,
									  FilterParams filter, PagingParams paging) throws ApplicationException {
		
		return callCommand(
			new TypeReference<DataPage<Dummy>>() {},
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
}



