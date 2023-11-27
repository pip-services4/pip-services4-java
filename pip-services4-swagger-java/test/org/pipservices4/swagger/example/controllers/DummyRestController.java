package org.pipservices4.swagger.example.controllers;

import org.pipservices4.commons.errors.ApplicationException;
import org.pipservices4.components.context.Context;
import org.pipservices4.http.controllers.RestController;
import org.pipservices4.swagger.example.data.Dummy;
import org.pipservices4.swagger.example.data.DummySchema;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Response;
import org.glassfish.jersey.process.Inflector;
import org.pipservices4.commons.convert.JsonConverter;
import org.pipservices4.commons.convert.TypeCode;
import org.pipservices4.data.query.*;
import org.pipservices4.commons.errors.ConfigException;
import org.pipservices4.components.refer.Descriptor;
import org.pipservices4.components.refer.IReferences;
import org.pipservices4.components.refer.ReferenceException;
import org.pipservices4.data.validate.ObjectSchema;
import org.pipservices4.http.controllers.RestController;
import org.pipservices4.swagger.example.services.IDummyService;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class DummyRestController extends RestController {
    private IDummyService _service;

    public DummyRestController() {
        this._dependencyResolver.put("service", new Descriptor("pip-services-dummies", "service", "default", "*", "*"));
    }

    @Override
    public void setReferences(IReferences references) throws ReferenceException, ConfigException {
        super.setReferences(references);
        this._service = this._dependencyResolver.getOneRequired(IDummyService.class, "service");
    }

    private Response getPageByFilter(ContainerRequestContext req) {
        try {
            var result = this._service.getPageByFilter(
                    Context.fromTraceId(getTraceId(req)),
                    new FilterParams(req.getUriInfo().getPathParameters()),
                    PagingParams.fromValue(req.getUriInfo().getPathParameters())
            );
            return this.sendResult(result);
        } catch (Exception ex) {
            return this.sendError(ex);
        }
    }

    private Response getOneById(ContainerRequestContext req) {
        try {
            var result = this._service.getOneById(
                    Context.fromTraceId(getTraceId(req)),
                    req.getUriInfo().getPathParameters().get("dummy_id").get(0)
            );
            return this.sendResult(result);
        } catch (Exception ex) {
            return this.sendError(ex);
        }
    }

    private Response create(ContainerRequestContext req) {
        try {
            var result = this._service.create(
                    Context.fromTraceId(getTraceId(req)),
                    JsonConverter.fromJson(
                            Dummy.class,
                            new String(req.getEntityStream().readAllBytes(), StandardCharsets.UTF_8)
                    )
            );
            return this.sendCreatedResult(result);
        } catch (Exception ex) {
            return this.sendError(ex);
        }
    }

    private Response update(ContainerRequestContext req) {
        try {
            var result = this._service.update(
                    Context.fromTraceId(getTraceId(req)),
                    JsonConverter.fromJson(
                            Dummy.class,
                            new String(req.getEntityStream().readAllBytes(), StandardCharsets.UTF_8)
                    )
            );
            return this.sendResult(result);
        } catch (Exception ex) {
            return this.sendError(ex);
        }
    }

    private Response deleteById(ContainerRequestContext req) {
        try {
            var result = this._service.deleteById(
                    Context.fromTraceId(getTraceId(req)),
                    req.getUriInfo().getPathParameters().get("dummy_id").get(0)
            );
            return this.sendDeletedResult(result);
        } catch (Exception ex) {
            return this.sendError(ex);
        }
    }

    @Override
    public void register() {
        this.registerRoute(
                "get", "/dummies",
                new ObjectSchema()
                        .withOptionalProperty("skip", TypeCode.String)
                        .withOptionalProperty("take", TypeCode.String)
                        .withOptionalProperty("total", TypeCode.String),
                this::getPageByFilter
        );

        this.registerRoute(
                "get", "/dummies/{dummy_id}",
                new ObjectSchema()
                        .withRequiredProperty("dummy_id", TypeCode.String),
                this::getOneById
        );

        this.registerRoute(
                "post", "/dummies",
                new ObjectSchema()
                        .withRequiredProperty("body", new DummySchema()),
                this::create
        );

        this.registerRoute(
                "put", "/dummies/{dummy_id}",
                new ObjectSchema()
                        .withRequiredProperty("body", new DummySchema()),
                this::update
        );

        this.registerRoute(
                "delete", "/dummies/{dummy_id}",
                new ObjectSchema()
                        .withRequiredProperty("dummy_id", TypeCode.String),
                this::deleteById
        );

        this._swaggerRoute = "/dummies/swagger";
        var dirname = Objects.requireNonNull(this.getClass().getClassLoader().getResource("")).getPath();
        this.registerOpenApiSpecFromFile(dirname + "./org/pipservices4/swagger/example/controllers/dummy.yml");
    }
}
