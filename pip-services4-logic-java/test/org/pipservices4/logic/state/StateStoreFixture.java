package org.pipservices4.logic.state;

import java.util.List;

import static org.junit.Assert.*;

public class StateStoreFixture {
    private final String KEY1 = "key1";
    private final String KEY2 = "key2";
    private final String VALUE1 = "value1";
    private final String VALUE2 = "value2";
    private IStateStore _state = null;

    public StateStoreFixture(IStateStore state) {
        _state = state;
    }

    public void testSaveAndLoad() {
        this._state.save(null, KEY1, VALUE1);
        this._state.save(null, KEY2, VALUE2);

        String val = this._state.load(null, KEY1);
        assertNotNull(val);
        assertEquals(VALUE1, val);

        List<StateValue<String>> values = this._state.loadBulk(null, List.of(KEY2));
        assertEquals(1, values.size());
        assertEquals(KEY2, values.get(0).key);
        assertEquals(VALUE2, values.get(0).value);
    }

    public void testDelete() {
        this._state.save(null, KEY1, VALUE1);

        this._state.delete(null, KEY1);

        String val = this._state.load(null, KEY1);
        assertNull(val);
    }
}
