package org.pipservices4.rpc.clients;

import org.junit.*;
import org.pipservices4.commons.errors.ApplicationException;
import org.pipservices4.components.refer.Descriptor;
import org.pipservices4.components.refer.References;
import org.pipservices4.rpc.sample.DummyService;

public class DummyDirectClientTest {	
	private DummyService _service;
    private DummyDirectClient _client;
    private DummyClientFixture _fixture;
	
    @Before
    public void setUp() throws ApplicationException {
        _service = new DummyService();
        _client = new DummyDirectClient();

        References references = References.fromTuples(
            new Descriptor("pip-services-dummies", "service", "default", "default", "1.0"), _service
        );
        _client.setReferences(references);

        _fixture = new DummyClientFixture(_client);

        _client.open(null);
    }

    @After
    public void tearDown() {
    	_client.close(null);
    }    
    
    @Test
    public void testCrudOperations() throws ApplicationException {
    	_fixture.testCrudOperations();
	}
    
}
