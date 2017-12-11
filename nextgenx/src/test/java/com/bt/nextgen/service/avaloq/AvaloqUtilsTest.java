package com.bt.nextgen.service.avaloq;

import com.avaloq.abs.bb.fld_def.Ctx;
import com.avaloq.abs.bb.fld_def.FldAnnot;
import org.hamcrest.core.Is;
import org.junit.Test;

import static org.junit.Assert.assertThat;

public class AvaloqUtilsTest
{
	@Test
	public void testasAvaloqId()
	{
		assertThat(AvaloqUtils.asAvaloqId(null).isEmpty(), Is.is(true));
		com.avaloq.abs.bb.fld_def.TextFld textField = new com.avaloq.abs.bb.fld_def.TextFld();
		FldAnnot fieldAnnot = new FldAnnot();
		Ctx ctx = new Ctx();
		ctx.setId("1234");
		fieldAnnot.setCtx(ctx);
		textField.setAnnot(fieldAnnot);
		assertThat(AvaloqUtils.asAvaloqId(textField), Is.is("1234"));

	}

	@Test
	public void testasAvaloqType()
	{
		assertThat(AvaloqUtils.asAvaloqType(null).isEmpty(), Is.is(true));
		com.avaloq.abs.bb.fld_def.TextFld textField = new com.avaloq.abs.bb.fld_def.TextFld();
		com.avaloq.abs.bb.fld_def.FldAnnot fieldAnnot = new com.avaloq.abs.bb.fld_def.FldAnnot();
		Ctx ctx = new Ctx();
		ctx.setId("avaloq");
		fieldAnnot.setCtx(ctx);
		textField.setAnnot(fieldAnnot);
		assertThat(AvaloqUtils.asAvaloqType(textField), Is.is("avaloq"));
	}
}
