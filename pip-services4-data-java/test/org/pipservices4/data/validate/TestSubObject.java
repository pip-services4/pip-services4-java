package org.pipservices4.data.validate;

public class TestSubObject {
	private String _id;
	private Object _nullProperty = null;
	
    public TestSubObject(String id) {
        _id = id;
    }

    public String getId() { return _id; }
    public void setId(String value) { _id = value; }
    
    public float floatField = 432;
    
    public Object getNullProperty() { return _nullProperty; }
    public void setNullProperty(Object value) { _nullProperty = value; }
}
