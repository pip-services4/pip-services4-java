package org.pipservices4.rpc.sample;

import java.util.*;

import org.pipservices4.components.context.ContextResolver;
import org.pipservices4.components.context.IContext;
import org.pipservices4.data.keys.IdGenerator;
import org.pipservices4.data.query.*;
import org.pipservices4.rpc.commands.CommandSet;
import org.pipservices4.rpc.commands.ICommandable;
import org.pipservices4.commons.errors.*;

public class DummyService implements IDummyService, ICommandable {
	private final Object _lock = new Object();
	private final List<Dummy> _entities = new ArrayList<Dummy>();

	private DummyCommandSet _commandSet;

	public DummyService() {}

	@Override
	public CommandSet getCommandSet() {
		if (_commandSet == null) {
			_commandSet = new DummyCommandSet(this);
		}

		return _commandSet;
	}

	@Override
	public DataPage<Dummy> getPageByFilter(IContext context, FilterParams filter, PagingParams paging)
		throws ApplicationException {
		
		filter = filter != null ? filter : new FilterParams();
		String key = filter.getAsNullableString("key");

		paging = paging != null ? paging : new PagingParams();
		long skip = paging.getSkip(0);
		long take = paging.getTake(100);

		List<Dummy> result = new ArrayList<Dummy>();
		
		synchronized (_lock) {
			for (Dummy entity : _entities) {
				if (key != null && !key.equals(entity.getKey()))
					continue;

				skip--;
				if (skip >= 0)
					continue;

				take--;
				if (take < 0)
					break;

				result.add(entity);
			}
		}
		return new DataPage<Dummy>(result);
	}

	@Override
	public Dummy getOneById(IContext context, String id) {
		
		synchronized (_lock) {
			for (Dummy entity : _entities) {
				if (entity.getId().equals(id))
					return entity;
			}
		}
		return null;
	}

	@Override
	public Dummy create(IContext context, Dummy entity) {
		
		synchronized (_lock) {
			if (entity.getId() == null)
				entity.setId(IdGenerator.nextLong());

			_entities.add(entity);
		}
		return entity;
	}

	@Override
	public Dummy update(IContext context, Dummy newEntity) {
		
		synchronized (_lock) {
			for (int index = 0; index < _entities.size(); index++) {
				Dummy entity = _entities.get(index);
				if (entity.getId().equals(newEntity.getId())) {
					_entities.set(index, newEntity);
					return newEntity;
				}
			}
		}
		return null;
	}

	@Override
	public Dummy deleteById(IContext context, String id) {
		
		synchronized (_lock) {
			for (int index = 0; index < _entities.size(); index++) {
				Dummy entity = _entities.get(index);
				if (entity.getId().equals(id)) {
					_entities.remove(index);
					return entity;
				}
			}
		}
		return null;
	}

	@Override
	public String checkTraceId(IContext context) {
		return context != null ? ContextResolver.getTraceId(context) : null;
	}

	@Override
	public void raiseException(IContext context)
		throws ApplicationException {

		throw new NotFoundException(context != null ? ContextResolver.getTraceId(context) : null, "TEST_ERROR", "Dummy error in service!");
	}

	@Override
	public boolean ping() throws ApplicationException {
		return true;
	}
}
