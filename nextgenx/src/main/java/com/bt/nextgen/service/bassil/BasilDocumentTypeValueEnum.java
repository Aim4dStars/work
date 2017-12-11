package com.bt.nextgen.service.bassil;

import com.bt.nextgen.web.controller.cash.util.Attribute;

public enum BasilDocumentTypeValueEnum
{
	//TODO Clarify the value for Quarterly Payg Statements is PAYGSTM or PYGSTM.for now keeping it as it is.
	STMQTR(Attribute.QRTLY_STMTS), QTRSTM(Attribute.QRTLY_STMTS), PYGSTM(Attribute.QRTLY_PAYG_STMTS), STPAYG(
		Attribute.QRTLY_PAYG_STMTS), EMAILF(Attribute.FAILURE_NOTIFICATION), EXTCLO(Attribute.EXIT_STMTS), EXTSTM(
		Attribute.EXIT_STMTS), STMANN(Attribute.ANNUAL_INVESTOR_STMT), STMTAX(Attribute.ANNUAL_TAX_STMT);

	private final String documentType;

	BasilDocumentTypeValueEnum(String documentType)
	{
		this.documentType = documentType;
	}


	public String getDocumentType() {
		return documentType;
	}
}
