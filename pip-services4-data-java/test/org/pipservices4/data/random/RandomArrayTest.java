package org.pipservices4.data.random;

import static org.junit.Assert.*;

import java.util.*;

import org.junit.Test;

public class RandomArrayTest {
    @Test
    public void testPick() {
        Integer[] listEmpty = {};
        Integer value = RandomArray.pick(listEmpty);
        assertNull(value);

        Integer[] array = {1, 2};
        value = RandomArray.pick(array);
        assertTrue(value == 1 || value == 2);

        List<Integer> list = new ArrayList<Integer>();
        assertNull(RandomArray.pick(list));

        list.add(1);
        list.add(2);
        value = RandomArray.pick(array);
        assertTrue(value == 1 || value == 2);
    }

}