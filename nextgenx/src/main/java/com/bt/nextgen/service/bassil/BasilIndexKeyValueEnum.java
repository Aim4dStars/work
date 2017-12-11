package com.bt.nextgen.service.bassil;

public enum BasilIndexKeyValueEnum
{
InvestorNumber,
DocumentType,
BusinessLine("NEXTGEN"),
TrustType("CASH"),
EffectiveDate;

private String indexValue;

BasilIndexKeyValueEnum()
{
}

BasilIndexKeyValueEnum(String indexValue)
{
	this.indexValue = indexValue;
}

public String getIndexValue() {
	return indexValue;
}

}