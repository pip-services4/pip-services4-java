package org.pipservices4.data.random;

import static org.junit.Assert.*;

import org.junit.Test;

public class RandomTextTest {

    @Test
    public void testPhrase() {
        assertSame("", RandomText.phrase(-1));
        assertSame("", RandomText.phrase(-1, -2));
        assertSame("", RandomText.phrase(-1, 0));
        assertSame("", RandomText.phrase(-2, -1));

        String text = RandomText.phrase(4);
        assertTrue(text.length() >= 4 && text.length() <= 10);
        String text1 = RandomText.phrase(4, 10);
        assertTrue(text1.length() >= 4);
    }

    @Test
    public void testFullName() {
        String text = RandomText.fullName();
        assertTrue(text.contains(" "));
    }

    @Test
    public void testPhone() {
        String text = RandomText.phone();
        assertTrue(text.contains("("));
        assertTrue(text.contains(")"));
        assertTrue(text.contains("-"));
    }

    @Test
    public void testEmail() {
        String text = RandomText.email();
        assertTrue(text.contains("@"));
        assertTrue(text.contains(".com"));
    }

}