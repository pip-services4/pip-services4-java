package org.pipservices4.data.random;

import static org.junit.Assert.*;

import org.junit.Test;

public class RandomFloatTest {
    @Test
    public void testNextFloat() {
    	float value = RandomFloat.nextFloat(5);
        assertTrue(value < 5);
        
    	value = RandomFloat.nextFloat(2,5);
    	assertTrue(value < 5 && value > 2);
    }
    
    @Test
    public void testUpdateFloat() {
    	float value = RandomFloat.updateFloat(0, 5);
        assertTrue(value <= 5 && value >= -5);       

        value = RandomFloat.updateFloat(5, 0);
        assertTrue(value >= 4.5 && value <= 5.5); 

        value = RandomFloat.updateFloat(0);
        assertTrue(value == 0); 
    }
}