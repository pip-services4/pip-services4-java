package org.pipservices4.commons.errors;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class ApplicationExceptionTest {

	private ApplicationException _appEx;
	private Exception _ex;

	public final String CATEGORY = "category";
	public final String CORRELATION_ID = "correlationId";
	public final String CODE = "code";
	public final String MESSAGE = "message";
	@Before
	public void setException() {
		_ex = new Exception("Cause exception");
		_appEx = new ApplicationException(CATEGORY, CORRELATION_ID, CODE, MESSAGE);
	}

	@Test
	public void testWithCause() {
		_appEx.withCause(_ex);

		assertEquals(_ex.getMessage(), _appEx.getCauseString());
	}

	@Test
	public void testCheckParameters() {
		assertEquals(CATEGORY, _appEx.getCategory());
		assertEquals(CORRELATION_ID, _appEx.getCorrelationId());
		assertEquals(CODE, _appEx.getCode());
		assertEquals(MESSAGE, _appEx.getMessage());
	}

	@Test
	public void testWithCode() {
		String newCode = "newCode";
		ApplicationException appEx = _appEx.withCode(newCode);

		assertEquals(_appEx, appEx);
		assertEquals(newCode, appEx.getCode());
	}

	@Test
	public void testWithCorrelationId() {
		String newCode = "newCode";
		ApplicationException appEx = _appEx.withCode(newCode);

		assertEquals(_appEx, appEx);
		assertEquals(newCode, appEx.getCode());
	}

	@Test
	public void testWithStatus() {
		String key = "key";
		Object obj = new Object();

		ApplicationException appEx = _appEx.withDetails(key, obj);
		Object newObj = appEx.getDetails().getAsObject(key); // todo check

		assertNotNull(newObj);
		assertEquals(_appEx, appEx);
	}

	@Test
	public void testWithStackTrace() {
		String newTrace = "newTrace";
		ApplicationException appEx = _appEx.withStackTrace(newTrace);

		assertEquals(_appEx, appEx);
		assertEquals(newTrace, appEx.getStackTraceString());
	}
	@Test
	public void test() {
		ApplicationException error = new ApplicationException(null, null, null, "Test error")
			.withCode("TEST_ERROR");
		
		assertEquals("TEST_ERROR", error.getCode());
		assertEquals("Test error", error.getMessage());
		
		error = new ApplicationException();

		assertEquals("UNKNOWN", error.getCode());
		assertEquals("Unknown error", error.getMessage());
	}

}
