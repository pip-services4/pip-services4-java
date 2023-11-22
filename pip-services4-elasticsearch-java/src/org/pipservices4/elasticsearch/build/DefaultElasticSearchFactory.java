package org.pipservices4.elasticsearch.build;

import org.pipservices4.components.refer.Descriptor;
import org.pipservices4.components.build.Factory;
import org.pipservices4.elasticsearch.log.ElasticSearchLogger;

/**
 * Creates ElasticSearch components by their descriptors.
 *
 * @see org.pipservices4.elasticsearch.log.ElasticSearchLogger
 */
public class DefaultElasticSearchFactory extends Factory {
    private static final Descriptor ElasticSearchLoggerDescriptor = new Descriptor("pip-services", "logger", "elasticsearch", "*", "1.0");

    /**
     * Create a new instance of the factory.
     */
    public DefaultElasticSearchFactory() {
        super();
        this.registerAsType(DefaultElasticSearchFactory.ElasticSearchLoggerDescriptor, ElasticSearchLogger.class);
    }
}
