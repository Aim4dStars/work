package com.bt.nextgen.serviceops.model;

/**
 * Created by L091297 on 08/06/2017.
 */

public class SiloMovementStatusModel {

	private Long appId;

	private String userId;

	private String datetimeStart;

	private String datetimeEnd;

	private String oldCis;

	private String newCis;

	private String fromSilo;

	private String toSilo;

	private String lastSuccState;

	private String errState;

	private String errMsg;

	public Long getAppId() {
		return appId;
	}

	public void setAppId(Long appId) {
		this.appId = appId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getDatetimeStart() {
		return datetimeStart;
	}

	public void setDatetimeStart(String datetimeStart) {
		this.datetimeStart = datetimeStart;
	}

	public String getDatetimeEnd() {
		return datetimeEnd;
	}

	public void setDatetimeEnd(String datetimeEnd) {
		this.datetimeEnd = datetimeEnd;
	}

	public String getOldCis() {
		return oldCis;
	}

	public void setOldCis(String oldCis) {
		this.oldCis = oldCis;
	}

	public String getNewCis() {
		return newCis;
	}

	public void setNewCis(String newCis) {
		this.newCis = newCis;
	}

	public String getFromSilo() {
		return fromSilo;
	}

	public void setFromSilo(String fromSilo) {
		this.fromSilo = fromSilo;
	}

	public String getToSilo() {
		return toSilo;
	}

	public void setToSilo(String toSilo) {
		this.toSilo = toSilo;
	}

	public String getLastSuccState() {
		return lastSuccState;
	}

	public void setLastSuccState(String lastSuccState) {
		this.lastSuccState = lastSuccState;
	}

	public String getErrState() {
		return errState;
	}

	public void setErrState(String errState) {
		this.errState = errState;
	}

	public String getErrMsg() {
		return errMsg;
	}

	public void setErrMsg(String errMsg) {
		this.errMsg = errMsg;
	}

}
