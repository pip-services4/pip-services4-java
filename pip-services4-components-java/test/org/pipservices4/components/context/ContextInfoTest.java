package org.pipservices4.components.context;

import static org.junit.Assert.*;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.Objects;

import org.junit.Before;
import org.junit.Test;
import org.pipservices4.components.config.ConfigParams;

public final class ContextInfoTest {
    ContextInfo contextInfo;

    @Before
    public void setup() {
        contextInfo = new ContextInfo();
    }

    @Test
    public void testName() {
        assertEquals(contextInfo.getName(), "unknown");

        String update = "new name";
        contextInfo.setName(update);

        assertEquals(contextInfo.getName(), update);
    }

    @Test
    public void testContext() {
        var ctx = Context.fromTraceId("my_trace_id");
        assert Objects.equals(ContextResolver.getTraceId(ctx), "my_trace_id");

        ctx = new Context(Map.of("trace_id", "my_map_trace_id"));
        assert Objects.equals(ContextResolver.getTraceId(ctx), "my_map_trace_id");

        ctx = new Context(Map.of("trace_id", new Object(), "client", new Object(), "user", new Object()));
        assert ContextResolver.getTraceId(ctx) == null;
        assert ContextResolver.getClient(ctx) == null;
        assert ContextResolver.getUser(ctx) == null;

        assert ContextResolver.getTraceId(null) == null;
        assert ContextResolver.getClient(null) == null;
        assert ContextResolver.getUser(null) == null;
    }

    @Test
    public void testDescription() {
        assertNull(contextInfo.getDescription());

        String update = "new description";
        contextInfo.setDescription(update);
        assertEquals(contextInfo.getDescription(), update);
    }

    @Test
    public void TestContextId() {
        assertNotNull(contextInfo.getContextId());

        String update = "new context id";
        contextInfo.setContextId(update);

        assertEquals(update, contextInfo.getContextId());
    }

    @Test
    public void TestStartTime() {
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("UTC"));

        assertEquals(contextInfo.getStartTime().getYear(), now.getYear());
        assertEquals(contextInfo.getStartTime().getMonth(), now.getMonth());

        contextInfo.setStartTime(ZonedDateTime.of(1975, 4, 8, 0, 0, 0, 0, ZoneId.of("UTC")));

        assertEquals(contextInfo.getStartTime().getYear(), 1975);
        assertEquals(contextInfo.getStartTime().getMonthValue(), 4);
        assertEquals(contextInfo.getStartTime().getDayOfMonth(), 8);
    }

    @Test
    public void TestFromConfigs() {
        ConfigParams config = ConfigParams.fromTuples(
                "name", "new name",
                "description", "new description",
                "properties.access_key", "key",
                "properties.store_key", "store key"
        );

        ContextInfo contextInfo = ContextInfo.fromConfig(config);
        assertEquals(contextInfo.getName(), "new name");
        assertEquals(contextInfo.getDescription(), "new description");
    }
}
