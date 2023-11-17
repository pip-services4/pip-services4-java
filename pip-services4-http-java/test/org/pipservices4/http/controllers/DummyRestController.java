package org.pipservices4.http.controllers;

import jakarta.ws.rs.HttpMethod;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Response;
import org.pipservices4.components.config.ConfigParams;
import org.pipservices4.commons.convert.JsonConverter;
import org.pipservices4.commons.convert.TypeCode;
import org.pipservices4.components.context.Context;
import org.pipservices4.data.query.*;
import org.pipservices4.commons.errors.ApplicationException;
import org.pipservices4.commons.errors.ConfigException;
import org.pipservices4.components.refer.Descriptor;
import org.pipservices4.components.refer.IReferences;
import org.pipservices4.components.refer.ReferenceException;
import org.pipservices4.data.validate.FilterParamsSchema;
import org.pipservices4.data.validate.ObjectSchema;
import org.pipservices4.http.sample.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class DummyRestController extends RestController {
    private IDummyService _service;
    private int _numberOfCalls = 0;
    private String _swaggerContent;
    private String _swaggerPath;

    public DummyRestController() {
        this._dependencyResolver.put("service", new Descriptor("pip-services-dummies", "service", "default", "*", "*"));
    }

    @Override
    public void configure(ConfigParams config) throws ConfigException {
        super.configure(config);

        this._swaggerContent = config.getAsNullableString("swagger.content");
        this._swaggerPath = config.getAsNullableString("swagger.path");
    }

    public void setReferences(IReferences references) throws ReferenceException, ConfigException {
        super.setReferences(references);
        this._service = this._dependencyResolver.getOneRequired(IDummyService.class, "service");
    }

    public int getNumberOfCalls() {
        return this._numberOfCalls;
    }

    public Object incrementNumberOfCalls(ContainerRequestContext req) {
        this._numberOfCalls++;
        return null;
    }

    private Response getPageByFilter(ContainerRequestContext req) {
        try {
            var res = this._service.getPageByFilter(
                    Context.fromTraceId(this.getTraceId(req)),
                    new FilterParams(req.getUriInfo().getPathParameters()),
                    PagingParams.fromValue(req.getUriInfo().getPathParameters())
            );
            return this.sendResult(res);
        } catch (ApplicationException err) {
            return sendError(err);
        }

    }

    private Response getOneById(ContainerRequestContext req) {
        var res = this._service.getOneById(
                Context.fromTraceId(this.getTraceId(req)),
                req.getUriInfo().getPathParameters().get("dummy_id").get(0)
        );
        return this.sendResult(res);
    }

    private Response create(ContainerRequestContext req) {
        try {
            var res = this._service.create(
                    Context.fromTraceId(this.getTraceId(req)),
                    JsonConverter.fromJson(
                            Dummy.class,
                            new String(req.getEntityStream().readAllBytes(), StandardCharsets.UTF_8)
                    )
            );
            return this.sendCreatedResult(res);
        } catch (IOException err) {
            return sendError(err);
        }
    }

    private Response update(ContainerRequestContext req) {
        try {
            var res = this._service.update(
                    Context.fromTraceId(this.getTraceId(req)),
                    JsonConverter.fromJson(
                            Dummy.class,
                            new String(req.getEntityStream().readAllBytes(), StandardCharsets.UTF_8)
                    )
            );
            return this.sendResult(res);
        } catch (IOException err) {
            return sendError(err);
        }
    }

    private Response deleteById(ContainerRequestContext req) {
        var res = this._service.deleteById(
                Context.fromTraceId(this.getTraceId(req)),
                req.getUriInfo().getPathParameters().get("dummy_id").stream().findFirst().get()
        );
        return this.sendDeletedResult(res);
    }

    private Response checkTraceId(ContainerRequestContext req) {
        var result = this._service.checkTraceId(Context.fromTraceId(this.getTraceId(req)));
        return this.sendResult(Map.of("trace_id", result));
    }

    private Response raiseException(ContainerRequestContext req) {
        try {
            this._service.raiseException(Context.fromTraceId(this.getTraceId(req)));
            return sendEmptyResult();
        } catch (Exception ex) {
            return sendError(ex);
        }
    }

    @Override
    public void register() {
        this.registerInterceptor("/dummies$", this::incrementNumberOfCalls);

        this.registerRoute(
                HttpMethod.GET, "/dummies",
                new ObjectSchema()
                        .withOptionalProperty("skip", TypeCode.String)
                        .withOptionalProperty("take", TypeCode.String)
                        .withOptionalProperty("total", TypeCode.String)
                        .withOptionalProperty("body", new FilterParamsSchema()),
                this::getPageByFilter
        );

        this.registerRoute(
                HttpMethod.GET, "/dummies/check/trace_id",
                new ObjectSchema(),
                this::checkTraceId
        );

        this.registerRoute(
                HttpMethod.GET, "/dummies/{dummy_id}",
                new ObjectSchema()
                        .withRequiredProperty("dummy_id", TypeCode.String),
                this::getOneById
        );

        this.registerRoute(
                HttpMethod.POST, "/dummies",
                new ObjectSchema()
                        .withRequiredProperty("body", new DummySchema()),
                this::create
        );

        this.registerRoute(
                HttpMethod.PUT, "/dummies",
                new ObjectSchema()
                        .withRequiredProperty("body", new DummySchema()),
                this::update
        );

        this.registerRoute(
                HttpMethod.DELETE, "/dummies/{dummy_id}",
                new ObjectSchema()
                        .withRequiredProperty("dummy_id", TypeCode.String),
                this::deleteById
        );

        this.registerRoute(
                HttpMethod.POST, "/dummies/raise_exception",
                new ObjectSchema(),
                this::raiseException
        );

        if (this._swaggerContent != null)
            this.registerOpenApiSpec(this._swaggerContent);

        if (this._swaggerPath != null)
            this.registerOpenApiSpecFromFile(this._swaggerPath);
    }


}
