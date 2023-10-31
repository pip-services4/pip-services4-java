package org.pipservices4.commons.reflect;

import java.time.*;

public class TestClass {
	@SuppressWarnings("unused")
	private int privateField = 123;
	public String publicField = "ABC";
	private ZonedDateTime _publicProp = ZonedDateTime.now();
	
	public TestClass() {}
	
	public TestClass(int arg1) {}
	
	protected long getPrivateProp() { return 543; }
	protected void setPrivateProp(long value) {}
	
	public ZonedDateTime getPublicProp() { return _publicProp; }
	public void setPublicProp(ZonedDateTime value) { _publicProp = value; }
	
	private TestNestedClass NestedProperty = new TestNestedClass();
	
	public TestNestedClass getNestedProperty() {return NestedProperty;}
	public void setNestedProperty(TestNestedClass nestedProperty) {	NestedProperty = nestedProperty;}
	
	@SuppressWarnings("unused")
	private void privateMethod() {}
	
	public int publicMethod(int arg1, int arg2) {
		return arg1 + arg2;
	}
	
	 public class TestNestedClass
	    {
	        private int intProperty;

			public int getIntProperty() {return intProperty;}
			public void setIntProperty(int intProperty) {this.intProperty = intProperty;}	        
	    }
}
