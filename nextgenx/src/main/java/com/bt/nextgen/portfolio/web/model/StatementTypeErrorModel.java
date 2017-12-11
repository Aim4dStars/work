package com.bt.nextgen.portfolio.web.model;

@Deprecated
public class StatementTypeErrorModel {
	private String subCode;
	private String description;
	private String reason;
	public String getSubCode() {
		return subCode;
	}
	public void setSubCode(String subCode) {
		this.subCode = subCode;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
}
