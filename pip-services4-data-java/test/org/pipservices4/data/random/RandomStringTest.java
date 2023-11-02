package org.pipservices4.data.random;

import static org.junit.Assert.*;

import org.junit.Test;

public class RandomStringTest {
    private final String symbols = "_,.:-/.[].{},#-!,$=%.+^.&*-() ";
    private final String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private final String digits = "01234956789";

    @Test
    public void testPick() {
        assertEquals('\0', RandomString.pickChar(""));
        char charVariable = RandomString.pickChar(chars);
        assertTrue(chars.indexOf(charVariable) != -1);

        String[] valuesEmpty = {};
        assertSame("", RandomString.pick(valuesEmpty));

        String[] values = {"ab", "cd"};
        String result = RandomString.pick(values);
        assertTrue(result.equals("ab") || result.equals("cd"));
    }

    @Test
    public void testDistort() {
        String value = RandomString.distort("abc");
        assertTrue(value.length() == 3 || value.length() == 4);
        assertTrue(value.startsWith("Abc")
                || value.startsWith("abc")
        );

        if (value.length() == 4)
            assertTrue(symbols.contains(value.substring(3)));
    }

    @Test
    public void testNextAlphaChar() {
        assertTrue(chars.indexOf(RandomString.nextAlphaChar()) != -1);
    }

    @Test
    public void testNextString() {
        String value = RandomString.nextString(3, 5);
        assertTrue(value.length() <= 5 && value.length() >= 3);

        for (int i = 0; i < value.length(); i++) {
            assertTrue(chars.indexOf(value.charAt(i)) != -1
                    || symbols.indexOf(value.charAt(i)) != -1
                    || digits.indexOf(value.charAt(i)) != -1
            );
        }
    }

}