package com.bt.nextgen.service.integration.registration.model;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "USER_ROLE_TNC")
@SuppressWarnings({"findbugs:EI_EXPOSE_REP2"})
public class UserRoleTermsAndConditions implements Serializable
{
	@EmbeddedId
	private UserRoleTermsAndConditionsKey userRoleTermsAndConditionsKey;

	@Column(name = "TNC_ACCEPTED")
	private String tncAccepted = "0";

	@Column(name = "TNC_ACCEPTED_ON")
	private Date tncAcceptedOn;

	@Column(name = "MODIFY_DATETIME")
	private Date modifyDatetime;

	@Column(name = "VERSION")
	private Integer version;

	public UserRoleTermsAndConditionsKey getUserRoleTermsAndConditionsKey() {
		return userRoleTermsAndConditionsKey;
	}

	public void setUserRoleTermsAndConditionsKey(UserRoleTermsAndConditionsKey userRoleTermsAndConditionsKey) {
		this.userRoleTermsAndConditionsKey = userRoleTermsAndConditionsKey;
	}

	public String getTncAccepted() {
		return tncAccepted;
	}

	public void setTncAccepted(String tncAccepted) {
		this.tncAccepted = tncAccepted;
	}

	public Date getTncAcceptedOn()
	{
		return tncAcceptedOn != null ? new Date(tncAcceptedOn.getTime()) : null;
	}

	public void setTncAcceptedOn(Date tncAcceptedOn) {
		this.tncAcceptedOn = tncAcceptedOn;
	}

	public Date getModifyDatetime()
	{
		return modifyDatetime != null ? new Date(modifyDatetime.getTime()) : null;
	}

	public void setModifyDatetime(Date modifyDatetime) {
		this.modifyDatetime = modifyDatetime;
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}
}