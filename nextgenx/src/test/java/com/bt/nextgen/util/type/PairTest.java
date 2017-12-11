package com.bt.nextgen.util.type;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import com.bt.nextgen.core.type.Pair;

public class PairTest
{
	@Test
	public void testBasic()
	{
		com.bt.nextgen.core.type.Pair<String, Integer> pair = new com.bt.nextgen.core.type.Pair<String, Integer>("test",
			1);
		assertEquals(pair.getKey(), "test");
		assertEquals(pair.getValue(), new Integer(1));
	}

	@Test
	public void testSetters()
	{
		Pair<String, String> stringPair = new Pair<String, String>("key", "value");

		stringPair.setKey("newKey");
		stringPair.setValue("newValue");

		assertThat(stringPair.getKey(), is("newKey"));
		assertThat(stringPair.getValue(), is("newValue"));
		assertThat(stringPair.toString(), notNullValue());
	}

	@Test
	public void testEquals()
	{
		com.bt.nextgen.core.type.Pair<String, Integer> pair1 = new com.bt.nextgen.core.type.Pair<String, Integer>(
			"test", 1);
		com.bt.nextgen.core.type.Pair<String, Integer> pair2 = new com.bt.nextgen.core.type.Pair<String, Integer>(
			"test", 1);
		com.bt.nextgen.core.type.Pair<String, Integer> pair3 = new com.bt.nextgen.core.type.Pair<String, Integer>(
			"test", 2);
		assertThat(pair1, is(pair1));
		assertThat(pair1, is(pair2));
		assertThat(pair1, not(is(pair3)));
		assertThat(pair1.getKey(), is(pair2.getKey()));
		assertThat(pair1.getKey(), is(pair3.getKey()));
		assertThat(pair1.getValue(), is(pair2.getValue()));
		assertThat(pair1.getValue(), not(is(pair3.getValue())));
	}
}
