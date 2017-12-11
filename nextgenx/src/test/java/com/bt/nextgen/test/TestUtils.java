package com.bt.nextgen.test;

import java.math.BigDecimal;

import com.avaloq.abs.bb.fld_def.Ctx;
import com.avaloq.abs.bb.fld_def.FldAnnot;
import com.avaloq.abs.bb.fld_def.NrFld;
import com.avaloq.abs.bb.fld_def.TextFld;

public class TestUtils
{

	public static TextFld setCtxId(String value)
	{
		TextFld textField = new TextFld();
		FldAnnot fieldAnnot = new FldAnnot();
		Ctx ctx = new Ctx();
		ctx.setId(value);
		fieldAnnot.setCtx(ctx);
		textField.setAnnot(fieldAnnot);
		textField.setVal(value);

		return textField;
	}

	public static TextFld getTextField(String value)
	{
		TextFld textField = new TextFld();
		textField.setVal(value);
		return textField;
	}

	public static NrFld getNumberField(String value)
	{
		NrFld numberField = new NrFld();
		numberField.setVal(new BigDecimal(value));
		return numberField;
	}

	public static NrFld getNumberValue(String value)
	{
		NrFld numberVal = new NrFld();
		numberVal.setVal(new BigDecimal(value));
		return numberVal;
	}
}
