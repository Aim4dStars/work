package com.bt.nextgen.api.morningstar.model;

public enum MorningstarDocumentType {
	PROSPECTUS("1"),
	SUPPLEMENT("2"),
	SAI("3"),
	ANNUAL_REPORT("4"),
	HALF_YEARLY_REPORT("5"),
	SUMMARY_PROSPECTUS("17"),
	SIMPLIFIED_PROSPECTUS("9"),
	FACTSHEET("52"),
	SUPPLEMENT_TO_SUMMARY_PROSPECTUS("60"),
	KIID("74"),
	KFS("76"),
	PHS("77");

	private String externalTypeId;

	MorningstarDocumentType(String externalTypeId) {
		this.externalTypeId = externalTypeId;
	}

	public static MorningstarDocumentType forTypeId(String id) {
		for (MorningstarDocumentType docType : MorningstarDocumentType.values()) {
			if (docType.externalTypeId.equals(id)) {
				return docType;
			}
		}

		return MorningstarDocumentType.PROSPECTUS;
	}

	public static MorningstarDocumentType forCode(String code) {
		for (MorningstarDocumentType docType : MorningstarDocumentType.values()) {
			if (docType.name().equals(code)) {
				return docType;
			}
		}

		return MorningstarDocumentType.PROSPECTUS;
	}

	public String getExternalTypeId() {
		return externalTypeId;
	}

	public String getCode() {
		return name();
	}
}
