package org.pipservices4.rpc.sample;

import org.pipservices4.commons.convert.TypeCode;
import org.pipservices4.rpc.commands.*;
import org.pipservices4.commons.data.AnyValueMap;
import org.pipservices4.data.query.FilterParams;
import org.pipservices4.data.query.PagingParams;
import org.pipservices4.components.exec.Parameters;
import org.pipservices4.data.validate.*;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.Map;

public class DummyCommandSet extends CommandSet {

    private final IDummyService _service;

    public DummyCommandSet(IDummyService service) {
        _service = service;

        addCommand(makeGetPageByFilterCommand());
        addCommand(makeGetOneByIdCommand());
        addCommand(makeCreateCommand());
        addCommand(makeUpdateCommand());
        addCommand(makeDeleteByIdCommand());
        addCommand(makeCheckTraceIdCommand());

        // Commands for errors
        addCommand(makeCreateWithoutValidationCommand());
        addCommand(makeRaiseCommandSetExceptionCommand());
        addCommand(makeRaiseServiceExceptionCommand());

        // V2
        addCommand(makePingCommand());
    }

    private ICommand makeGetPageByFilterCommand() {
        return new Command("get_dummies",
                new ObjectSchema()
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
                        .withRequiredProperty("dummy_id", TypeCode.String),
                (context, args) -> {
                    String dummyId = args.getAsString("dummy_id");

                    return _service.getOneById(context, dummyId);
                });
    }

    private ICommand makeCreateCommand() {
        return new Command("create_dummy", new ObjectSchema()
                .withRequiredProperty("dummy", new DummySchema()),
                (context, args) -> {
                    Dummy dummy = extractDummy(args);
                    return _service.create(context, dummy);
                });
    }

    private ICommand makeUpdateCommand() {
        return new Command("update_dummy", new ObjectSchema()
                .withRequiredProperty("dummy", new DummySchema()),
                (context, args) -> {
                    Dummy dummy = extractDummy(args);
                    return _service.update(context, dummy);
                });
    }

    private ICommand makeDeleteByIdCommand() {
        return new Command("delete_dummy",
                new ObjectSchema()
                        .withRequiredProperty("dummy_id", TypeCode.String),
                (context, args) -> {
                    String dummyId = args.getAsString("dummy_id");
                    return _service.deleteById(context, dummyId);

                }
        );
    }

    private ICommand makeCreateWithoutValidationCommand() {
        return new Command("create_dummy_without_validation", null,
                (context, parameters) -> {
                    return null;
                });
    }

    private ICommand makeRaiseCommandSetExceptionCommand() {
        return new Command("raise_commandset_error",
                new ObjectSchema()
                        .withRequiredProperty("dummy", new DummySchema()),
                (context, parameters) -> {
                    throw new RuntimeException("Dummy error in commandset!");
                }
        );
    }

    private ICommand makeCheckTraceIdCommand() {
        return new Command("check_trace_id",
                new ObjectSchema(),
                (context, parameters) -> {
                    var value = this._service.checkTraceId(context);
                    return Map.of("trace_id", value);
                }
        );
    }

    private ICommand makeRaiseServiceExceptionCommand() {
        return new Command("raise_exception", new ObjectSchema(),
                (context, parameters) -> {
                    _service.raiseException(context);
                    return null;
                }
        );
    }

    private ICommand makePingCommand() {
        return new Command("ping_dummy", null, (context, parameters) -> {
            return _service.ping();
        });
    }


    private static Dummy extractDummy(Parameters args) {
        AnyValueMap map = args.getAsMap("dummy");

        String id = map.getAsNullableString("id");
        String key = map.getAsNullableString("key");
        String content = map.getAsNullableString("content");
        var arrayObj = map.getAsNullableArray("array");

        var arrayList = new ArrayList<SubDummy>();
        if (arrayObj != null) {
            for (var item : arrayObj) {
                final ObjectMapper mapper = new ObjectMapper(); // jackson's objectmapper
                final SubDummy subDummy = mapper.convertValue(item, SubDummy.class);
                arrayList.add(subDummy);
            }
        }

        return new Dummy(id, key, content, arrayList);
    }

//	private static Dummy extractDummy(Parameters args) {
//		AnyValueMap map = args.getAsMap("dummy");
//
//		String id = map.getAsNullableString("id");
//		String key = map.getAsNullableString("key");
//		String content = map.getAsNullableString("content");
//		List<SubDummy> array = new ArrayList<>();
//		map.getAsArrayWithDefault("array", new AnyValueArray()).forEach(el -> array.add((SubDummy)el));
//
//		return new Dummy(id, key, content, array);
//	}
}
