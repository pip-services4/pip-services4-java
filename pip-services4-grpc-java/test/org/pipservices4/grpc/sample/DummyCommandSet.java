package org.pipservices4.grpc.sample;

import org.pipservices4.rpc.commands.*;
import org.pipservices4.data.query.*;
import org.pipservices4.components.exec.Parameters;
import org.pipservices4.data.validate.*;


public class DummyCommandSet extends CommandSet {

    private final IDummyService _service;

    public DummyCommandSet(IDummyService service) {
        _service = service;

        addCommand(makeGetPageByFilterCommand());
        addCommand(makeGetOneByIdCommand());
        addCommand(makeCreateCommand());
        addCommand(makeUpdateCommand());
        addCommand(makeDeleteByIdCommand());
    }

    private ICommand makeGetPageByFilterCommand() {
        return new Command("get_dummies",
                new ObjectSchema()
                        .withOptionalProperty("trace_id", String.class)
                        .withOptionalProperty("filter", new FilterParamsSchema())
                        .withOptionalProperty("paging", new PagingParamsSchema()),
                (context, args) -> {
                    FilterParams filter = FilterParams.fromValue(args.get("filter"));
                    PagingParams paging = PagingParams.fromValue(args.get("paging"));

                    return _service.getPageByFilter(context, filter, paging);
                });
    }

    private ICommand makeGetOneByIdCommand() {
        return new Command("get_dummy_by_id",
                new ObjectSchema()
                        .withRequiredProperty("dummy_id", org.pipservices4.commons.convert.TypeCode.String),
                (context, args) -> {
                    String dummyId = args.getAsString("dummy_id");

                    return _service.getOneById(context, dummyId);
                });
    }

    private ICommand makeCreateCommand() {
        return new Command("create_dummy", new DummySchema(),
                (context, args) -> {
                    Dummy dummy = extractDummy(args);
                    return _service.create(context, dummy);
                });
    }

    private ICommand makeUpdateCommand() {
        return new Command("update_dummy", new DummySchema(),
                (context, args) -> {
                    Dummy dummy = extractDummy(args);
                    return _service.update(context, dummy);
                });
    }

    private ICommand makeDeleteByIdCommand() {
        return new Command("delete_dummy",
                new ObjectSchema()
                        .withRequiredProperty("dummy_id", org.pipservices4.commons.convert.TypeCode.String),
                (context, args) -> {
                    String dummyId = args.getAsString("dummy_id");
                    return _service.deleteById(context, dummyId);

                }
        );
    }

    private static Dummy extractDummy(Parameters args) {
        String id = args.getAsNullableString("id");
        String key = args.getAsNullableString("key");
        String content = args.getAsNullableString("content");

        return new Dummy(id, key, content);
    }
}
