package com.bt.nextgen.service.json;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;
@RunWith(MockitoJUnitRunner.class)
public class DefaultStreamStrategyTest {
	
	@InjectMocks
	DefaultStreamStrategy defaultStreamStrategy;
	
	@Test
	public void processNameTest() {
		String sample1=defaultStreamStrategy.processName("name_Value");
		String sample2=defaultStreamStrategy.processName("name_value");
		String sample3=defaultStreamStrategy.processName("nameValue");
		assertEquals("nameValue", sample1);
		assertEquals("nameValue", sample2);
		assertEquals("nameValue", sample3);
    }

	@Test
	public void processValueTest() {
		String sample=defaultStreamStrategy.processValue("Value");
		assertEquals("Value", sample);
    }

	@Test
    public void processNumberTest() {
		String sample=defaultStreamStrategy.processNumber("Number");
		assertEquals("Number", sample);
    }
	
}
