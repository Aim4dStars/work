package com.bt.nextgen.core.type;

import com.btfin.panorama.core.util.StringUtil;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.core.IsNull.nullValue;

public class StringUtilTest
{

	@Test
	public void testAreEqualStrings() throws Exception
	{
		assertThat(StringUtil.areEqualStrings("australia", "australia"), is(true));
		assertThat(StringUtil.areEqualStrings("australia", "india"), is(false));
	}
	@Test
	public void testIsNumeric() throws Exception
	{
		assertThat(StringUtil.isNumeric("12345"), is(true));
		assertThat(StringUtil.isNumeric("hello12345"), is(false));
	}

	@Test
	public void testIsNotNullorEmpty() throws Exception
	{
		assertThat(StringUtil.isNotNullorEmpty("12345"), is(true));
		assertThat(StringUtil.isNotNullorEmpty(""), is(false));
		assertThat(StringUtil.isNotNullorEmpty(null), is(false));
	}

	@Test
	public void testAfterDecmialValue() throws Exception
	{
		assertThat(StringUtil.getAfterDecmialValue("200.50"), is(String.valueOf("50")));
	}

	@Test
	public void testBeforeDecinalValue() throws Exception
	{
		assertThat(StringUtil.getBeforeDecmialValue("200.50"), is(String.valueOf("200")));
	}

	@Test
	public void testToStringReturnNotNull() throws Exception
	{
		assertThat(StringUtil.toString(new String("Hello")), notNullValue());
	}

	@Test
	public void nullIfBlank() {
		assertThat(StringUtil.nullIfBlank(null), nullValue());
		assertThat(StringUtil.nullIfBlank("  "), nullValue());
		assertThat(StringUtil.nullIfBlank(" Not Blank! "), is(" Not Blank! "));
	}
}
