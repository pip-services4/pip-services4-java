package org.pipservices4.swagger.example.services;

import org.pipservices4.commons.convert.JsonConverter;
import org.pipservices4.components.context.IContext;
import org.pipservices4.swagger.example.data.Dummy;
import org.pipservices4.swagger.example.data.DummySchema;
import org.pipservices4.rpc.commands.Command;
import org.pipservices4.rpc.commands.CommandSet;
import org.pipservices4.rpc.commands.ICommand;
import org.pipservices4.commons.convert.TypeCode;
import org.pipservices4.data.query.*;
import org.pipservices4.components.exec.Parameters;
import org.pipservices4.data.validate.FilterParamsSchema;
import org.pipservices4.data.validate.ObjectSchema;
import org.pipservices4.data.validate.PagingParamsSchema;

import java.io.IOException;

public class DummyCommandSet extends CommandSet {
    private final IDummyService _service;

    public DummyCommandSet(IDummyService service) {
        super();
        this._service = service;

        this.addCommand(this.makeGetPageByFilterCommand());
        this.addCommand(this.makeGetOneByIdCommand());
        this.addCommand(this.makeCreateCommand());
        this.addCommand(this.makeUpdateCommand());
        this.addCommand(this.makeDeleteByIdCommand());
    }

    private ICommand makeGetPageByFilterCommand() {
        return new Command(
                "get_dummies",
                new ObjectSchema()
                        .withOptionalProperty("filter", new FilterParamsSchema())
                        .withOptionalProperty("paging", new PagingParamsSchema()),
                (IContext context, Parameters args) -> {
                    var filter = FilterParams.fromValue(args.get("filter"));
                    var paging = PagingParams.fromValue(args.get("paging"));
                    return this._service.getPageByFilter(context, filter, paging);
                }
        );
    }

    private ICommand makeGetOneByIdCommand() {
        return new Command(
                "get_dummy_by_id",
                new ObjectSchema()
                        .withRequiredProperty("dummy_id", TypeCode.String),
                (IContext context, Parameters args) -> {
                    var id = args.getAsString("dummy_id");
                    return this._service.getOneById(context, id);
                }
        );
    }

    private ICommand makeCreateCommand() {
        return new Command(
                "create_dummy",
                new ObjectSchema()
                        .withRequiredProperty("dummy", new DummySchema()),
                (IContext context, Parameters args) -> {
                    Dummy entity;
                    try {
                        entity = JsonConverter.fromJson(Dummy.class, JsonConverter.toJson(args.get("dummy")));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    return this._service.create(context, entity);
                }
        );
    }

    private ICommand makeUpdateCommand() {
        return new Command(
                "update_dummy",
                new ObjectSchema()
                        .withRequiredProperty("dummy", new DummySchema()),
                (IContext context, Parameters args) -> {
                    Dummy entity;
                    try {
                        entity = JsonConverter.fromJson(Dummy.class, JsonConverter.toJson(args.get("dummy")));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    return this._service.update(context, entity);
                }
        );
    }

    private ICommand makeDeleteByIdCommand() {
        return new Command(
                "delete_dummy",
                new ObjectSchema()
                        .withRequiredProperty("dummy_id", TypeCode.String),
                (IContext context, Parameters args) -> {
                    var id = args.getAsString("dummy_id");
                    return this._service.deleteById(context, id);
                }
        );
    }
}
