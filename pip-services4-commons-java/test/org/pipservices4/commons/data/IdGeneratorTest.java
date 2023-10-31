package org.pipservices4.commons.data;

import static org.junit.Assert.*;

import org.junit.Test;

public class IdGeneratorTest {
	
	@Test
	public void testShortId() {
		String id1 = IdGenerator.nextShort();
		assertNotNull(id1);
		assertTrue(id1.length() >= 9);
		
		String id2 = IdGenerator.nextShort();
		assertNotNull(id2);
		assertTrue(id2.length() >= 9);
		assertNotEquals(id1, id2);
	}

	@Test
	public void testLongId() {
		String id1 = IdGenerator.nextLong();
		assertNotNull(id1);
		assertTrue(id1.length() >= 32);

		String id2 = IdGenerator.nextLong();
		assertNotNull(id2);
		assertTrue(id2.length() >= 32);
		assertNotEquals(id1, id2);
	}

}
