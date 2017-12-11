package com.bt.nextgen.core.repository;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class CorporateActionSavedParticipationKey implements Serializable {

	@Column(name = "OE_ID")
	private String oeId;

	@Column(name = "ORDER_NUMBER")
	private String orderNumber;

	public CorporateActionSavedParticipationKey() {
		// Empty constructor
	}

	public CorporateActionSavedParticipationKey(String oeId, String orderNumber) {
		this.oeId = oeId;
		this.orderNumber = orderNumber;
	}

	public String getOeId() {
		return oeId;
	}

	public void setOeId(String oeId) {
		this.oeId = oeId;
	}

	public String getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(String orderNumber) {
		this.orderNumber = orderNumber;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;

		result = prime * result + oeId.hashCode();
		result = prime * result + orderNumber.hashCode();

		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}

		CorporateActionSavedParticipationKey other = (CorporateActionSavedParticipationKey) obj;

		// Split tests into individual methods to pass Sonar
		return oeIdMatches(other) && orderNumberMatches(other);
	}

	private boolean oeIdMatches(CorporateActionSavedParticipationKey other) {
		if (oeId == null) {
			if (other.oeId != null) {
				return false;
			}
		} else if (!oeId.equals(other.oeId)) {
			return false;
		}

		return true;
	}

	private boolean orderNumberMatches(CorporateActionSavedParticipationKey other) {
		if (orderNumber == null) {
			if (other.orderNumber != null) {
				return false;
			}
		} else if (!orderNumber.equals(other.orderNumber)) {
			return false;
		}

		return true;
	}
}
