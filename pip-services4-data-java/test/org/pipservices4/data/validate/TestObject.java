package org.pipservices4.data.validate;

import java.util.*;

public class TestObject {
    @SuppressWarnings("unused")
	private final float privateField = 124;
	private String _privateProperty = "XYZ";
	private String _stringProperty = "ABC";
	private Object _nullProperty = null;
	private int[] _intArrayProperty = new int[] { 1, 2, 3 };
	private List<String> _stringListProperty = Arrays.asList(new String[] { "AAA", "BBB" });
	private Map<String, Integer> _mapProperty = new HashMap<>();
	private TestSubObject _subObjectProperty = new TestSubObject("1");
    private TestSubObject[] _subArrayProperty = new TestSubObject[] { new TestSubObject("2"), new TestSubObject("3") };
	
    public TestObject() { 
    	_mapProperty.put("Key1", 111);
    	_mapProperty.put("Key2", 222);
    }

    @SuppressWarnings("unused")
	private String getPrivateProperty() { return _privateProperty; }
    @SuppressWarnings("unused")
	private void setPrivateProperty(String value) { _privateProperty = value; }

    public int intField = 12345;
    
    public String getStringProperty() { return _stringProperty; }
    public void setStringProperty(String value) { _stringProperty = value; }
    
    public Object getNullProperty() { return _nullProperty; }
    public void setNullProperty(Object value) { _nullProperty = value; }
    
    public int[] getIntArrayProperty() { return _intArrayProperty; }
    public void setIntArrayProperty(int[] value) { _intArrayProperty = value; }
    
    public List<String> getStringListProperty() { return _stringListProperty; }
    public void setStringListProperty(List<String> value) { _stringListProperty = value; }
    
    public Map<String, Integer> getMapProperty() { return _mapProperty; }
    public void setMapProperty(Map<String, Integer> value) { _mapProperty = value; }
    
    public TestSubObject getSubObjectProperty() { return _subObjectProperty; }
    public void setSubObjectProperty(TestSubObject value) { _subObjectProperty = value; }

    public TestSubObject[] getSubArrayProperty() { return _subArrayProperty; }
    public void setSubArrayProperty(TestSubObject[] value) { _subArrayProperty = value; }
}
