package org.pipservices4.mongodb.fixtures;

import java.util.*;

import org.pipservices4.commons.data.AnyValueMap;
import org.pipservices4.components.context.IContext;
import org.pipservices4.data.query.*;
import org.pipservices4.commons.errors.*;

public interface IDummyPersistence {
    DataPage<Dummy> getPageByFilter(IContext context, FilterParams filterDefinition, PagingParams paging) throws ApplicationException;
    List<Dummy> getListByIds(IContext context, String[] ids) throws ApplicationException;
    long getCountByFilter(IContext context, FilterParams filter);
    Dummy getOneById(IContext context, String id) throws ApplicationException;
	Dummy create(IContext context, Dummy item);
    Dummy update(IContext context, Dummy item) throws ApplicationException;
    Dummy updatePartially(IContext context, String id, AnyValueMap update) throws ApplicationException;
    Dummy deleteById(IContext context, String id) throws ApplicationException;
    void deleteByIds(IContext context, String[] ids) throws ApplicationException;
}
