package org.pipservices4.persistence.persistence;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.*;
import org.pipservices4.commons.errors.*;

public class DummyFilePersistenceTest {
    private static DummyFilePersistence db;
    private static DummyPersistenceFixture fixture;

    @Before
    public void setUpBeforeClass() throws Exception {
        db = new DummyFilePersistence("./data/dummies.json");
        fixture = new DummyPersistenceFixture(db);
        db.open(null);
        db.clear(null);
    }

    @Test
    public void testCrudOperations() throws ApplicationException {
        fixture.testCrudOperations();
    }

    @Test
    public void testBatchOperations() throws ApplicationException {
        fixture.testBatchOperations();
    }

    @Test
    public void testPageSortingOperations() throws ApplicationException {
        fixture.testPageSortingOperations();
    }

    @Test
    public void testListSortingOperations() throws ApplicationException {
        fixture.testListSortingOperations();
    }
}
