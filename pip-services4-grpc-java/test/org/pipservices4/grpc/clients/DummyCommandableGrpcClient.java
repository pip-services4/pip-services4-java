package org.pipservices4.grpc.clients;

import org.pipservices4.components.context.IContext;
import org.pipservices4.data.query.*;
import org.pipservices4.grpc.sample.Dummy;

import java.util.Map;

public class DummyCommandableGrpcClient extends CommandableGrpcClient implements IDummyClient {

    public DummyCommandableGrpcClient() {
        super("dummy");
    }

    @Override
    public DataPage<Dummy> getDummies(IContext context, FilterParams filter, PagingParams paging) {
        return this.callCommand(DataPage.class,
                "get_dummies",
                context,
                Map.of("filter", filter,
                        "paging", paging)
        );
    }

    @Override
    public Dummy getDummyById(IContext context, String dummyId) {
        return this.callCommand(Dummy.class,
                "get_dummy_by_id",
                context,
                Map.of("dummy_id", dummyId)
        );
    }

    @Override
    public Dummy createDummy(IContext context, Dummy dummy) {
        return this.callCommand(
                Dummy.class,
                "create_dummy",
                context,
                dummy
        );
    }

    @Override
    public Dummy updateDummy(IContext context, Dummy dummy) {
        return this.callCommand(
                Dummy.class,
                "update_dummy",
                context,
                dummy
        );
    }

    @Override
    public Dummy deleteDummy(IContext context, String dummyId) {
        return this.callCommand(
                Dummy.class,
                "delete_dummy",
                context,
                Map.of(
                        "dummy_id", dummyId
                )
        );
    }
}
