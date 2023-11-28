package org.pipservices4.swagger.example.services;

import org.pipservices4.components.context.IContext;
import org.pipservices4.data.keys.IdGenerator;
import org.pipservices4.swagger.example.data.Dummy;
import org.pipservices4.rpc.commands.CommandSet;
import org.pipservices4.rpc.commands.ICommandable;
import org.pipservices4.data.query.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DummyService implements IDummyService, ICommandable {
    private DummyCommandSet _commandSet;
    private final List<Dummy> _entities = new ArrayList<>();

    @Override
    public CommandSet getCommandSet() {
        if (this._commandSet == null)
            this._commandSet = new DummyCommandSet(this);
        return this._commandSet;
    }

    @Override
    public DataPage<Dummy> getPageByFilter(IContext context, FilterParams filter, PagingParams paging) {
        filter = filter != null ? filter : new FilterParams();
        String key = filter.getAsNullableString("key");

        paging = paging != null ? paging : new PagingParams();
        long skip = paging.getSkip(0);
        long take = paging.getTake(100);

        var result = new ArrayList<Dummy>();
        for (Dummy entity : this._entities) {
            if (key != null && !key.equals(entity.getKey()))
                continue;

            skip--;
            if (skip >= 0) continue;

            take--;
            if (take < 0) break;

            result.add(entity);
        }

        return new DataPage<>(result);
    }

    @Override
    public Dummy getOneById(IContext context, String id) {
        for (Dummy entity : this._entities) {
            if (Objects.equals(id, entity.getId())) {
                return entity;
            }
        }
        return null;
    }

    @Override
    public Dummy create(IContext context, Dummy entity) {
        if (entity.getId() == null) {
            entity.setId(IdGenerator.nextLong());
        }
        this._entities.add(entity);
        return entity;
    }

    @Override
    public Dummy update(IContext context, Dummy newEntity) {
        for (var index = 0; index < this._entities.size(); index++) {
            Dummy entity = this._entities.get(index);
            if (Objects.equals(entity.getId(), newEntity.getId())) {
                this._entities.set(index, newEntity);
                return newEntity;
            }
        }
        return null;
    }

    @Override
    public Dummy deleteById(IContext context, String id) {
        for (var index = 0; index < this._entities.size(); index++) {
            Dummy entity = this._entities.get(index);
            if (Objects.equals(entity.getId(), id)) {
                this._entities.remove(index);
                return entity;
            }
        }
        return null;
    }
}
