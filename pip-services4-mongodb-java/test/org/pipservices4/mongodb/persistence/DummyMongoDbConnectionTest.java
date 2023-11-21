package org.pipservices4.mongodb.persistence;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.pipservices4.components.config.ConfigParams;
import org.pipservices4.commons.errors.ApplicationException;
import org.pipservices4.components.refer.Descriptor;
import org.pipservices4.components.refer.References;
import org.pipservices4.mongodb.connect.MongoDbConnection;
import org.pipservices4.mongodb.fixtures.DummyPersistenceFixture;

public class DummyMongoDbConnectionTest {
    MongoDbConnection connection;
    DummyMongoDbPersistence persistence;
    DummyPersistenceFixture fixture;

    String mongoUri = System.getenv("MONGO_SERVICE_URI") != null ? System.getenv("MONGO_SERVICE_URI") : null;
    String mongoHost = System.getenv("MONGO_SERVICE_HOST") != null ? System.getenv("MONGO_SERVICE_HOST") : "localhost";
    int mongoPort = System.getenv("MONGO_SERVICE_PORT") != null ? Integer.valueOf(System.getenv("MONGO_SERVICE_PORT")) : 27017;
    String mongoDatabase = System.getenv("MONGO_DB") != null ? System.getenv("MONGO_DB") : "test";

    boolean enabled = false;

    public DummyMongoDbConnectionTest() {
        // Skip tests
        if (mongoUri != null || mongoHost != null)
            enabled = true;
    }

    @Before
    public void setup() throws ApplicationException {
        if (enabled) {
            var dbConfig = ConfigParams.fromTuples(
                    "connection.uri", mongoUri,
                    "connection.host", mongoHost,
                    "connection.port", mongoPort,
                    "connection.database", mongoDatabase
            );

            connection = new MongoDbConnection();
            connection.configure(dbConfig);

            persistence = new DummyMongoDbPersistence();
            persistence.setReferences(References.fromTuples(
                    new Descriptor("pip-services", "connection", "mongodb", "default", "1.0"), connection
            ));

            fixture = new DummyPersistenceFixture(persistence);

            connection.open(null);
            persistence.open(null);
            persistence.clear(null);
        }
    }

    @After
    public void teardown() throws ApplicationException {
        if (enabled) {
            connection.close(null);
            persistence.close(null);
        }
    }

    @Test
    public void testCrudOperations() throws ApplicationException {
        if (enabled)
            fixture.testCrudOperations();
    }

    @Test
    public void testBatchOperations() throws ApplicationException {
        if (enabled)
            fixture.testBatchOperations();
    }
}
