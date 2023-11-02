package org.pipservices4.components.exec;

public class TestClass {
    public TestClass(Object value1, Object value2) {
        this.value1 = value1;
        this.setValue2(value2);
    }

    public Object value1;
    private Object _value2;

    public Object getValue2() {
        return _value2;
    }

    public void setValue2(Object value) {
        _value2 = value;
    }
}