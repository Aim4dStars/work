package com.bt.nextgen.serviceops.model;

public class MaintainIpToIpRelationshipReqModel {
	private String useCase;

    private String sourcePersonType;

    private String sourceCISKey;
    
    private String partyRelType;

    private String silo;

    private String targetPersonType;
    
    private String targetCISKey;

    private String partyRelStatus;

    private String partyRelStartDate;

    private String partyRelEndDate;

    private String partyRelModNum;
    
    private String versionNumber;

	public String getVersionNumber() {
		return versionNumber;
	}

	public void setVersionNumber(String versionNumber) {
		this.versionNumber = versionNumber;
	}

	public String getUseCase() {
		return useCase;
	}

	public void setUseCase(String useCase) {
		this.useCase = useCase;
	}

	public String getSourcePersonType() {
		return sourcePersonType;
	}

	public void setSourcePersonType(String sourcePersonType) {
		this.sourcePersonType = sourcePersonType;
	}

	public String getSourceCISKey() {
		return sourceCISKey;
	}

	public void setSourceCISKey(String sourceCISKey) {
		this.sourceCISKey = sourceCISKey;
	}

	public String getPartyRelType() {
		return partyRelType;
	}

	public void setPartyRelType(String partyRelType) {
		this.partyRelType = partyRelType;
	}

	public String getSilo() {
		return silo;
	}

	public void setSilo(String silo) {
		this.silo = silo;
	}

	public String getTargetPersonType() {
		return targetPersonType;
	}

	public void setTargetPersonType(String targetPersonType) {
		this.targetPersonType = targetPersonType;
	}

	public String getTargetCISKey() {
		return targetCISKey;
	}

	public void setTargetCISKey(String targetCISKey) {
		this.targetCISKey = targetCISKey;
	}

	public String getPartyRelStatus() {
		return partyRelStatus;
	}

	public void setPartyRelStatus(String partyRelStatus) {
		this.partyRelStatus = partyRelStatus;
	}

	public String getPartyRelStartDate() {
		return partyRelStartDate;
	}

	public void setPartyRelStartDate(String partyRelStartDate) {
		this.partyRelStartDate = partyRelStartDate;
	}

	public String getPartyRelEndDate() {
		return partyRelEndDate;
	}

	public void setPartyRelEndDate(String partyRelEndDate) {
		this.partyRelEndDate = partyRelEndDate;
	}

	public String getPartyRelModNum() {
		return partyRelModNum;
	}

	public void setPartyRelModNum(String partyRelModNum) {
		this.partyRelModNum = partyRelModNum;
	}
	
}
