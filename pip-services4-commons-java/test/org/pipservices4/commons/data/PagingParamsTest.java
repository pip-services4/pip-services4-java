package org.pipservices4.commons.data;

import static org.junit.Assert.*;

import org.junit.Test;


import java.util.Optional;

public class PagingParamsTest {

    @Test
    public void testParametersCreation() {
        // Create empty PagingParams (regular)
        PagingParams paging = new PagingParams();

        assertNull(paging.getSkip());
        assertNull(paging.getTake());
        assertFalse(paging.hasTotal());

        // Create empty PagingParams (gRPC)
        paging = new PagingParams(0, 0, false);
        assertEquals(Optional.of(0L), Optional.of(paging.getSkip()));
        assertNull(paging.getTake());
        assertFalse(paging.hasTotal());

        // Create PagingParams with set skip/take values
        paging = new PagingParams(25, 50, false);
        assertEquals(Optional.of(25L), Optional.of(paging.getSkip()));
        assertEquals(Optional.of(50L), Optional.of(paging.getTake()));
        assertFalse(paging.hasTotal());

        // getSkip & getTake
        paging = new PagingParams(25, 50, false);
        assertEquals(50L, paging.getSkip(50));
        assertEquals(25L, paging.getSkip(10));
        assertEquals(50L, paging.getTake(100));
        assertEquals(25L, paging.getTake(25));

        paging = new PagingParams();
        assertEquals(10L, paging.getSkip(10));
        assertEquals(10L, paging.getTake(10));
    }
}
