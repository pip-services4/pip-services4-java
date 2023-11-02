package org.pipservices4.data.query;

import static org.junit.Assert.*;
import org.junit.Test;
import java.util.List;

public class SortParamsTest {

    @Test
    public void testCreateAndPush() {
        SortParams sort = new SortParams(List.of(new SortField("f1"), new SortField("f2")));
        sort.add(new SortField("f3", false));
        assertEquals(3, sort.size());
    }
}
