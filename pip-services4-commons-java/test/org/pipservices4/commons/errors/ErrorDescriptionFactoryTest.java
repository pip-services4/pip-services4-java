package org.pipservices4.commons.errors;

import static org.junit.Assert.*;

import org.junit.Test;

import java.io.PrintWriter;
import java.io.StringWriter;

public class ErrorDescriptionFactoryTest {

    @Test
    public void testCreateFromApplicationException() {
        String key = "key";
        String details = "details";

        ApplicationException ex = new ApplicationException("category", "traceId", "code", "message");

        ex.setStatus(777);
        ex.setCauseString("cause");
        ex.setStackTraceString("stackTrace");
        ex.withDetails(key, details);

        ErrorDescription descr = ErrorDescriptionFactory.create(ex);

        assertNotNull(descr);
        assertEquals(ex.getCategory(), descr.getCategory());
        assertEquals(ex.getTraceId(), descr.getTraceId());
        assertEquals(ex.getCode(), descr.getCode());
        assertEquals(ex.getMessage(), descr.getMessage());
        assertEquals(ex.getStatus(), descr.getStatus());
        assertEquals(ex.getCauseString(), descr.getCause());
        assertEquals(ex.getStackTraceString(), descr.getStackTrace());
        assertEquals(ex.getDetails(), descr.getDetails());
    }

    @Test
    public void testCreateFromError() {
        Exception ex = new Exception("message");

        ErrorDescription descr = ErrorDescriptionFactory.create(ex);

        assertNotNull(descr);
        assertEquals(ErrorCategory.Unknown, descr.getCategory());
        assertEquals("UNKNOWN", descr.getCode());
        assertEquals(ex.getMessage(), descr.getMessage());
        assertEquals(500, descr.getStatus());

        StringWriter sw = new StringWriter();
        ex.printStackTrace(new PrintWriter(sw));
        String exceptionAsString = sw.toString();

        assertEquals(exceptionAsString, descr.getStackTrace());

    }
}
