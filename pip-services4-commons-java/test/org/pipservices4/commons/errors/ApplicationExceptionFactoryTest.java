package org.pipservices4.commons.errors;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.pipservices4.commons.data.StringValueMap;

public class ApplicationExceptionFactoryTest {
    private ErrorDescription _descr;

    public void checkProperties(ApplicationException ex) {
        assertNotNull(ex);

        assertEquals(_descr.getCause(), ex.getCauseString());
        assertEquals(_descr.getStackTrace(), ex.getStackTraceString());
        assertEquals(_descr.getDetails(), ex.getDetails());
        assertEquals(_descr.getCategory(), ex.getCategory());
    }

    @Before
    public void setupDescription() {
        _descr = new ErrorDescription();
        _descr.setCorrelationId("correlationId");
        _descr.setCode("code");
        _descr.setMessage("message");
        _descr.setStatus(777);
        _descr.setCause("cause");
        _descr.setStackTrace("stackTrace");

        StringValueMap map = new StringValueMap();
        map.put("key", "value");

        _descr.setDetails(map);
    }

    @Test
    public void testCreateFromUnknown() {
        _descr.setCategory(ErrorCategory.Unknown);

        ApplicationException ex = ApplicationExceptionFactory.create(_descr);

        checkProperties(ex);

        assertTrue(ex instanceof UnknownException);
    }

    @Test
    public void testCreateFromInternal() {
        _descr.setCategory(ErrorCategory.Internal);

        ApplicationException ex = ApplicationExceptionFactory.create(_descr);

        checkProperties(ex);

        assertTrue(ex instanceof InternalException);
    }

    @Test
    public void testCreateFromMisconfiguration() {
        _descr.setCategory(ErrorCategory.Misconfiguration);

        ApplicationException ex = ApplicationExceptionFactory.create(_descr);

        checkProperties(ex);

        assertTrue(ex instanceof ConfigException);
    }

    @Test
    public void testCreateFromNoResponse() {
        _descr.setCategory(ErrorCategory.NoResponse);

        ApplicationException ex = ApplicationExceptionFactory.create(_descr);

        checkProperties(ex);

        assertTrue(ex instanceof ConnectionException);
    }

    @Test
    public void testCreateFromFailedInvocation() {
        _descr.setCategory(ErrorCategory.FailedInvocation);

        ApplicationException ex = ApplicationExceptionFactory.create(_descr);

        checkProperties(ex);

        assertTrue(ex instanceof InvocationException);
    }

    @Test
    public void testCreateFromNoFileAccess() {
        _descr.setCategory(ErrorCategory.FileError);

        ApplicationException ex = ApplicationExceptionFactory.create(_descr);

        checkProperties(ex);

        assertTrue(ex instanceof FileException);
    }

    @Test
    public void testCreateFromBadRequest() {
        _descr.setCategory(ErrorCategory.BadRequest);

        ApplicationException ex = ApplicationExceptionFactory.create(_descr);

        checkProperties(ex);

        assertTrue(ex instanceof BadRequestException);
    }

    @Test
    public void testCreateFromUnauthorized() {
        _descr.setCategory(ErrorCategory.Unauthorized);

        ApplicationException ex = ApplicationExceptionFactory.create(_descr);

        checkProperties(ex);

        assertTrue(ex instanceof UnauthorizedException);
    }

    @Test
    public void testCreateFromConflict() {
        _descr.setCategory(ErrorCategory.Conflict);

        ApplicationException ex = ApplicationExceptionFactory.create(_descr);

        checkProperties(ex);

        assertTrue(ex instanceof ConflictException);
    }

    @Test
    public void testCreateFromNotFound() {
        _descr.setCategory(ErrorCategory.NotFound);

        ApplicationException ex = ApplicationExceptionFactory.create(_descr);

        checkProperties(ex);

        assertTrue(ex instanceof NotFoundException);
    }

    @Test
    public void testCreateFromDefault() {
        _descr.setCategory("any_other");

        ApplicationException ex = ApplicationExceptionFactory.create(_descr);

        checkProperties(ex);

        assertTrue(ex instanceof UnknownException);
    }
}
