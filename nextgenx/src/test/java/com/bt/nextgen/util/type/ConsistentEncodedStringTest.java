package com.bt.nextgen.util.type;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Assert;
import org.junit.Test;

import com.bt.nextgen.core.type.ConsistentEncodedString;

public class ConsistentEncodedStringTest
{
	@Test
	public void testPlainText() throws Exception
	{
		assertThat(ConsistentEncodedString.fromPlainText("james").plainText(), is("james"));
	}

	@Test
	public void testToPlainText() throws Exception
	{
		ConsistentEncodedString james = ConsistentEncodedString.fromPlainText("james");

		assertThat(james.plainText(), is("james"));
	}
	
	@Test
	public void testFromPlainTextToConsistentCode() throws Exception
	{
		ConsistentEncodedString code = ConsistentEncodedString.fromPlainText("55555");
		assertThat(ConsistentEncodedString.fromPlainText("55555"), is(code));
	}
	
	@Test
	public void testToPlainTextFromConsistentCode() throws Exception
	{
		String decryptValue = ConsistentEncodedString.toPlainText("D671036F60C34DDD");
		assertThat(decryptValue, is("55555"));
	}
	
	@Test
	public void testFromPlainTextToConsistentCodeMatch() throws Exception
	{
		String code1 = ConsistentEncodedString.fromPlainText("55555").toString();
		String code2 = ConsistentEncodedString.fromPlainText("55555").toString();
		Assert.assertEquals(code1, code2);
	}

	@Test
	public void testTransitiveEquals() throws Exception
	{
		final String name = "james";
		assertThat(ConsistentEncodedString.fromPlainText(name).plainText(), is(name));
		assertThat(new ConsistentEncodedString(ConsistentEncodedString.fromPlainText(name).toString()).plainText(), is(name));
	}

	@Test
	public void testEquals() throws Exception
	{
		final ConsistentEncodedString JAMES = ConsistentEncodedString.fromPlainText("james");
		final ConsistentEncodedString JAMESPrime = ConsistentEncodedString.fromPlainText("james");
		assertThat(JAMESPrime, is(JAMES));
	}

	@Test
	public void testHashCode() throws Exception
	{
		final ConsistentEncodedString JAMES = ConsistentEncodedString.fromPlainText("james");
		final ConsistentEncodedString JAMESPrime = ConsistentEncodedString.fromPlainText("james");
		assertThat(JAMES.hashCode(), is(JAMESPrime.hashCode()));
	}

	@Test
	public void testFromPlainText() throws Exception
	{
		final ConsistentEncodedString JAMES = ConsistentEncodedString.fromPlainText("james");
		assertThat(JAMES.plainText(), is("james"));
	}
}
