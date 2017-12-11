package com.bt.nextgen.serviceops.repository;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * Created by l069679 on 9/02/2017.
 */
@Entity
@Table(name = "SILO_MOVEMENT_STATUS")
public class SiloMovementStatus implements Serializable {

	@Column(name = "ID")
	@SequenceGenerator(name = "SILO_MOVEMENT_STATUS_SEQ", sequenceName = "SILO_MOVEMENT_STATUS_SEQ", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SILO_MOVEMENT_STATUS_SEQ")
	@Id
	private Long id;

	@Column(name = "APP_ID")
	private String appId;

	@Column(name = "USER_ID")
	private String userId;

	@Column(name = "DATETIME_START")
	@Temporal(TemporalType.TIMESTAMP)
	private Date datetimeStart;

	@Column(name = "DATETIME_END")
	@Temporal(TemporalType.TIMESTAMP)
	private Date datetimeEnd;

	@Column(name = "OLD_CIS")
	private String oldCis;

	@Column(name = "NEW_CIS")
	private String newCis;

	@Column(name = "FROM_SILO")
	private String fromSilo;

	@Column(name = "TO_SILO")
	private String toSilo;

	@Column(name = "LAST_SUCC_STATE")
	private String lastSuccState;

	@Column(name = "ERR_STATE")
	private String errState;

	@Column(name = "ERR_MSG")
	private String errMsg;

	public SiloMovementStatus() {
		// Default constructor for GcmOpsAuditTrail.
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public Date getDatetimeStart() {
		return datetimeStart;
	}

	public void setDatetimeStart(Date datetimeStart) {
		this.datetimeStart = datetimeStart;
	}

	public Date getDatetimeEnd() {
		return datetimeEnd;
	}

	public void setDatetimeEnd(Date datetimeEnd) {
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
