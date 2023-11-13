package org.pipservices4.persistence.persistence;

import org.junit.*;
import org.pipservices4.components.config.ConfigParams;
import org.pipservices4.commons.errors.*;

public class DummyMemoryPersistenceTest {
    private static DummyMemoryPersistence db;
    private static DummyPersistenceFixture fixture;

	@Before
	public void setUpBeforeClass() throws ConfigException {
        db = new DummyMemoryPersistence();
		db.configure(new ConfigParams());

        fixture = new DummyPersistenceFixture(db);
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
