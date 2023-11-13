package org.pipservices4.persistence.persistence;

import org.pipservices4.commons.errors.ApplicationException;
import org.pipservices4.components.context.IContext;
import org.pipservices4.data.query.DataPage;
import org.pipservices4.data.query.FilterParams;
import org.pipservices4.data.query.PagingParams;
import org.pipservices4.persistence.read.IGetter;
import org.pipservices4.persistence.sample.Dummy;
import org.pipservices4.persistence.write.IPartialUpdater;
import org.pipservices4.persistence.write.IWriter;

import java.util.Comparator;
import java.util.List;

public interface IDummyPersistence extends IGetter<Dummy, String>, IWriter<Dummy, String>, IPartialUpdater<Dummy, String> {
	
    DataPage<Dummy> getPageByFilter(IContext context, FilterParams filter, PagingParams paging) throws ApplicationException;
    int getCountByFilter(IContext context, FilterParams filter);
    DataPage<Dummy> getSortedPage(IContext context, Comparator<Dummy> sort);
    List<Dummy> getSortedList(IContext context, Comparator<Dummy> sort) throws ApplicationException;
    Dummy getOneById(IContext context, String dummyId) throws ApplicationException;
    List<Dummy> getListByIds(IContext context, String[] ids) throws ApplicationException;
    List<Dummy> getListByIds(IContext context, List<String> ids) throws ApplicationException;
    Dummy create(IContext context, Dummy dummy) throws ApplicationException;
    Dummy update(IContext context, Dummy dummy) throws ApplicationException;

    Dummy deleteById(IContext context, String dummyId) throws ApplicationException;
    void deleteByIds(IContext context, String[] ids) throws ApplicationException;
}
