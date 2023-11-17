package org.pipservices4.http.controllers;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Response;
import org.pipservices4.components.config.ConfigParams;
import org.pipservices4.components.config.IConfigurable;
import org.pipservices4.data.query.*;
import org.pipservices4.commons.errors.*;
import org.pipservices4.components.refer.DependencyResolver;
import org.pipservices4.components.refer.IReferenceable;
import org.pipservices4.components.refer.IReferences;
import org.pipservices4.components.refer.ReferenceException;
import org.pipservices4.observability.count.CompositeCounters;
import org.pipservices4.observability.log.CompositeLogger;

import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public abstract class RestOperations implements IConfigurable, IReferenceable {
    protected CompositeLogger _logger = new CompositeLogger();
    protected CompositeCounters _counters = new CompositeCounters();
    protected DependencyResolver _dependencyResolver = new DependencyResolver();

    @Override
    public void configure(ConfigParams config) throws ConfigException {
        this._dependencyResolver.configure(config);
    }

    @Override
    public void setReferences(IReferences references) throws ReferenceException, ConfigException {
        this._logger.setReferences(references);
        this._counters.setReferences(references);
        this._dependencyResolver.setReferences(references);
    }

    /**
     * Returns traceId from request
     *
     * @param req -  http request
     * @return Returns traceId from request
     */
    protected String getTraceId(ContainerRequestContext req) {
        var traceId = getQueryParameter(req, "trace_id");
        if (traceId == null || traceId.isEmpty()) {
            traceId = req.getHeaderString("trace_id") != null && !req.getHeaderString("trace_id").isEmpty() ?
                    req.getHeaderString("trace_id") :
                    req.getHeaderString("correlation_id");
        }
        return traceId;
    }

    protected String getQueryParameter(ContainerRequestContext request, String name) {
        String value = null;
        name = URLEncoder.encode(name, StandardCharsets.UTF_8);
        if (request.getUriInfo().getQueryParameters().containsKey(name)) {
            value = request.getUriInfo().getQueryParameters().getFirst(name);
            value = value != null ? URLDecoder.decode(value, StandardCharsets.UTF_8) : null;
        }

        return value;
    }

    protected FilterParams getFilterParams(ContainerRequestContext req) {
        var value = new HashMap<>(req.getUriInfo().getQueryParameters());
        value.remove("skip");
        value.remove("take");
        value.remove("total");
        value.remove("trace_id");

        return FilterParams.fromValue(value);
    }

    protected PagingParams getPagingParams(ContainerRequestContext req) {
        var params = req.getUriInfo().getQueryParameters();
        var value = Map.of(
                "skip", params.getFirst("skip"),
                "take", params.getFirst("take"),
                "total", params.getFirst("total")
        );

        return PagingParams.fromValue(value);
    }

    protected Response sendResult(Object result) {
        return HttpResponseSender.sendResult(result);
    }

    protected Response sendEmptyResult() {
        return HttpResponseSender.sendEmptyResult();
    }

    protected Response sendCreatedResult(Object result) {
        return HttpResponseSender.sendCreatedResult(result);
    }

    protected Response sendDeletedResult(Object result) {
        return HttpResponseSender.sendDeletedResult(result);
    }

    protected Response sendError(Exception error) {
        return HttpResponseSender.sendError(error);
    }

    protected Response sendBadRequest(ContainerRequestContext req, String message) {
        var traceId = this.getTraceId(req);
        var error = new BadRequestException(traceId, "BAD_REQUEST", message);
        return this.sendError(error);
    }

    protected Response sendUnauthorized(ContainerRequestContext req, String message) {
        var traceId = this.getTraceId(req);
        var error = new UnauthorizedException(traceId, "UNAUTHORIZED", message);
        return this.sendError(error);
    }

    protected Response sendNotFound(ContainerRequestContext req, String message) {
        var traceId = this.getTraceId(req);
        var error = new NotFoundException(traceId, "NOT_FOUND", message);
        return this.sendError(error);
    }

    protected Response sendConflict(ContainerRequestContext req, String message) {
        var traceId = this.getTraceId(req);
        var error = new ConflictException(traceId, "CONFLICT", message);
        return this.sendError(error);
    }

    protected Response sendSessionExpired(ContainerRequestContext req, String message) {
        var traceId = this.getTraceId(req);
        var error = new UnknownException(traceId, "SESSION_EXPIRED", message);
        error.setStatus(440);
        return this.sendError(error);
    }

    protected Response sendInternalError(ContainerRequestContext req, String message) {
        var traceId = this.getTraceId(req);
        var error = new UnknownException(traceId, "INTERNAL", message);
        return this.sendError(error);
    }

    protected Response sendServerUnavailable(ContainerRequestContext req, String message) {
        var traceId = this.getTraceId(req);
        var error = new ConflictException(traceId, "SERVER_UNAVAILABLE", message);
        error.setStatus(503);
        return this.sendError(error);
    }

    public Method invoke(String operation) {
        Method func = null;
        Method[] methods = this.getClass().getMethods();
        for (var method : methods) {
            if (method.getName().equals(operation)) {
                func = method;
                break;
            }
        }

        return func;
    }
}
