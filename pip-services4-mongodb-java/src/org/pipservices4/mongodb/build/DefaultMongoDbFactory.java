package org.pipservices4.mongodb.build;

import org.pipservices4.components.refer.Descriptor;
import org.pipservices4.components.build.Factory;
import org.pipservices4.mongodb.connect.MongoDbConnection;

/**
 * Creates MongoDb components by their descriptors.
 *
 * @see Factory
 * @see org.pipservices4.mongodb.connect.MongoDbConnection
 */
public class DefaultMongoDbFactory extends Factory {
    private static final Descriptor MongoDbConnectionDescriptor = new Descriptor("pip-services", "connection", "mongodb", "*", "1.0");

    /**
     * Create a new instance of the factory.
     */
    public DefaultMongoDbFactory() {
        this.registerAsType(DefaultMongoDbFactory.MongoDbConnectionDescriptor, MongoDbConnection.class);
    }
}
