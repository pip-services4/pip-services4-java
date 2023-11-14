package org.pipservices4.container.example;

import org.pipservices4.components.config.ConfigParams;
import org.pipservices4.components.config.IReconfigurable;
import org.pipservices4.commons.errors.ApplicationException;
import org.pipservices4.commons.errors.ConfigException;
import org.pipservices4.components.context.IContext;
import org.pipservices4.components.exec.*;
import org.pipservices4.components.refer.IReferenceable;
import org.pipservices4.components.refer.IReferences;
import org.pipservices4.components.refer.ReferenceException;
import org.pipservices4.components.run.*;
import org.pipservices4.observability.log.CompositeLogger;

public class DummyController implements  IReferenceable, IReconfigurable, IOpenable, INotifiable {
	private final FixedRateTimer _timer;
    private final CompositeLogger _logger = new CompositeLogger();
    private String _message = "Hello World!";
	private long _counter = 0;
    	
	public DummyController() {
        _timer = new FixedRateTimer(
            (String, Parameters) -> { notify(null, new Parameters()); }, 
            1000, 1000
        );
    }
	
	@Override
	public void configure(ConfigParams config) throws ConfigException {
		_message = config.getAsStringWithDefault("message", _message);		
	}

	@Override
	public void setReferences(IReferences references) throws ReferenceException, ConfigException {
		_logger.setReferences(references);
	}
	
	@Override
	public void notify(IContext context, Parameters args) throws ApplicationException {
		 _logger.info(context, "%s - %s", _counter++, _message);
	}

	@Override
	public boolean isOpen() {
		return _timer.isStarted();
	}

	@Override
	public void open(IContext context) throws ApplicationException {
		_timer.start();
        _logger.trace(context, "Dummy controller opened");		
	}
	
	@Override
	public void close(IContext context) throws ApplicationException {
		 _timer.stop();
         _logger.trace(context, "Dummy controller closed");		
	}

}