package org.pipservices4.commons.data;

import static org.junit.Assert.*;

import org.junit.Test;

import java.util.List;

public final class ProjectionParamsTest {

	@Test
	public void createProjectionParamsFromNullObject() {
		ProjectionParams parameters = ProjectionParams.fromValue(null);
		assertTrue(parameters.isEmpty());
	}

	@Test
	public void convertParametersFromValues() {

		ProjectionParams parameters = ProjectionParams.fromValues("field1", "field2", "field3");

		assertEquals(3, parameters.size());
		assertEquals("field1", parameters.get(0));
		assertEquals("field2", parameters.get(1));
		assertEquals("field3", parameters.get(2));
	}

	@Test
	public void createProjectionParamsFromObject() {
		ProjectionParams parameters = ProjectionParams
				.fromValue(new String[] {"field1", "field2", "field3"});

		assertEquals(3, parameters.size());
		assertEquals("field1", parameters.get(0));
		assertEquals("field2", parameters.get(1));
		assertEquals("field3", parameters.get(2));
	}

	@Test
	public void testConvertToString() {
		ProjectionParams parameters = ProjectionParams.fromValue(List.of("field1", "field2", "field3"));

		assertNotNull(parameters.toString());
		assertEquals(parameters.toString(), "field1,field2,field3");
	}

	@Test
	public void convertParametersFromValuesAsOneString() {
		ProjectionParams parameters = ProjectionParams.fromValues("field1,field2, field3");

		assertEquals(3, parameters.size());
		assertEquals("field1", parameters.get(0));
		assertEquals("field2", parameters.get(1));
		assertEquals("field3", parameters.get(2));
	}

	@Test
	public void convertParametersFromGroupedValues() {
		ProjectionParams parameters = ProjectionParams.fromValues("object1(field1)", "object2(field1, field2)",
				"field3");

		assertEquals(4, parameters.size());
		assertEquals("object1.field1", parameters.get(0));
		assertEquals("object2.field1", parameters.get(1));
		assertEquals("object2.field2", parameters.get(2));
		assertEquals("field3", parameters.get(3));
	}

	@Test
	public void convertParametersFromGroupedValuesAsOneString() {
		ProjectionParams parameters = ProjectionParams.fromValues("object1(object2(field1,field2,object3(field1)))");

		assertEquals(3, parameters.size());
		assertEquals("object1.object2.field1", parameters.get(0));
		assertEquals("object1.object2.field2", parameters.get(1));
		assertEquals("object1.object2.object3.field1", parameters.get(2));
	}

	@Test
	public void convertParametersFromMultipleGroupedValues1() {
		ProjectionParams parameters = ProjectionParams
				.fromValues("object1(field1, object2(field1, field2, field3, field4), field3)", "field2");

		assertEquals(7, parameters.size());
		assertEquals("object1.field1", parameters.get(0));
		assertEquals("object1.object2.field1", parameters.get(1));
		assertEquals("object1.object2.field2", parameters.get(2));
		assertEquals("object1.object2.field3", parameters.get(3));
		assertEquals("object1.object2.field4", parameters.get(4));
		assertEquals("object1.field3", parameters.get(5));
		assertEquals("field2", parameters.get(6));
	}

	@Test
	public void convertParametersFromMultipleGroupedValues2() {
		ProjectionParams parameters = ProjectionParams.fromValues("object1(field1, object2(field1), field3)", "field2");

		assertEquals(4, parameters.size());
		assertEquals("object1.field1", parameters.get(0));
		assertEquals("object1.object2.field1", parameters.get(1));
		assertEquals("object1.field3", parameters.get(2));
		assertEquals("field2", parameters.get(3));
	}

	@Test
	public void convertParametersFromMultipleGroupedValues3() {
		ProjectionParams parameters = ProjectionParams
				.fromValues("object1(field1, object2(field1, field2, object3(field1), field4), field3)", "field2");

		assertEquals(7, parameters.size());
		assertEquals("object1.field1", parameters.get(0));
		assertEquals("object1.object2.field1", parameters.get(1));
		assertEquals("object1.object2.field2", parameters.get(2));
		assertEquals("object1.object2.object3.field1", parameters.get(3));
		assertEquals("object1.object2.field4", parameters.get(4));
		assertEquals("object1.field3", parameters.get(5));
		assertEquals("field2", parameters.get(6));
	}

	@Test
	public void convertParametersFromMultipleGroupedValues4() {
		ProjectionParams parameters = ProjectionParams.fromValues("object1(object2(object3(field1)), field2)",
				"field2");

		assertEquals(3, parameters.size());
		assertEquals("object1.object2.object3.field1", parameters.get(0));
		assertEquals("object1.field2", parameters.get(1));
		assertEquals("field2", parameters.get(2));
	}

	@Test
	public void convertParametersFromMultipleGroupedValues5() {
		ProjectionParams parameters = ProjectionParams
				.fromValues("field1,object1(field1),object2(field1,field2),object3(field1),field2,field3");

		assertEquals(7, parameters.size());
		assertEquals("field1", parameters.get(0));
		assertEquals("object1.field1", parameters.get(1));
		assertEquals("object2.field1", parameters.get(2));
		assertEquals("object2.field2", parameters.get(3));
		assertEquals("object3.field1", parameters.get(4));
		assertEquals("field2", parameters.get(5));
		assertEquals("field3", parameters.get(6));
	}

}
