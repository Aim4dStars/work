package com.bt.nextgen.core.web.binding;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import org.hamcrest.Matchers;
import org.junit.Test;

import com.bt.nextgen.payments.domain.PayeeType;

public class EnumPropertyEditorTest
{

	@Test
	public void testSetsValueOfEnum() throws Exception
	{
		PayeeType targetValue = PayeeType.BPAY;
		EnumPropertyEditor<PayeeType> editor = new EnumPropertyEditor<PayeeType>(PayeeType.class);
		editor.setAsText(PayeeType.BPAY.name());
		assertThat(editor.getValue(), equalTo((Object) targetValue));
	}

	@Test
	public void testSetsValueCaseInsensitive() throws Exception
	{
		PayeeType targetValue = PayeeType.BPAY;
		EnumPropertyEditor<PayeeType> editor = new EnumPropertyEditor<PayeeType>(PayeeType.class);
		editor.setAsText(PayeeType.BPAY.name().toLowerCase());
		assertThat(editor.getValue(), equalTo((Object) targetValue));
	}

	@Test
	public void testSetsValueIgnoreSpace() throws Exception
	{
		PayeeType targetValue = PayeeType.BPAY;
		EnumPropertyEditor<PayeeType> editor = new EnumPropertyEditor<PayeeType>(PayeeType.class);
		editor.setAsText(" " + PayeeType.BPAY.name()+ " ");
		assertThat(editor.getValue(), equalTo((Object) targetValue));
	}

	@Test
	public void testSetInvalidValueIsNull() throws Exception
	{
		PayeeType targetValue = PayeeType.BPAY;
		EnumPropertyEditor<PayeeType> editor = new EnumPropertyEditor<PayeeType>(PayeeType.class);
		editor.setAsText(" invalid ");
		assertThat(editor.getValue(), Matchers.nullValue());
	}


}
