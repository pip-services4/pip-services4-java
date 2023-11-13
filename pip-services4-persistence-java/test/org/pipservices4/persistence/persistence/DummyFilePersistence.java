package org.pipservices4.persistence.persistence;

import org.pipservices4.components.config.*;
import org.pipservices4.commons.errors.*;
import org.pipservices4.persistence.persistence.JsonFilePersister;
import org.pipservices4.persistence.sample.Dummy;


public class DummyFilePersistence extends DummyMemoryPersistence {
	protected JsonFilePersister<Dummy> _persister;

	public DummyFilePersistence() {
		super();
	}
	
    public DummyFilePersistence(String path) {
    	super();
    	
    	_persister = new JsonFilePersister<Dummy>(Dummy.class, path);
    	_loader = _persister;
    	_saver = _persister;
    }
    @Override
    public void configure(ConfigParams config) throws ConfigException {
        super.configure(config);
    	_persister.configure(config);
    }
}
