package org.pipservices4.aws.containers;

import org.pipservices4.aws.Dummy;
import org.pipservices4.aws.DummyFactory;
import org.pipservices4.aws.DummySchema;
import org.pipservices4.aws.IDummyService;
import org.pipservices4.commons.convert.JsonConverter;
import org.pipservices4.commons.convert.TypeCode;
import org.pipservices4.components.context.Context;
import org.pipservices4.data.query.*;
import org.pipservices4.commons.errors.ConfigException;
import org.pipservices4.components.refer.Descriptor;
import org.pipservices4.components.refer.IReferences;
import org.pipservices4.components.refer.ReferenceException;
import org.pipservices4.data.validate.FilterParamsSchema;
import org.pipservices4.data.validate.ObjectSchema;

import java.io.IOException;
import java.util.Map;

public class DummyLambdaFunction extends LambdaFunction {
    private IDummyService _service;

    public DummyLambdaFunction() {
        super("dummy", "Dummy lambda function");
        this._dependencyResolver.put("service", new Descriptor("pip-services-dummies", "service", "default", "*", "*"));
        _factories.add(new DummyFactory());
    }

    public void setReferences(IReferences references) throws ReferenceException, ConfigException {
        super.setReferences(references);
        this._service = this._dependencyResolver.getOneRequired(IDummyService.class, "service");
    }

    private DataPage<Dummy> getPageByFilter(Map<String, Object> params) {
        return _service.getPageByFilter(
                Context.fromTraceId(params.get("trace_id") != null ? params.get("trace_id").toString() : null),
                FilterParams.fromValue(params.get("filter")),
                PagingParams.fromValue(params.get("paging"))
        );

    }

    private Dummy getOneById(Map<String, Object> params) {
        return _service.getOneById(
                Context.fromTraceId(params.get("trace_id") != null ? params.get("trace_id").toString() : null),
                params.get("dummy_id").toString()
        );
    }

    private Dummy create(Map<String, Object> params) {
        try {
            return _service.create(
                    Context.fromTraceId(params.get("trace_id") != null ? params.get("trace_id").toString() : null),
                    JsonConverter.fromJson(
                            Dummy.class,
                            params.get("dummy").toString()
                    )
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Dummy update(Map<String, Object> params) {
        try {
            return _service.update(
                    Context.fromTraceId(params.get("trace_id") != null ? params.get("trace_id").toString() : null),
                    JsonConverter.fromJson(
                            Dummy.class,
                            params.get("dummy").toString()
                    )
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Dummy deleteById(Map<String, Object> params) {
        return _service.deleteById(
                Context.fromTraceId(params.get("trace_id") != null ? params.get("trace_id").toString() : null),
                params.get("dummy_id").toString()
        );
    }

    @Override
    public void register() {
        this.registerAction(
                "get_dummies",
                new ObjectSchema()
                        .withOptionalProperty("skip", TypeCode.String)
                        .withOptionalProperty("take", TypeCode.String)
                        .withOptionalProperty("total", TypeCode.String)
                        .withOptionalProperty("body", new FilterParamsSchema()),
                this::getPageByFilter
        );

        this.registerAction(
                "get_dummy_by_id",
                new ObjectSchema()
                        .withRequiredProperty("dummy_id", TypeCode.String),
                this::getOneById
        );

        this.registerAction(
                "create_dummy",
                new ObjectSchema()
                        .withRequiredProperty("dummy", new DummySchema()),
                this::create
        );

        this.registerAction(
                "update_dummy",
                new ObjectSchema()
                        .withRequiredProperty("dummy", new DummySchema()),
                this::update
        );

        this.registerAction(
                "delete_dummy",
                new ObjectSchema()
                        .withRequiredProperty("dummy_id", TypeCode.String),
                this::deleteById
        );
    }


}
