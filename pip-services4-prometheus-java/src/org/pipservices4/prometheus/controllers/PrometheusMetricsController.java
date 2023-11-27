package org.pipservices4.prometheus.controllers;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.pipservices4.commons.errors.ConfigException;
import org.pipservices4.components.refer.Descriptor;
import org.pipservices4.components.refer.IReferences;
import org.pipservices4.components.refer.ReferenceException;
import org.pipservices4.observability.count.CachedCounters;
import org.pipservices4.components.context.ContextInfo;
import org.pipservices4.http.controllers.RestController;
import org.pipservices4.prometheus.count.PrometheusCounterConverter;
import org.pipservices4.prometheus.count.PrometheusCounters;

import java.util.Objects;

public class PrometheusMetricsController extends RestController {
    private CachedCounters _cachedCounters;
    private String _source;
    private String _instance;

    /**
     * Creates a new instance of this service.
     */
    public PrometheusMetricsController() {
        super();
        this._dependencyResolver.put("cached-counters", new Descriptor("pip-services", "counters", "cached", "*", "1.0"));
        this._dependencyResolver.put("prometheus-counters", new Descriptor("pip-services", "counters", "prometheus", "*", "1.0"));
    }

    /**
     * Sets references to dependent components.
     *
     * @param references references to locate the component dependencies.
     */
    public void setReferences(IReferences references) throws ReferenceException, ConfigException {
        super.setReferences(references);

        this._cachedCounters = this._dependencyResolver.getOneOptional(PrometheusCounters.class, "prometheus-counters");
        if (this._cachedCounters == null)
            this._cachedCounters = this._dependencyResolver.getOneOptional(CachedCounters.class, "cached-counters");

        var contextInfo = references.getOneOptional(ContextInfo.class,
                new Descriptor("pip-services", "context-info", "default", "*", "1.0"));

        if (contextInfo != null && (Objects.equals(this._source, "") || this._source == null))
            this._source = contextInfo.getName();

        if (contextInfo != null && (Objects.equals(this._instance, "") || this._instance == null))
            this._instance = contextInfo.getContextId();
    }

    /**
     * Registers all service routes in HTTP endpoint.
     */
    @Override
    public void register() {
        this.registerRoute("get", "metrics", null, this::metrics);
        this.registerRoute("get", "metricsandreset", null, this::metricsAndReset);
    }

    /**
     * Handles metrics requests
     *
     * @param req an HTTP request
     * @return res   an HTTP response
     */
    public Response metrics(ContainerRequestContext req) {
        var counters = this._cachedCounters != null ? this._cachedCounters.getAll() : null;
        var body = PrometheusCounterConverter.toString(counters, this._source, this._instance);
        return Response
                .status(Response.Status.OK)
                .type(MediaType.TEXT_PLAIN)
                .entity(body)
                .build();
    }

    /**
     * Handles metricsandreset requests.
     * The counters will be returned and then zeroed out.
     *
     * @param req an HTTP request
     * @return res an HTTP response
     */
    private Response metricsAndReset(ContainerRequestContext req) {
        var counters = this._cachedCounters != null ? this._cachedCounters.getAll() : null;
        var body = PrometheusCounterConverter.toString(counters, this._source, this._instance);

        if (this._cachedCounters != null)
            this._cachedCounters.clearAll();

        return Response
                .status(Response.Status.OK)
                .type(MediaType.TEXT_PLAIN)
                .entity(body)
                .build();
    }
}
