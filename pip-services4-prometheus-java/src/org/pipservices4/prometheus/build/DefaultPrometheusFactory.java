package org.pipservices4.prometheus.build;

import org.pipservices4.components.refer.Descriptor;
import org.pipservices4.components.build.Factory;
import org.pipservices4.prometheus.count.PrometheusCounters;
import org.pipservices4.prometheus.controllers.PrometheusMetricsController;

/**
 * Creates Prometheus components by their descriptors.
 *
 * @see Factory
 * @see org.pipservices4.prometheus.count.PrometheusCounters
 * @see org.pipservices4.prometheus.controllers.PrometheusMetricsController
 */
public class DefaultPrometheusFactory extends Factory {
    private static final Descriptor PrometheusCountersDescriptor = new Descriptor("pip-services", "counters", "prometheus", "*", "1.0");
    private static final Descriptor PrometheusMetricsControllerDescriptor = new Descriptor("pip-services", "metrics-controller", "prometheus", "*", "1.0");

    /**
     * Create a new instance of the factory.
     */
    public DefaultPrometheusFactory() {
        super();
        this.registerAsType(DefaultPrometheusFactory.PrometheusCountersDescriptor, PrometheusCounters.class);
        this.registerAsType(DefaultPrometheusFactory.PrometheusMetricsControllerDescriptor, PrometheusMetricsController.class);
    }
}
